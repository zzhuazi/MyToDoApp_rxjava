package com.ljh.mytodoapp_rxjava.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ljh.mytodoapp_rxjava.R;
import com.ljh.mytodoapp_rxjava.data.Task;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Administrator
 * @date 2018/8/12
 */
public class TasksAdapter extends BaseAdapter {
    private List<Task> mTasks;

    private TaskItemListener mItemListener = null;

    public TasksAdapter(List<Task> mTasks) {
        this.mTasks = mTasks;
    }

    public void setTaskItemListener(TaskItemListener taskItemListener){
        mItemListener = taskItemListener;
    }
    public void replaceData(List<Task> tasks) {
        setList(tasks);
        notifyDataSetChanged();
    }

    private void setList(List<Task> tasks) {
        mTasks = checkNotNull(tasks);
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public Task getItem(int i) {
        return mTasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if(rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.task_item, viewGroup, false);
        }
        final Task task = getItem(i);

        TextView titleTv = rowView.findViewById(R.id.title);
        titleTv.setText(task.getTitleForList());

        CheckBox completeCB = rowView.findViewById(R.id.complete);

        completeCB.setChecked(task.isCompleted());
        if(task.isCompleted()) {
            rowView.setBackgroundDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.list_completed_touch_feedback));
        }else {
            rowView.setBackgroundDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.touch_feedback));
        }
        completeCB.setOnClickListener(__ ->{
            if(!task.isCompleted()) {
                mItemListener.onCompleteTaskClick(task);
            }else {
                mItemListener.onActivateTaskClick(task);
            }
        });

        rowView.setOnClickListener(__ -> mItemListener.onTaskClick(task));
        return rowView;
    }

    public interface TaskItemListener{
        void onTaskClick(Task clickedTask);

        void onCompleteTaskClick(Task completedTask);

        void onActivateTaskClick(Task activatedTask);
    }
}
