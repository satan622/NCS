package com.monad.noisecontrolsystem.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.monad.noisecontrolsystem.Model.ApiService;
import com.monad.noisecontrolsystem.Model.Contributor;
import com.monad.noisecontrolsystem.Model.MydataModel;
import com.monad.noisecontrolsystem.R;
import com.monad.noisecontrolsystem.Realm.Myinfo;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InitActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Button next;
    private Realm realm;
    private MydataModel data;
    private int room;
    private Spinner spinner;
    private ApiService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        data = MydataModel.getInstance();

        SpinnerSet();
        LayoutSet();
    }


    private void SpinnerSet() {
        spinner = (Spinner) findViewById(R.id.test1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }
    private void LayoutSet() {
        next = (Button) findViewById(R.id.initActivity_next);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.initActivity_next:
                realm.beginTransaction();
                Myinfo myinfo = realm.createObject(Myinfo.class); // Create a new object
                myinfo.setFirtNumber(room);
                myinfo.setLastNumber(room);

                realm.commitTransaction();
                data.setRoom(room);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://116.121.158.137:8000")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                service = retrofit.create(ApiService.class);
                Log.i("room", room + "");
                Call<Contributor> loadSizeCall = service.Insert(room, 0, 0);

                loadSizeCall.enqueue(new Callback<Contributor>() {
                    @Override
                    public void onResponse(Call<Contributor> call, Response<Contributor> response) {
                        if(response.isSuccessful()) {
                            Log.i("Success", "Success");

                        }
                    }

                    @Override
                    public void onFailure(Call<Contributor> call, Throwable t) {
                        Log.i("fail",t.getMessage());
                    }
                });
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String str = (String) spinner.getSelectedItem();
        room = Integer.parseInt(str.replaceAll("\\D", ""));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
