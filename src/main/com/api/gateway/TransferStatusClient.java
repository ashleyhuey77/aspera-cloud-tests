package com.api.gateway;

import retrofit2.Response;
import retrofit2.http.*;

import java.util.concurrent.CompletableFuture;

public interface TransferStatusClient {

    @GET("/ops/transfers/{id}")
    @Headers({"accept: application/json", "content-type: application/json"})
    CompletableFuture<Response<TransferStatusResponse>> response(@Path("id") String id) throws Exception;

}
