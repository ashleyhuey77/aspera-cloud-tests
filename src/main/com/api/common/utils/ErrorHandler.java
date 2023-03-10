package com.api.common.utils;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import java.io.IOException;
import java.lang.annotation.Annotation;

public class ErrorHandler {

	public static ErrorResponse parseError(Response<?> response, Retrofit retro) {
		Converter<ResponseBody, ErrorResponse> converter = retro.responseBodyConverter(ErrorResponse.class , new Annotation[0]);
		ErrorResponse error;
		try{
			error = converter.convert(response.errorBody());
		}catch (IOException e){
			return new ErrorResponse();
		}
		return error;
	}
}