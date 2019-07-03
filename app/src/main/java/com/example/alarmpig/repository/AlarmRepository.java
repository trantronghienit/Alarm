package com.example.alarmpig.repository;

import com.example.alarmpig.callback.OnGetConfigInfoListener;
import com.example.alarmpig.callback.OnImportTokenListener;
import com.example.alarmpig.model.ConfigAlarm;
import com.example.alarmpig.rest.ApiClient;
import com.example.alarmpig.rest.ApiServices;
import com.example.alarmpig.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AlarmRepository {

    private ApiServices apiServices;
    private static AlarmRepository repository;

    public AlarmRepository() {
        apiServices = ApiClient.getClient().create(ApiServices.class);
    }

    public static AlarmRepository getInstance() {
        if (repository == null) {
            repository = new AlarmRepository();
        }
        return repository;
    }

    public void getConfig(final OnGetConfigInfoListener listener) {
        apiServices.getConfig().enqueue(new Callback<ConfigAlarm>() {
            @Override
            public void onResponse(Call<ConfigAlarm> call, Response<ConfigAlarm> response) {
                if (response.isSuccessful()) {
                    if (listener != null) {
                        listener.onSuccess(response.body());
                    }
                } else {
                    String message = response.message();
                    if (listener != null) {
                        listener.onFailed(message);
                    }
                }

            }

            @Override
            public void onFailure(Call<ConfigAlarm> call, Throwable t) {
                if (listener != null) {
                    listener.onFailed(t.getMessage());
                }
            }
        });
    }

    public void pushTokenNotification(String token , final OnImportTokenListener listener) {
        final JSONObject jsonData = new JSONObject();
        try {
            jsonData.put(Constants.TOKEN, token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(Constants.JSON, jsonData.toString());
        apiServices.importTokenNotification(Constants.HOST_TOKEN_FIREBASE, body)
                .enqueue(new Callback<Response<ResponseBody>>() {
                    @Override
                    public void onResponse(Call<Response<ResponseBody>> call, Response<Response<ResponseBody>> response) {
                        if (response.isSuccessful()) {
                            if (listener != null) {
                                listener.onImportTokenSuccess();
                            }
                        } else {
                            String message = response.message();
                            if (listener != null) {
                                listener.onImportTokenFailed(message);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Response<ResponseBody>> call, Throwable t) {
                        if (listener != null) {
                            listener.onImportTokenFailed(t.getMessage());
                        }
                    }
                });
    }

    public <T> T createService(Class<T> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(new OkHttpClient.Builder().build())
                .build();
        return retrofit.create(serviceClass);
    }

}
