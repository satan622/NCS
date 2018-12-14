package com.monad.noisecontrolsystem.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monad.noisecontrolsystem.Model.ChatData;
import com.monad.noisecontrolsystem.R;

import java.util.ArrayList;

/**
 * Created by temp on 2017. 5. 31..
 */

public class ListviewAdapter extends BaseAdapter {
    private ArrayList<ChatData> lists;
    private TextView msg;
    private int room;
    private Context mContext;
    public ListviewAdapter(Context mContext, int room) {
        this.mContext = mContext;
        this.room = room;
        lists = new ArrayList<ChatData>();
    }

    public int AddItem(ChatData item){
        this.lists.add(item);
        return 1;
    }

    public void Clear() {
        lists.clear();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.recyclerview_item, parent, false);


            ChatData model = lists.get(position);

            msg = (TextView)convertView.findViewById(R.id.message);

            Log.i("room", room+"");

            if(model.getRoom_number() != room) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.LEFT;
                params.setMargins(8,8,8,8);
                msg.setTextSize(14);
                msg.setBackgroundColor(Color.parseColor("#9E9E9E"));
                msg.setLayoutParams(params);
                msg.setPadding(7,7,7,7);
                msg.setText(lists.get(position).getMessage());
            }else {
                msg.setText(lists.get(position).getMessage());
            }

        }



        return convertView;

    }
}
