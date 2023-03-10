package com.api.gateway;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TransferStatusResponse {

    @SerializedName("status")
    @Expose
    private String status;
}
