package com.ljh.mytodoapp_rxjava.statistics;

import com.ljh.mytodoapp_rxjava.BasePresenter;
import com.ljh.mytodoapp_rxjava.BaseView;

/**
 * @author Administrator
 * @date 2018/8/13
 */
public interface StatisticContract {

    interface View extends BaseView<Presenter> {
        void setProgressIndicator(boolean active);

        void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks);

        void showLoadingStatisticsError();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

    }
}
