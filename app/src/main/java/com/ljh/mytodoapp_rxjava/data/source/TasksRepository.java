package com.ljh.mytodoapp_rxjava.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Optional;
import com.ljh.mytodoapp_rxjava.data.Task;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import io.reactivex.Flowable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Administrator
 * @date 2018/8/11
 */
public class TasksRepository implements TasksDataSource {
    @Nullable
    private static TasksRepository INSTANCE = null;

    @Nonnull
    private final TasksDataSource mTasksLocalDataSource;

    @Nonnull
    private final TasksDataSource mTasksRemoteDataSource;

    @Nullable
    Map<String, Task> mCachedTasks;

    boolean mCacheIsDirty = false;

    private TasksRepository(@Nonnull TasksDataSource mTasksLocalDataSource, @Nonnull TasksDataSource mTasksRemoteDateSource) {
        this.mTasksLocalDataSource = mTasksLocalDataSource;
        this.mTasksRemoteDataSource = mTasksRemoteDateSource;
    }

    public static TasksRepository getInstance(@Nonnull TasksDataSource mTasksLocalDataSource, @Nonnull TasksDataSource mTasksRemoteDateSource){
        if(INSTANCE == null) {
            INSTANCE = new TasksRepository(mTasksLocalDataSource, mTasksRemoteDateSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public Flowable<List<Task>> getTasks() {
        if(mCachedTasks != null && !mCacheIsDirty) {
            return Flowable.fromIterable(mCachedTasks.values()).toList().toFlowable();
        }else if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        Flowable<List<Task>> remoteTasks = getAndSaveRemoteTasks();

        if(mCacheIsDirty) {
            return remoteTasks;
        }else {
            Flowable<List<Task>> loadTasks = getAndCacheLocalTasks();
            return Flowable.concat(loadTasks, remoteTasks)
                    .filter(tasks -> tasks.isEmpty())
                    .firstOrError()
                    .toFlowable();
        }
    }

    private Flowable<List<Task>> getAndCacheLocalTasks() {
        return mTasksLocalDataSource.getTasks()
                .flatMap(tasks -> Flowable.fromIterable(tasks)
                    .doOnNext(task -> mCachedTasks.put(task.getMId(), task))
                    .toList()
                    .toFlowable());
    }

    private Flowable<List<Task>> getAndSaveRemoteTasks() {
        return mTasksRemoteDataSource
                .getTasks()
                .flatMap(tasks -> Flowable.fromIterable(tasks).doOnNext(task -> {    //使用lambda表达式
                    mTasksLocalDataSource.saveTask(task);
                    mCachedTasks.put(task.getMId(), task);
                }).toList().toFlowable())
                .doOnComplete(() -> mCacheIsDirty = false);
    }

    @Override
    public Flowable<Optional<Task>> getTask(@NonNull String taskId) {
        Task cacheTask = getTaskWithId(taskId);

        if(cacheTask != null) {
            return Flowable.just(Optional.of(cacheTask));
        }

        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        Flowable<Optional<Task>> localTask = getTaskWithIdFromLocalRepository(taskId);
        Flowable<Optional<Task>> remoteTask = mTasksRemoteDataSource
                .getTask(taskId)
                .doOnNext(taskOptional -> {
                    if(taskOptional.isPresent()) {
                        Task task = taskOptional.get();
                        mTasksLocalDataSource.saveTask(task);
                        mCachedTasks.put(task.getMId(), task);
                    }
                });

        return Flowable.concat(localTask,remoteTask)
                .firstElement()
                .toFlowable();
    }

    private Flowable<Optional<Task>> getTaskWithIdFromLocalRepository(String taskId) {
        return mTasksLocalDataSource
                .getTask(taskId)
                .doOnNext(taskOptional -> {
                    if(taskOptional.isPresent()) {
                        mCachedTasks.put(taskId,taskOptional.get());
                    }
                })
                .firstElement()
                .toFlowable();
    }

    @Override
    public void saveTask(@Nonnull Task task) {
        mTasksLocalDataSource.saveTask(task);
        mTasksRemoteDataSource.saveTask(task);

        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getMId(), task);
    }

    @Override
    public void completeTask(@Nonnull Task task) {
        mTasksRemoteDataSource.completeTask(task);
        mTasksLocalDataSource.completeTask(task);

        Task completedTask = new Task(task.getMId(),task.getTitle(), task.getDescription(),true);

        //更新缓存
        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getMId(), completedTask);
    }

    @Override
    public void completeTask(@Nonnull String taskId) {
        Task taskWithId = getTaskWithId(taskId);
        if (taskWithId != null) {
            completeTask(taskWithId);
        }
    }

    private Task getTaskWithId(String taskId) {
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(taskId);
        }
    }

    @Override
    public void activateTask(@Nonnull Task task) {
        mTasksRemoteDataSource.activateTask(task);
        mTasksLocalDataSource.activateTask(task);

        Task activateTask = new Task(task.getMId(), task.getTitle(), task.getDescription());

        //更新缓存
        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getMId(), activateTask);
    }

    @Override
    public void activateTask(@Nonnull String taskId) {
        Task taskWithId = getTaskWithId(taskId);
        if (taskWithId != null) {
            activateTask(taskWithId);
        }
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRemoteDataSource.clearCompletedTasks();
        mTasksLocalDataSource.clearCompletedTasks();

        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Task>> iterator = mCachedTasks.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Task> entry = iterator.next();
            if(entry.getValue().isCompleted()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();

        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    @Override
    public void deleteTask(@Nonnull String taskId) {
        mTasksRemoteDataSource.deleteTask(checkNotNull(taskId));
        mTasksLocalDataSource.deleteTask(checkNotNull(taskId));

        mCachedTasks.remove(taskId);
    }
}
