package com.app.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Configuration {
    @SerializedName("endpoint")
    @Expose
    private String endpoint;
    @SerializedName("thread_count")
    @Expose
    private Integer threadCount;
    @SerializedName("status_wait_time")
    @Expose
    private Integer waitTime;
}
