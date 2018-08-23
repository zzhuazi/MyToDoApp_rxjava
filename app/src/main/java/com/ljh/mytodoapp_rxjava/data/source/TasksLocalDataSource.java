package com.ljh.mytodoapp_rxjava.data.source;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
    public Flowable<Optional<Task>> getTask(@NonNull String taskId) {
        List<Task> tasks = LitePal.where("mid = ?", taskId).find(Task.class);
        if (tasks.size() > 0) {
            return Flowable.just(Optional.of(tasks.get(0)));
        } else
            return Flowable.empty();
    }

    @Override
    public void saveTask(@Nonnull Task task) {
        List<Task> tasks = LitePal.where("mid = ?", task.getMId()).find(Task.class);
        if(tasks.size() > 0) {
            return;
        }
        task.save();
    }

    @Override
    public void completeTask(@Nonnull Task task) {
        completeTask(task.getMId());
    }

    @Override
    public void completeTask(@Nonnull String taskId) {
        ContentValues values = new ContentValues();
        values.put("completed", true);
        LitePal.updateAll(Task.class, values, "mid = ?", taskId);
    }

    @Override
    public void activateTask(@Nonnull Task task) {
        activateTask(task.getMId());
    }

    @Override
    public void activateTask(@Nonnull String taskId) {
        ContentValues values = new ContentValues();
        values.put("completed", false);
        LitePal.updateAll(Task.class, values, "mid = ?", taskId);
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
    public void deleteTask(@Nonnull String taskId) {
        LitePal.deleteAll(Task.class, "mid = ?",taskId);
    }
}
