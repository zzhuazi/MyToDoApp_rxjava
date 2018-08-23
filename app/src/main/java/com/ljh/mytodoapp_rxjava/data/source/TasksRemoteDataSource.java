package com.ljh.mytodoapp_rxjava.data.source;

import android.support.annotation.NonNull;

import com.google.common.base.Optional;
import com.ljh.mytodoapp_rxjava.data.Task;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import io.reactivex.Flowable;

/**
 * @author Administrator
 * @date 2018/8/11
 */
public class TasksRemoteDataSource implements TasksDataSource {
    private final static Map<String, Task> TASKS_SERVICE_DATA;
    private static TasksRemoteDataSource INSTANCE;

    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.");
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!");
    }

    private TasksRemoteDataSource() {
    }

    private static void addTask(String title, String description) {
        Task newTask = new Task(title, description);
        TASKS_SERVICE_DATA.put(newTask.getMId(), newTask);
    }

    public static TasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }
        return INSTANCE;
    }

    ;

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Flowable<List<Task>> getTasks() {
        return Flowable
                .fromIterable(TASKS_SERVICE_DATA.values())
                .toList()
                .toFlowable();
    }

    @Override
    public Flowable<Optional<Task>> getTask(@NonNull String taskId) {
        Task task = TASKS_SERVICE_DATA.get(taskId);
        if (task != null) {
            return Flowable.just(Optional.of(task));
        } else {
            return Flowable.empty();
        }
    }

    @Override
    public void saveTask(@Nonnull Task task) {
        TASKS_SERVICE_DATA.put(task.getMId(), task);
    }

    @Override
    public void completeTask(@Nonnull Task task) {
        Task completedTask = new Task(task.getMId(), task.getTitle(), task.getDescription(), true);
        TASKS_SERVICE_DATA.put(task.getMId(), completedTask);
    }

    @Override
    public void completeTask(@Nonnull String taskId) {
        //在repository中处理
    }

    @Override
    public void activateTask(@Nonnull Task task) {
        Task activateTask = new Task(task.getMId(), task.getTitle(), task.getDescription());
        TASKS_SERVICE_DATA.put(task.getMId(), activateTask);
    }

    @Override
    public void activateTask(@Nonnull String taskId) {
        //在repository中处理
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> iterator = TASKS_SERVICE_DATA.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Task> next = iterator.next();
            if (next.getValue().isCompleted()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        //在repository中处理逻辑
    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteTask(@Nonnull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }
}
