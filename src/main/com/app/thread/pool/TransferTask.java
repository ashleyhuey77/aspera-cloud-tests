package com.app.thread.pool;

import com.api.Service;
import com.app.thread.ex.AsyncCallback;
import com.app.thread.ex.AsyncResult;
import com.app.thread.ex.ThreadAsyncExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferTask extends Task implements TaskControl {

    private static Logger LOGGER = LoggerFactory.getLogger(TransferTask.class);
    public TransferTask(ThreadAsyncExecutor executor) {
        super(executor);
    }

    @Override
    public AsyncResult<Object> start(String url, Integer waitTime) throws Exception {
        Service service = new Service(url);
        return executor.startProcess(service.transfer("", waitTime), callback("Starting new transfer"));
    }

    @Override
    public Object end(AsyncResult<Object> result) throws Exception {
        return executor.endProcess(result);
    }

    private static <T> AsyncCallback<T> callback(String name) {
        return (value, ex) -> {
            if (ex.isPresent()) {
                LOGGER.info("=> " + name + " failed: " + ex.map(Exception::getMessage).orElse(""));
            } else {
                LOGGER.info("=> " + name + " <" + value + ">");
            }
        };
    }


}
