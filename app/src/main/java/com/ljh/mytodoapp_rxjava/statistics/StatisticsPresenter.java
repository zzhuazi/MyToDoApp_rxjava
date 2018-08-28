package com.ljh.mytodoapp_rxjava.statistics;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.common.primitives.Ints;
import com.ljh.mytodoapp_rxjava.data.Task;
import com.ljh.mytodoapp_rxjava.data.source.TasksRepository;
import com.ljh.mytodoapp_rxjava.utils.schedulers.BaseSchedulerProvider;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;

/**
 * @author Administrator
 * @date 2018/8/13
 */
public class StatisticsPresenter implements StatisticContract.Presenter {

    @NonNull
    private final TasksRepository mTasksRepository;

    @NonNull
    private final StatisticContract.View mStatisticsView;

    @NonNull
    private final BaseSchedulerProvider mSchedularProvider;

    @NonNull
    private CompositeDisposable mCompositeDisposable;

    public StatisticsPresenter(@NonNull TasksRepository mTasksRepository, @NonNull StatisticContract.View mStatisticsView, @NonNull BaseSchedulerProvider mSchedularProvider) {
        this.mTasksRepository = mTasksRepository;
        this.mStatisticsView = mStatisticsView;
        this.mSchedularProvider = mSchedularProvider;

        mCompositeDisposable = new CompositeDisposable();
        mStatisticsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadStatistics();
    }

    private void loadStatistics() {
        mStatisticsView.setProgressIndicator(true);

        Flowable<Task> tasks = mTasksRepository
                .getTasks()
                .flatMap(Flowable::fromIterable);
        Flowable<Long> completedTasks = tasks.filter(Task::getCompleted).count().toFlowable();
        Flowable<Long> activeTasks = tasks.filter(new Predicate<Task>() {
            @Override
            public boolean test(Task task) throws Exception {
                return !task.getCompleted();
            }
        }).count().toFlowable();
        Disposable disposable = Flowable
                .zip(completedTasks, activeTasks, (completed, active) -> Pair.create(active, completed))
                .subscribeOn(mSchedularProvider.computation())
                .observeOn(mSchedularProvider.ui())
                .subscribe(
                        // onNext
                        stats -> mStatisticsView.showStatistics(Ints.saturatedCast(stats.first), Ints.saturatedCast(stats.second)),
                        // onError
                        throwable -> mStatisticsView.showLoadingStatisticsError(),
                        // onCompleted
                        () -> mStatisticsView.setProgressIndicator(false));
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }
}
