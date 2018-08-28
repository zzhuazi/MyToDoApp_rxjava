package com.ljh.mytodoapp_rxjava.taskdetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.ljh.mytodoapp_rxjava.data.Task;
import com.ljh.mytodoapp_rxjava.data.source.TasksRepository;
import com.ljh.mytodoapp_rxjava.utils.schedulers.BaseSchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Administrator
 * @date 2018/8/13
 */
public class TaskDetailPresenter implements TaskDetailContract.Presenter {
    @NonNull
    private final TasksRepository mTasksRepository;

    @NonNull
    private final TaskDetailContract.View mTaskDetailView;

    @NonNull
    private final BaseSchedulerProvider mSchedularProvider;

    @Nullable
    private int mTaskId;

    @NonNull
    private CompositeDisposable mCompositeDisposable;

    public TaskDetailPresenter(int mTaskId,
                               @NonNull TasksRepository mTasksRepository,
                               @NonNull TaskDetailContract.View mTaskDetailView,
                               @NonNull BaseSchedulerProvider mSchedularProvider) {
        this.mTasksRepository = mTasksRepository;
        this.mTaskDetailView = mTaskDetailView;
        this.mSchedularProvider = mSchedularProvider;
        this.mTaskId = mTaskId;

        mCompositeDisposable = new CompositeDisposable();
        mTaskDetailView.setPresenter(this);
    }

    @Override
    public void editTask() {
//        if () {
//            mTaskDetailView.showMissingTask();
//            return;
//        }
        mTaskDetailView.showEditTask(mTaskId);
    }

    @Override
    public void deleteTask() {
//        if (Strings.isNullOrEmpty(mTaskId)) {
//            mTaskDetailView.showMissingTask();
//            return;
//        }
        mTasksRepository.deleteTask(mTaskId);
        mTaskDetailView.showTaskDeleted();
    }

    @Override
    public void completeTask() {
//        if (Strings.isNullOrEmpty(mTaskId)) {
//            mTaskDetailView.showMissingTask();
//            return;
//        }
        mTasksRepository.completeTask(mTaskId);
        mTaskDetailView.showTaskMarkedComplete();
    }

    @Override
    public void activateTask() {
//        if (Strings.isNullOrEmpty(mTaskId)) {
//            mTaskDetailView.showMissingTask();
//            return;
//        }
        mTasksRepository.activateTask(mTaskId);
        mTaskDetailView.showTaskMarkedActive();
    }

    @Override
    public void subscribe() {
        openTask();
    }

    private void openTask() {
//        if (Strings.isNullOrEmpty(mTaskId)) {
//            mTaskDetailView.showMissingTask();
//            return;
//        }

        mTaskDetailView.setLoadingIndicator(true);
        mCompositeDisposable.add(mTasksRepository
                .getTask(mTaskId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribeOn(mSchedularProvider.computation())
                .observeOn(mSchedularProvider.ui())
                .subscribe(
                        //onNext
                        this::showTask,
                        //onError
                        throwable -> {
                        },
                        //onCompleted
                        () -> mTaskDetailView.setLoadingIndicator(false)));
    }

    private void showTask(Task task) {
        String title = task.getTitle();
        String description = task.getDescription();

        if (Strings.isNullOrEmpty(title)) {
            mTaskDetailView.hideTitle();
        } else {
            mTaskDetailView.showTitle(title);
        }

        if (Strings.isNullOrEmpty(description)) {
            mTaskDetailView.hideDescription();
        } else {
            mTaskDetailView.showDescription(description);
        }
        mTaskDetailView.showCompletionStatus(task.getCompleted());
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }
}
