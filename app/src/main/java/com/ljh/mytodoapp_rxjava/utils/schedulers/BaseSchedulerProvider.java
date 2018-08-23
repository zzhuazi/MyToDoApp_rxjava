package com.ljh.mytodoapp_rxjava.utils.schedulers;

import javax.annotation.Nonnull;

import io.reactivex.Scheduler;

/**
 * @author Administrator
 * @date 2018/8/12
 */
public interface BaseSchedulerProvider {

    @Nonnull
    Scheduler computation();

    @Nonnull
    Scheduler io();

    @Nonnull
    Scheduler ui();
}
