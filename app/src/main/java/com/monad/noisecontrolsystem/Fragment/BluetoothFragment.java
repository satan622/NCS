package com.monad.noisecontrolsystem.Fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.monad.noisecontrolsystem.Model.ApiService;
import com.monad.noisecontrolsystem.Model.Contributor;
import com.monad.noisecontrolsystem.Model.MydataModel;
import com.monad.noisecontrolsystem.R;
import com.monad.noisecontrolsystem.Realm.Myinfo;
import com.monad.noisecontrolsystem.Realm.ResponseModel;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class BluetoothFragment extends Fragment {
    private View v;
    static final int REQUEST_ENABLE_BT = 10;
    int mPariedDeviceCount = 0;
    private Set<BluetoothDevice> mDevices;
    private Realm realm;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDevice mRemoteDevie;

    private BluetoothSocket mSocket = null;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    char mCharDelimiter =  '\n';
    private MydataModel mydata;
    private ApiService service;
    private Thread mWorkerThread = null;
    private byte[] readBuffer;
    private int readBufferPosition;
    private int room;
    private Button mButtonSend;
    private TextView noise, vibration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_chat, container, false);

        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();
        RealmQuery<Myinfo> query = realm.where(Myinfo.class);
        RealmResults<Myinfo> result = query.findAll();
        room = result.get(0).getFirtNumber();
        mButtonSend = (Button) v.findViewById(R.id.search);
        noise = (TextView) v.findViewById(R.id.noise_data);
        vibration = (TextView) v.findViewById(R.id.vibration);
        mydata = MydataModel.getInstance();

        mButtonSend.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                        // 블루투스 활성화 시키는 메소드
                        checkBluetooth();
            }
        });
        return v;
    }


    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;
        for(BluetoothDevice deivce : mDevices) {
            if(name.equals(deivce.getName())) {
                selectedDevice = deivce;
                break;
            }
        }
        return selectedDevice;
    }

    void connectToSelectedDevice(String selectedDeviceName) {
        mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();

            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();
            beginListenForData();

        }catch(Exception e) {
            Toast.makeText(getActivity(),
                    "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            Log.i("Fuck","실패"); // App 종료
        }
    }


    void beginListenForData() {
        final Handler handler = new Handler();

        readBufferPosition = 0;
        readBuffer = new byte[1024];

        // 문자열 수신 쓰레드.
        mWorkerThread = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        int byteAvailable = mInputStream.available();
                        if(byteAvailable > 0) {                        // 데이터가 수신된 경우.
                            final byte[] packetBytes = new byte[byteAvailable];
                            mInputStream.read(packetBytes);
                            for(int i=0; i<byteAvailable; i++) {
                                byte b = packetBytes[i];
                                if(b == mCharDelimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                    final String data = new String(encodedBytes, "UTF-8");
                                    final String[] mA = data.split(",");
                                    readBufferPosition = 0;


                                    handler.post(new Runnable(){
                                        // 수신된 문자열 데이터에 대한 처리.
                                        @Override
                                        public void run() {
                                            noise.setText("소음 데이터 : " + mA[0]);
                                            vibration.setText("진동 데이터 : " + mA[1]);

                                            Retrofit retrofit = new Retrofit.Builder()
                                                    .baseUrl("http://116.121.158.137:8000")
                                                    .addConverterFactory(GsonConverterFactory.create())
                                                    .build();

                                            service = retrofit.create(ApiService.class);
                                            Log.i("room", room + "");
                                            Call<Contributor> loadSizeCall = service.Insert(room, Integer.parseInt(mA[0]),Integer.parseInt(mA[1].trim()));

                                            loadSizeCall.enqueue(new Callback<Contributor>() {
                                                @Override
                                                public void onResponse(Call<Contributor> call, Response<Contributor> response) {
                                                    if(response.isSuccessful()) {
                                                        Log.i("Success", "Success");
                                                        mydata.setNoise(Float.parseFloat(mA[0]));
                                                        mydata.setVibration(Float.parseFloat(mA[1]));
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Contributor> call, Throwable t) {
                                                    Log.i("fail",t.getMessage());
                                                }
                                            });
                                            onDestroy();
                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "데이터 수신 중 오류가 발생 했습니다.", Toast.LENGTH_LONG).show();
                        Log.i("Fuck","실패");
                    }
                }
            }


        });
        mWorkerThread.start();
    }

    void selectDevice() {

        mDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if(mPariedDeviceCount == 0 ) {
            Toast.makeText(getActivity(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            Log.i("Fuck","실패");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("블루투스 장치 선택");

        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }
        listItems.add("취소");


        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if(item == mPariedDeviceCount) {
                    Toast.makeText(getActivity(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    Log.i("Fuck","실패");
                }
                else {
                    connectToSelectedDevice(items[item].toString());
                }
            }

        });

        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null ) {  // 블루투스 미지원
            Toast.makeText(getActivity(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            Log.i("Fuck","실패");  // 앱종료
        }
        else { // 블루투스 지원
            /** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
             *               true : 지원 ,  false : 미지원
             */
            if(!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
                Toast.makeText(getActivity(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로 사용(0이상)
                /**
                 startActivityForResult 함수 호출후 다이얼로그가 나타남
                 "예" 를 선택하면 시스템의 블루투스 장치를 활성화 시키고
                 "아니오" 를 선택하면 비활성화 상태를 유지 한다.
                 선택 결과는 onActivityResult 콜백 함수에서 확인할 수 있다.
                 */
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else // 블루투스 지원하며 활성 상태인 경우.
                selectDevice();
        }
    }

    // onDestroy() : 어플이 종료될때 호출 되는 함수.
    //               블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌.
    @Override
    public void onDestroy() {
        try{
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
        }catch(Exception e){}
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult 를 여러번 사용할 땐 이런 식으로
        // switch 문을 사용하여 어떤 요청인지 구분하여 사용함.
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) { // 블루투스 활성화 상태
                    selectDevice();
                }
                else if(resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
                    Toast.makeText(getActivity(), "블루투수를 사용할 수 없어 프로그램을 종료합니다",
                            Toast.LENGTH_LONG).show();
                    Log.i("Fuck","실패");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}