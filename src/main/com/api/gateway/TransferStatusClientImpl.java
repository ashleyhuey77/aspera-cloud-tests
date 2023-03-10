package com.api.gateway;

import com.app.data.DataMapper;
import com.app.db.ConsoleDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.api.common.ErrorCode;
import com.api.common.utils.AuthenticationInterceptor;
import com.api.common.utils.UnsafeOkHttpClient;
import com.api.common.utils.Validator;

import java.util.Objects;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.CompletableFuture;

public class TransferStatusClientImpl implements TransferStatusClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferStatusClientImpl.class);
  private final TransferStatusClient tc;
  private static final OkHttpClient.Builder httpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
  private static Retrofit retrofit;

  public TransferStatusClientImpl(String url) {
    ConsoleDecoration.printSection("TRANSFER STATUS CLIENT");
    LOGGER.info("=> Hitting transfer status api...");
    String authToken =
        Credentials.basic(DataMapper.getCredentials().name, DataMapper.getCredentials().password);

    Retrofit.Builder builder =
        new Retrofit.Builder()
            .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create());

    AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

    if (!httpClient.interceptors().contains(interceptor)) {
      httpClient.addInterceptor(interceptor);

      retrofit = builder.client(httpClient.build()).build();
    }
    tc = retrofit.create(TransferStatusClient.class);
  }

  @Override
  public CompletableFuture<Response<TransferStatusResponse>> response(String id) throws Exception {
    Gson mapper = new GsonBuilder().setPrettyPrinting().create();
    CompletableFuture<Response<TransferStatusResponse>> resp = tc.response(id);
    if (resp.get().isSuccessful()) {
      Validator.of(resp.get().body())
          .validate(
              TransferStatusResponse::getStatus,
              Objects::nonNull,
              new TransferClientException(
                  "The transfer status returned a null value. Check client configuration.",
                  ErrorCode.TRANSFER_STATUS_ERROR))
          .get();
      LOGGER.info(
          "=> The transfer status call was started successfully: {}",
          mapper.toJson(resp.get().body(), TransferStatusResponse.class));
      return resp;
    } else {
      throw new TransferStatusClientException(
          "Service response was " + resp.get().code(), ErrorCode.TRANSFER_STATUS_ERROR);
    }
  }
}
