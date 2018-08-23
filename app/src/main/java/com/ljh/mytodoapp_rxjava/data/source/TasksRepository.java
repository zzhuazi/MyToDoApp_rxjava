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

    private TasksRepository(@Nonnull TasksDataSource mTasksLocalDataSource) {
        this.mTasksLocalDataSource = mTasksLocalDataSource;
    }

    public static TasksRepository getInstance(@Nonnull TasksDataSource mTasksLocalDataSource){
        if(INSTANCE == null) {
            INSTANCE = new TasksRepository(mTasksLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public Flowable<List<Task>> getTasks() {
        return mTasksLocalDataSource.getTasks()
                .flatMap(tasks -> Flowable.fromIterable(tasks))
                .toList()
                .toFlowable();

    }

    @Override
    public Flowable<Optional<Task>> getTask(@NonNull String taskId) {
        return mTasksLocalDataSource.getTask(taskId);
    }


    @Override
    public void saveTask(@Nonnull Task task) {
        mTasksLocalDataSource.saveTask(task);
    }

    @Override
    public void completeTask(@Nonnull Task task) {
        mTasksLocalDataSource.completeTask(task);
    }

    @Override
    public void completeTask(@Nonnull String taskId) {
        mTasksLocalDataSource.completeTask(taskId);
    }


    @Override
    public void activateTask(@Nonnull Task task) {
        mTasksLocalDataSource.activateTask(task);
    }

    @Override
    public void activateTask(@Nonnull String taskId) {
        mTasksLocalDataSource.activateTask(taskId);
    }

    @Override
    public void clearCompletedTasks() {
        mTasksLocalDataSource.clearCompletedTasks();

    }

    @Override
    public void deleteAllTasks() {
        mTasksLocalDataSource.deleteAllTasks();
    }

    @Override
    public void deleteTask(@Nonnull String taskId) {
        mTasksLocalDataSource.deleteTask(checkNotNull(taskId));
    }
}
