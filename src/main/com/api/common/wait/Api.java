package com.api.common.wait;

import com.api.gateway.TransferStatusClient;
import com.api.gateway.TransferStatusResponse;
import org.openqa.selenium.*;

public interface Api extends SearchContext {

    TransferStatusResponse call(TransferStatusClient transferStatusClient, String id) throws Exception;

    @Override
    WebElement findElement(By by);
}
