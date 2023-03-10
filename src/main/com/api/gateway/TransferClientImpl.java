package com.api.gateway;

import com.app.data.DataMapper;
import com.app.db.ConsoleDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.api.common.ErrorCode;
import com.api.common.utils.AuthenticationInterceptor;
import com.api.common.utils.ErrorHandler;
import com.api.common.utils.UnsafeOkHttpClient;
import com.api.common.utils.Validator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class TransferClientImpl implements TransferClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferClientImpl.class);
  private final TransferClient tc;
  private static final OkHttpClient.Builder httpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
  private static Retrofit retrofit;

  public TransferClientImpl(String url) {
    ConsoleDecoration.printSection("TRANSFER CLIENT");
    LOGGER.info("=> Hitting transfer api...");
    String authToken = Credentials.basic(DataMapper.getCredentials().name, DataMapper.getCredentials().password);
    Builder builder =
        new Retrofit.Builder()
            .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create());

    AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

    if (!httpClient.interceptors().contains(interceptor)) {
      httpClient.addInterceptor(interceptor);

      retrofit = builder.client(httpClient.build()).build();
    }
    tc = retrofit.create(TransferClient.class);
  }

  @Override
  public CompletableFuture<Response<TransferResponse>> response(TransferRequest body)
      throws Exception {
    Gson mapper = new GsonBuilder().setPrettyPrinting().create();
    CompletableFuture<Response<TransferResponse>> resp = tc.response(body);
    if (resp.get().isSuccessful()) {
      LOGGER.info(
          "=> The new transfer was started successfully: {}",
          mapper.toJson(resp.get().body(), TransferResponse.class));
      Validator.of(resp.get().body())
          .validate(
              TransferResponse::getId,
              Objects::nonNull,
              new TransferClientException(
                  "The transfer id returned a null value. Check client configuration.",
                  ErrorCode.TRANSFER_ERROR)).get();
      return resp;
    } else {
      throw new TransferClientException("Service response was " + resp.get().code() + ": " + ErrorHandler.parseError(resp.get(), retrofit).error.status, ErrorCode.TRANSFER_ERROR);
    }
  }
}
