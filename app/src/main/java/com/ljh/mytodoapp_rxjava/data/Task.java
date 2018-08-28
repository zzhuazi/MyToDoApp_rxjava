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
    private  int id;

    @Nullable
    private  String title;

    @Nullable
    private  String description;

    private  boolean completed;

    public Task(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean getCompleted() {
        return completed;
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
        return Objects.equal(id, task.id) &&
                Objects.equal(title, task.title) &&
                Objects.equal(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, title, description);
    }

    @Override
    public String toString() {
        return "Task with title " + title;
    }
}
