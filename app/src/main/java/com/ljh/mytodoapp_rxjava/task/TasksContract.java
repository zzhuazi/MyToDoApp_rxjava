package com.ljh.mytodoapp_rxjava.task;

import android.support.annotation.NonNull;

import com.ljh.mytodoapp_rxjava.BasePresenter;
import com.ljh.mytodoapp_rxjava.BaseView;
import com.ljh.mytodoapp_rxjava.data.Task;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * @author Administrator
 * @date 2018/8/12
 */
public interface TasksContract {
    interface View extends BaseView<Presenter>{
//        void setLoadingIndicator(boolean active);

        void showTasks(List<Task> taskList);

        void showAddTask();

        void showTaskDetailUi(String taskId);

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        void showCompletedTaskCleared();

        void showLoadingTasksError();

        void showNoTasks();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showFilteringPopUpMenu();
    }

    interface Presenter extends BasePresenter{
        void result(int requestCode, int resultCode);

        void loadTasks();

        void addNewTask();

        void openTaskDetails(@NonNull Task requestTask);

        void completeTask(@NonNull Task completedTask);

        void activateTask(@NonNull Task activeTask);

        void clearCompletedTasks();

        void setFiltering(TasksFilterType requestType);

        TasksFilterType getFiltering();
    }
}