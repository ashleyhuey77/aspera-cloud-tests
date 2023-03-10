package com.api.gateway;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TransferRequest {

    @SerializedName("direction")
    @Expose
    private String direction;
    @SerializedName("remote_host")
    @Expose
    private String remoteHost;
    @SerializedName("remote_user")
    @Expose
    private String remoteUser;
    @SerializedName("ssh_private_key")
    @Expose
    private String sshPrivateKey;
    @SerializedName("paths")
    @Expose
    private List<Paths> paths;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("ssh_port")
    @Expose
    private String sshPort;
    @SerializedName("target_rate_kbps")
    @Expose
    private String targetRate;
    @SerializedName("overwrite")
    @Expose
    private String overwrite;
    @SerializedName("resume_policy")
    @Expose
    private String resumePolicy;
    @SerializedName("retry_duration")
    @Expose
    private String retryDuration;
    @SerializedName("create_dir")
    @Expose
    private String createDir;
    @SerializedName("precalculate_job_size")
    @Expose
    private String precalculateJobSize;

    public static class Paths {
        @SerializedName("source")
        @Expose
        private String source;
        @SerializedName("destination")
        @Expose
        private String destination;
    }
}
