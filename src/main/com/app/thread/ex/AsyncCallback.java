package com.app.thread.ex;

import java.util.Optional;

public interface AsyncCallback<T> {

    void onComplete(T value, Optional<Exception> ex);
}
