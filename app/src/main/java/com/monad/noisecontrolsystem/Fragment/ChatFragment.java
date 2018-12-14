package com.monad.noisecontrolsystem.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.monad.noisecontrolsystem.Adapter.ChatAdapter;
import com.monad.noisecontrolsystem.Model.ApiService;
import com.monad.noisecontrolsystem.Model.ChatData;
import com.monad.noisecontrolsystem.Model.ListModel;
import com.monad.noisecontrolsystem.Model.MydataModel;
import com.monad.noisecontrolsystem.R;
import com.monad.noisecontrolsystem.Realm.Myinfo;
import com.monad.noisecontrolsystem.Realm.ResponseModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private ArrayList<ChatData> myDataset;
    private TextView mText;
    private Button post;
    private Realm realm;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private int room;
    private ApiService service;
    private List<String> test = new ArrayList<>();
    private int LastNumber;
    private Spinner spinner;
    private int toRoom = 101; //채티장 방 호수
    private View v;
    private MydataModel data;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_3, container, false);
        //firebase init
        data = MydataModel.getInstance();
        room = data.getRoom();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_listview);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        myDataset = new ArrayList<>();
        mAdapter = new ChatAdapter(myDataset, room);
        mRecyclerView.setAdapter(mAdapter);


        //data Instance Load

        loadList();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //layout bind
        mText = (TextView) v.findViewById(R.id.textView2);
        post = (Button) v.findViewById(R.id.button123);
        post.setOnClickListener(this);


        mText.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });

        return v;
    }

    private void SpinnerSet(List<String> list) {
        spinner = (Spinner) v.findViewById(R.id.chat_spinner);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spin, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void loadList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://116.121.158.137:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.i("start","start");
        service = retrofit.create(ApiService.class);

        Call<List<ListModel>> loadSizeCall = service.listLoad();

        loadSizeCall.enqueue(new Callback<List<ListModel>>() {
            @Override
            public void onResponse(Call<List<ListModel>>  call, Response<List<ListModel>> response) {
                test.clear();
                List<ListModel> list = response.body();
                for (ListModel obj: list) {
                    if(obj.getHo() != data.getRoom())
                        test.add(obj.getHo()+"");
                }
                SpinnerSet(test);

            }

            @Override
            public void onFailure(Call<List<ListModel>> call, Throwable t) {
                Log.i("fail",t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.button123:
                if (mText.getText().toString().equals("")) break;
                else {
                    String inPutText = mText.getText().toString();
                    ChatData chatData = new ChatData(inPutText, room , toRoom);  // 유저 이름과 메세지로 chatData 만들기
                    Log.i("sendRoom", toRoom+"");
                    databaseReference.child("message").push().setValue(chatData);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                    mText.setText("");
                    mRecyclerView.scrollToPosition(myDataset.size());
                }
                break;
        }
    }

    void show() {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("아이가 자고있습니다.");
        ListItems.add("조금만 조용히 해주세요.");
        ListItems.add("야근하고 왔습니다.");
        ListItems.add("쿵쾅거리는 소리가 너무 울립니다.");
        ListItems.add("집에 손님이 왔습니다.");
        ListItems.add("조금만 주의해 주세요.");
        ListItems.add("아내가 임신했습니다.");
        ListItems.add("청소기는 낮에 돌려주시기 바랍니다.");
        ListItems.add("소리가 너무 크게 들립니다.");
        ListItems.add("잠을 못 자고 있습니다.");
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        final List SelectedItems = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("전송할 내용 선택");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });

        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String msg = "";
                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            msg = ListItems.get(index);
                        }
                        mText.setText(msg);
                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String str = (String) spinner.getSelectedItem();
        toRoom = Integer.parseInt(str.replaceAll("\\D", ""));

        myDataset.clear();
        mAdapter.notifyDataSetChanged();

        databaseReference.child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myDataset.clear();
                mAdapter.notifyDataSetChanged();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ChatData post = data.getValue(ChatData.class);
                    if(room == post.getRoom_number() && toRoom == post.getTo_room_number()) { // 받은 메시지가 내가 보낸거가나..
                        myDataset.add(post);
                        mAdapter.notifyDataSetChanged();
                    }

                    if(room == post.getTo_room_number() && toRoom == post.getRoom_number()) {
                        myDataset.add(post);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}