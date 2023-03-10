package com.app.thread.ex;

import java.util.concurrent.ExecutionException;

public interface AsyncResult<T> {

    boolean isCompleted();

    T getValue() throws ExecutionException;

    void await() throws InterruptedException;
}
