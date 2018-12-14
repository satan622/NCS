package com.monad.noisecontrolsystem.Model;

import com.monad.noisecontrolsystem.Realm.ResponseModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by temp on 2017. 5. 18..
 */

public interface ApiService {
    @GET("/api/noise/")
    Call<List<Contributor>> loadSizes();

    @GET("/rank/")
    Call<List<RankModel>> getRank();

    @GET("/api/noise/list/")
    Call<List<ListModel>> listLoad();

    @GET("/average/{username}/")
    Call<AverageModel> getAverage(@Path("username") int room);

    @GET("/time/{username}/")
    Call<List<TimeModel>> getTime(@Path("username") int room);

    @FormUrlEncoded
    @POST("/api/noise/")
    Call<Contributor> Insert(@Field("ho") int ho, @Field("noise_data") int noise_data,
                           @Field("vibration_data") int vibration_data);

    @GET("/api/noise/{username}/")
    Call<ResponseModel> Load(@Path("username") int room);
}