package com.ljh.mytodoapp_rxjava.addedittask;

import com.ljh.mytodoapp_rxjava.BasePresenter;
import com.ljh.mytodoapp_rxjava.BaseView;

/**
 * @author Administrator
 * @date 2018/8/13
 */
public interface AddEditTaskContract {

    interface View extends BaseView<Presenter>{
        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter{
        void saveTask(String title, String description);

        void populateTask();

        boolean isDataMissing();
    }
}
