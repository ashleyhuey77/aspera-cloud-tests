package com.api.common.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponse {

	@SerializedName("error")
	@Expose
	public ErrorStatus error;

	public class ErrorStatus {
		@SerializedName("user_message")
		@Expose
		public String status;
	}
}