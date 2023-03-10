package com.app.thread.pool;

import com.app.thread.ex.AsyncResult;

public interface TaskControl {
	public AsyncResult<Object> start(String url, Integer waitTime) throws Exception;

	public Object end(AsyncResult<Object> result) throws Exception;
}
