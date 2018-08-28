package com.ljh.mytodoapp_rxjava.data.source;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.ljh.mytodoapp_rxjava.data.Task;

import org.litepal.LitePal;
import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * @author Administrator
 * @date 2018/8/10
 */
public class TasksLocalDataSource implements TasksDataSource {
    @Nullable
    private static TasksLocalDataSource INSTANCE;

    public static TasksLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TasksLocalDataSource();
        }
        return INSTANCE;
    }

    private TasksLocalDataSource() {
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Flowable<List<Task>> getTasks() {
        List<Task> tasks = LitePal.findAll(Task.class);
        return Flowable
                .fromIterable(tasks)
                .toList()
                .toFlowable();
    }

    @Override
    public Flowable<Optional<Task>> getTask(@NonNull int taskId) {
        Task task = LitePal.find(Task.class, taskId);
        if (task != null) {
            return Flowable.just(Optional.of(task));
        } else
            return Flowable.empty();
    }

    @Override
    public void saveTask(@Nonnull Task task) {
        task.save();
    }

    @Override
    public void updateTask(@NonNull Task task) {
        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        values.put("completed",task.getCompleted());
        values.put("description", task.getDescription());
        LitePal.update(Task.class, values, task.getId());
    }

    @Override
    public void completeTask(@Nonnull Task task) {
        completeTask(task.getId());
    }

    @Override
    public void completeTask(@Nonnull int taskId) {
        ContentValues values = new ContentValues();
        values.put("completed", true);
        LitePal.update(Task.class, values, taskId);
    }

    @Override
    public void activateTask(@Nonnull Task task) {
        activateTask(task.getId());
    }

    @Override
    public void activateTask(@Nonnull int taskId) {
        ContentValues values = new ContentValues();
        values.put("completed", false);
        LitePal.update(Task.class, values, taskId);
    }

    @Override
    public void clearCompletedTasks() {
        LitePal.deleteAll(Task.class, "completed = 1");
    }

    @Override
    public void deleteAllTasks() {
        LitePal.deleteAll(Task.class);
    }

    @Override
    public void deleteTask(@Nonnull int taskId) {
        LitePal.delete(Task.class, taskId);
    }
}
