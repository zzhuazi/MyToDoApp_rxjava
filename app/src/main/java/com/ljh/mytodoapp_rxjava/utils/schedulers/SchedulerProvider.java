package com.ljh.mytodoapp_rxjava.utils.schedulers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Administrator
 * @date 2018/8/12
 */
public class SchedulerProvider implements BaseSchedulerProvider {
    @Nullable
    private static SchedulerProvider INSTANCE;

    private SchedulerProvider() {
    }

    public static synchronized SchedulerProvider getInstance(){
        if(INSTANCE == null) {
            INSTANCE = new SchedulerProvider();
        }
        return INSTANCE;
    }

    @Nonnull
    @Override
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @Nonnull
    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @Nonnull
    @Override
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }
}
