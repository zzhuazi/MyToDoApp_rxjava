package com.ljh.mytodoapp_rxjava.utils.schedulers;

import javax.annotation.Nonnull;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Administrator
 * @date 2018/8/12
 */
public class ImmediateSchedulerProvider implements BaseSchedulerProvider {
    @Nonnull
    @Override
    public Scheduler computation() {
        return Schedulers.trampoline();
    }

    @Nonnull
    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }

    @Nonnull
    @Override
    public Scheduler ui() {
        return Schedulers.trampoline();
    }
}
