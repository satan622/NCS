package com.monad.noisecontrolsystem.Activity;

import android.os.AsyncTask;
import android.util.Log;

import com.monad.noisecontrolsystem.Model.Contributor;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by temp on 2017. 5. 18..
 */

public class NetworkCall extends AsyncTask<Call, Void, String> {
    @Override
    protected String doInBackground(Call... params) {
        try {
            Call<List<Contributor>> call = params[0];
            Response<List<Contributor>> response = call.execute();
            return response.body().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("result", result);
    }
}
