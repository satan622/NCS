package com.monad.noisecontrolsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.monad.noisecontrolsystem.Activity.InitActivity;
import com.monad.noisecontrolsystem.Activity.MainActivity;
import com.monad.noisecontrolsystem.Model.ApiService;
import com.monad.noisecontrolsystem.Model.MydataModel;
import com.monad.noisecontrolsystem.Realm.Myinfo;
import com.monad.noisecontrolsystem.Realm.ResponseModel;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Intro extends AppCompatActivity {
    private Realm realm;
    private MydataModel data;
    private ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        RealmQuery<Myinfo> query = realm.where(Myinfo.class);
        RealmResults<Myinfo> result = query.findAll();

        data = MydataModel.getInstance();

        if(result.size() == 0) {
            Intent i = new Intent(this, InitActivity.class);
            startActivity(i);
        } else {
                data.setRoom(result.get(0).getFirtNumber());
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://116.121.158.137:8000")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                service = retrofit.create(ApiService.class);

                Call<ResponseModel> loadSizeCall = service.Load(result.get(0).getFirtNumber());

                loadSizeCall.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        Log.i("asd", response.body().getVibration_data() + " ");
                        ResponseModel responseData = response.body();
                        data.setVibration(responseData.getNoise());
                        data.setNoise(responseData.getVibration_data());
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        Log.i("Noise", responseData.getNoise() + "");
                        Log.i("Vibration", responseData.getVibration_data() + "");
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        Log.i("fail",t.getMessage());
                    }
                });

        }
        finish();
    }


}
