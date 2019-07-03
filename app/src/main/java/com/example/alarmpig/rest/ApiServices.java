package com.example.alarmpig.rest;

import com.example.alarmpig.model.ConfigAlarm;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiServices {

    @GET("config.json")
    Call<ConfigAlarm> getConfig();

    @GET
    @Streaming
    Call<ResponseBody> downloadFile(@Url String fileName);

    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadFileByUrlRx(@Url String fileUrl);

    //
    @PUT
    Call<Response<ResponseBody>> importTokenNotification(@Url String url ,@Body RequestBody body);
}
