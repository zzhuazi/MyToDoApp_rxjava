package com.ljh.mytodoapp_rxjava.data.source;

import android.support.annotation.NonNull;

import com.google.common.base.Optional;
import com.ljh.mytodoapp_rxjava.data.Task;

import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.Flowable;

/**
 * @author Administrator
 * @date 2018/8/10
 */
public interface TasksDataSource {

    Flowable<List<Task>> getTasks();

    Flowable<Optional<Task>> getTask(@NonNull int taskId);

    void saveTask(@Nonnull Task task);

    void updateTask(@NonNull Task task);

    void completeTask(@Nonnull Task task);

    void completeTask(@Nonnull int taskId);

    void activateTask(@Nonnull Task task);

    void activateTask(@Nonnull int taskId);

    void clearCompletedTasks();

    void deleteAllTasks();

    void deleteTask(@Nonnull int taskId);
}
