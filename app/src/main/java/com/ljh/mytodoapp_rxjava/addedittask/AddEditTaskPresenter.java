package com.ljh.mytodoapp_rxjava.addedittask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ljh.mytodoapp_rxjava.data.Task;
import com.ljh.mytodoapp_rxjava.data.source.TasksDataSource;
import com.ljh.mytodoapp_rxjava.utils.schedulers.BaseSchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Administrator
 * @date 2018/8/13
 */
public class AddEditTaskPresenter implements AddEditTaskContract.Presenter {
    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private final AddEditTaskContract.View mAddTaskView;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @Nullable
    private String mTaskId;

    private boolean mIsDataMissing;

    @NonNull
    private CompositeDisposable mCompositeDisposable;

    public AddEditTaskPresenter(String mTaskId, @NonNull TasksDataSource mTasksRepository, @NonNull AddEditTaskContract.View mAddTaskView, boolean shouldLoadDataFromRepo, @NonNull BaseSchedulerProvider mSchedulerProvider) {
        this.mTasksRepository = mTasksRepository;
        this.mAddTaskView = mAddTaskView;
        this.mSchedulerProvider = mSchedulerProvider;
        this.mTaskId = mTaskId;
        mIsDataMissing = shouldLoadDataFromRepo;

        mCompositeDisposable = new CompositeDisposable();
        mAddTaskView.setPresenter(this);
    }

    @Override
    public void saveTask(String title, String description) {
        if (isNewTask()) {
            createTask(title, description);
        } else {
            updateTask(title, description);
        }
    }

    private void updateTask(String title, String description) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() was called but task is new");
        }
        mTasksRepository.saveTask(new Task(title, description));
        mAddTaskView.showTasksList();
    }

    private void createTask(String title, String description) {
        Task newTask = new Task(title, description);
        if (newTask.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        } else {
            mTasksRepository.saveTask(newTask);
            mAddTaskView.showTasksList();
        }
    }

    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() was called but task is new");
        }
        mCompositeDisposable.add(mTasksRepository
                .getTask(mTaskId)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(
                        //onNext
                        taskOptional -> {
                            if (taskOptional.isPresent()) {
                                Task task = taskOptional.get();
                                if (mAddTaskView.isActive()) {
                                    mAddTaskView.setTitle(task.getTitle());
                                    mAddTaskView.setDescription(task.getDescription());

                                    mIsDataMissing = false;
                                }
                            } else {
                                if (mAddTaskView.isActive()) {
                                    mAddTaskView.showEmptyTaskError();
                                }
                            }
                        },
                        //onError
                        throwable -> {
                            if (mAddTaskView.isActive()) {
                                mAddTaskView.showEmptyTaskError();
                            }
                        }));
    }

    @Override
    public boolean isDataMissing() {
        return false;
    }

    @Override
    public void subscribe() {
        if (!isNewTask() && mIsDataMissing) {
            populateTask();
        }
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }
}
