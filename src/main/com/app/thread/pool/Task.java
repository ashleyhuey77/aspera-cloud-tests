package com.app.thread.pool;

import com.app.thread.ex.ThreadAsyncExecutor;

public abstract class Task implements TaskControl {

    protected ThreadAsyncExecutor executor;

    protected Task(ThreadAsyncExecutor executor) {
        this.executor = executor;
    }
}
