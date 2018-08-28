package com.ljh.mytodoapp_rxjava.addedittask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ljh.mytodoapp_rxjava.R;
import com.ljh.mytodoapp_rxjava.data.source.TasksLocalDataSource;
import com.ljh.mytodoapp_rxjava.data.source.TasksRepository;
import com.ljh.mytodoapp_rxjava.utils.ActivityUtils;
import com.ljh.mytodoapp_rxjava.utils.schedulers.SchedulerProvider;

/**
 * @author Administrator
 * @date 2018/8/13
 */
public class AddEditTaskActivity extends AppCompatActivity{
    public static final int REQUEST_ADD_TASK = 1;

    public static final String SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY";

    private AddEditTaskPresenter mAddEditTaskPresenter;

    private ActionBar mActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);

        //set up the toolbar;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if(mActionBar != null) {
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        AddEditTaskFragment addEditTaskFragment = (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        Integer taskId = getIntent().getIntExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID,-1);

        setToolbarTitle(taskId.toString());

        if(addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTaskFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), addEditTaskFragment, R.id.contentFrame);
        }

        boolean shouldLoadDataFromRepo = true;

        // Prevent the presenter from loading data from the repository if this is a config change.
        if (savedInstanceState != null) {
            // Data might not have loaded when the config change happen, so we saved the state.
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY);
        }

        mAddEditTaskPresenter = new AddEditTaskPresenter(taskId,
                TasksRepository.getInstance(TasksLocalDataSource.getInstance()),
                addEditTaskFragment,
                shouldLoadDataFromRepo,
                SchedulerProvider.getInstance());
    }

    private void setToolbarTitle(@Nullable String taskId) {
        if(taskId == null) {
            mActionBar.setTitle(R.string.add_task);
        } else {
            mActionBar.setTitle(R.string.edit_task);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, mAddEditTaskPresenter.isDataMissing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
