package com.example.alarmpig.repository;

import com.example.alarmpig.callback.OnGetConfigInfoListener;
import com.example.alarmpig.model.ConfigAlarm;
import com.example.alarmpig.rest.ApiClient;
import com.example.alarmpig.rest.ApiServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmRepository {

    private ApiServices apiServices;
    private static AlarmRepository repository;
    public AlarmRepository(){
        apiServices = ApiClient.getClient().create(ApiServices.class);
    }

    public static AlarmRepository getInstance() {
        if(repository ==  null){
            repository = new AlarmRepository();
        }
        return repository;
    }

    public void getConfig(final OnGetConfigInfoListener listener) {
        apiServices.getConfig().enqueue(new Callback<ConfigAlarm>() {
            @Override
            public void onResponse(Call<ConfigAlarm> call, Response<ConfigAlarm> response) {
                if(response.isSuccessful()){
                    if(listener != null){
                        listener.onSuccess(response.body());
                    }
                }else {
                    String message = response.message();
                    if(listener != null){
                        listener.onFailed(message);
                    }
                }

            }

            @Override
            public void onFailure(Call<ConfigAlarm> call, Throwable t) {
                if(listener != null){
                    listener.onFailed(t.getMessage());
                }
            }
        });
    }

}
