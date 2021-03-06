package com.ljh.mytodoapp_rxjava.task;

import android.support.annotation.NonNull;

import com.ljh.mytodoapp_rxjava.data.Task;
import com.ljh.mytodoapp_rxjava.data.source.TasksRepository;
import com.ljh.mytodoapp_rxjava.utils.schedulers.BaseSchedulerProvider;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author Administrator
 * @date 2018/8/12
 */
public class TasksPresenter implements TasksContract.Presenter {

    @NonNull
    private final TasksRepository mTasksRepository;

    @NonNull
    private final TasksContract.View mTasksView;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @NonNull
    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    //可以快速解除所有添加的Disposable类
    @NonNull
    private CompositeDisposable mCompositeDisposable;

    public TasksPresenter(@NonNull TasksRepository mTasksRepository, @NonNull TasksContract.View mTasksView, @NonNull BaseSchedulerProvider mSchedulerProvider) {
        this.mTasksRepository = mTasksRepository;
        this.mTasksView = mTasksView;
        this.mSchedulerProvider = mSchedulerProvider;

        mCompositeDisposable = new CompositeDisposable();
        mTasksView.setPresenter(this);
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void loadTasks() {
        //清除所有disposable
        mCompositeDisposable.clear();
        Disposable disposable = mTasksRepository
                .getTasks()
                .flatMap(Flowable::fromIterable)
                .filter(task -> {
                    switch (mCurrentFiltering) {
                        case ACTIVE_TASKS:
                            return !task.getCompleted();
                        case COMPLETED_TASKS:
                            return task.getCompleted();
                        case ALL_TASKS:
                        default:
                            return true;
                    }
                })
                .toList()
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(
                        //onNext
                        tasks -> {
                            processTasks(tasks);
//                            mTasksView.setLoadingIndicator(false);
                        },
                        //onError
                        throwable -> mTasksView.showLoadingTasksError());

        mCompositeDisposable.add(disposable);
    }


    private void processTasks(List<Task> tasks) {
        if(tasks.isEmpty()) {
            processEmptyTasks();
        }else {
            //显示task list
            mTasksView.showTasks(tasks);
            //set the filter laber's text
            showFilterLabel();
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                mTasksView.showNoCompletedTasks();
                break;
            default:
                mTasksView.showNoTasks();
                break;
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                mTasksView.showCompletedFilterLabel();
                break;
            default:
                mTasksView.showAllFilterLabel();
                break;
        }
    }

    @Override
    public void addNewTask() {
        mTasksView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestTask) {
        mTasksView.showTaskDetailUi(requestTask.getId());
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        mTasksRepository.completeTask(completedTask);
        mTasksView.showTaskMarkedComplete();
        loadTasks();
    }

    @Override
    public void activateTask(@NonNull Task activeTask) {
        mTasksRepository.activateTask(activeTask);
        mTasksView.showTaskMarkedActive();
        loadTasks();
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRepository.clearCompletedTasks();
        mTasksView.showCompletedTaskCleared();
        loadTasks();
    }

    @Override
    public void setFiltering(TasksFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }

    @Override
    public void subscribe() {
        loadTasks();
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }
}
