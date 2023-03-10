package com.api.common.wait;

import com.api.gateway.TransferStatusClient;
import com.api.gateway.TransferStatusClientException;
import com.api.gateway.TransferStatusResponse;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TransferStatusApi implements Api {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferStatusApi.class);

    @Override
    public TransferStatusResponse call(TransferStatusClient transferStatusClient, String id) throws Exception {
        TransferStatusResponse transferResponse = null;
        try {
            transferResponse = transferStatusClient
                    .response(id)
                    .get()
                    .body();
            LOGGER.info("=> Transfer status api call complete.");
            if (transferResponse != null) {
                if (transferResponse.getStatus().equalsIgnoreCase("complete")) {
                    LOGGER.info("=> The transfer status is {}.", transferResponse.getStatus());
                    return transferResponse;
                } else {
                    LOGGER.info("=> The transfer status is {}.", transferResponse.getStatus());
                    transferResponse = null;
                }
            }
        } catch (TransferStatusClientException e) {
            throw e;
        }
        return transferResponse;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return null;
    }

    @Override
    public WebElement findElement(By by) {
        return null;
    }
}
