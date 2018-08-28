package com.ljh.mytodoapp_rxjava.taskdetail;

import com.ljh.mytodoapp_rxjava.BasePresenter;
import com.ljh.mytodoapp_rxjava.BaseView;

/**
 * @author Administrator
 * @date 2018/8/13
 */
public interface TaskDetailContract {
    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showMissingTask();

        void hideTitle();

        void showTitle(String title);

        void hideDescription();

        void showDescription(String description);

        void showCompletionStatus(boolean complete);

        void showEditTask(int taskId);

        void showTaskDeleted();

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        boolean isActive();
    }

    interface Presenter extends BasePresenter{
        void editTask();

        void deleteTask();

        void completeTask();

        void activateTask();
    }
}
