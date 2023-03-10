package com.api.gateway;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import java.util.concurrent.CompletableFuture;

public interface TransferClient {

    @POST("/ops/transfers")
    @Headers({"accept: application/json", "content-type: application/json"})
    CompletableFuture<Response<TransferResponse>> response(@Body TransferRequest body) throws Exception;
}
