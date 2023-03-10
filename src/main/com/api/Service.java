package com.api;

import com.google.gson.Gson;
import com.api.common.ErrorCode;
import com.api.common.utils.Validator;
import com.api.common.wait.Api;
import com.api.common.wait.StatusTimeoutException;
import com.api.common.wait.TransferStatusApi;
import com.api.gateway.*;
import com.api.gateway.TransferRequest;
import com.api.gateway.TransferResponse;
import com.api.gateway.TransferStatusResponse;
import java.io.InputStream;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public class Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

  private final String url;
  private final TransferRequest requestBody;
  private TransferResponse transferResponse;

  public Service(String url) throws Exception {
    this.url = url;
    Gson gson = new Gson();
    InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream("transfer.json");
    Reader reader = new InputStreamReader(ioStream, "UTF-8");
    requestBody = gson.fromJson(reader, TransferRequest.class);
    Validator.of(requestBody)
        .validate(
            TransferRequest::getDirection,
            Objects::nonNull,
            new ServiceException(
                "The direction property returned null. This property is required to make a transfer service call.",
                ErrorCode.REQUEST_BODY_ERROR))
        .validate(
            TransferRequest::getRemoteHost,
            Objects::nonNull,
            new ServiceException(
                "The remote_host property returned null. This property is required to make a transfer service call.",
                ErrorCode.REQUEST_BODY_ERROR))
        .validate(
            TransferRequest::getSshPrivateKey,
            Objects::nonNull,
            new ServiceException(
                "The token property returned null. This property is required to make a transfer service call.",
                ErrorCode.REQUEST_BODY_ERROR))
        .validate(
            TransferRequest::getPaths,
            Objects::nonNull,
            new ServiceException(
                "The paths property returned null. This property is required to make a transfer service call.",
                ErrorCode.REQUEST_BODY_ERROR))
        .get();
  }

  public TransferResponse createNewTransfer() throws Exception {
    TransferClient transferClient = new TransferClientImpl(url);
    transferResponse = transferClient.response(requestBody).get().body();
    LOGGER.info("=> Transfer api call complete.");
    return transferResponse;
  }

  public TransferStatusResponse verifyTransferStatus(String id, Integer waitTime) {
    TransferStatusClientImpl transferStatusClient = new TransferStatusClientImpl(url);
    TransferStatusResponse response = null;
    AtomicReference<TransferStatusClientException> ex = new AtomicReference<>();
    if (id != null) {
      Api transferStatusApi = new TransferStatusApi();
      try {
        Wait<Api> wait =
            new FluentWait<>(transferStatusApi)
                .withTimeout(Duration.ofSeconds(waitTime))
                .pollingEvery(Duration.ofMillis(800));

        response =
            wait.until(
                api -> {
                  try {
                    return api.call(transferStatusClient, id);
                  } catch (TransferStatusClientException e) {
                    ex.set(e);
                    return new TransferStatusResponse();
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                  return null;
                });
        if (ex.get() != null) {
          throw ex.get();
        }
      } catch (TransferStatusClientException te) {
        throw te;
      } catch (TimeoutException time) {
        StatusTimeoutException exception = new StatusTimeoutException("Waited " + waitTime + " seconds for the status to change to complete.", ErrorCode.STATUS_WAIT_TIMEOUT);
        LOGGER.info("=> {}", exception.getMessage());
      } catch (Exception e) {
        LOGGER.info(
            "=> The following error occurred while waiting on the transfer status to change. \n {}",
            e.getMessage());
      }
    } else {
      throw new TransferClientException(
          "The transfer id returned a null value. Check client configuration.",
          ErrorCode.TRANSFER_ERROR);
    }
    return response;
  }

  public <T> Callable<T> transfer(T value, Integer waitTime) {
    return () -> {
      createNewTransfer();
      verifyTransferStatus(transferResponse.getId(), waitTime);
      return value;
    };
  }
}
