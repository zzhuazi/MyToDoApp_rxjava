package com.ljh.mytodoapp_rxjava.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.util.UUID;

/**
 * @author Administrator
 * @date 2018/8/10
 */
public class Task extends LitePalSupport {

    @NonNull
    @SerializedName("id")
    private final String mId;

    @Nullable
    private final String title;

    @Nullable
    private final String description;

    private final boolean completed;

    //创建一个未完成的task
    public Task(String mTitle, String mDescription) {
        this(UUID.randomUUID().toString(), mTitle, mDescription, false);
    }

    //创建一个已有id的task（或者复制其他task）
    public Task(@NonNull String mId, String mTitle, String mDescription) {
        this(mId, mTitle, mDescription, false);
    }

    //创建一个已完成的task
    public Task(String mTitle, String mDescription, boolean mCompleted) {
        this(UUID.randomUUID().toString(), mTitle, mDescription, mCompleted);
    }

    //创建一个已完成的并带有id的task
    public Task(@NonNull String id, String title, String description, boolean completed) {
        this.mId = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    @NonNull
    public String getMId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(title)) {
            return title;
        } else {
            return description;
        }
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isActive() {
        return !completed;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(title) &&
                Strings.isNullOrEmpty(description);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task task = (Task) obj;
        return Objects.equal(mId, task.mId) &&
                Objects.equal(title, task.title) &&
                Objects.equal(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, title, description);
    }

    @Override
    public String toString() {
        return "Task with title " + title;
    }
}
