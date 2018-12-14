package com.monad.noisecontrolsystem.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monad.noisecontrolsystem.Model.ChatData;
import com.monad.noisecontrolsystem.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ChatData> mDataset;
    private int room;

    private final static int HEADER_VIEW = 0;
    private final static int CONTENT_VIEW = 1;

    private int currentViewType;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mMessage;

        public ViewHolder(View view) {
            super(view);
            mMessage = (TextView)view.findViewById(R.id.message);
        }
    }

    public static class ViewHolder2 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mMessage;

        public ViewHolder2(View view) {
            super(view);
            mMessage = (TextView)view.findViewById(R.id.message);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatAdapter(ArrayList<ChatData> myDataset, int room) {
        mDataset = myDataset;
        this.room = room;
    }

    void setCurrentViewType(int currentViewType) {
        this.currentViewType = currentViewType;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType){
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
                viewHolder = new ViewHolder(view);
              break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item2, parent, false);
                viewHolder = new ViewHolder2(view);
                break;
            default:
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatData data = mDataset.get(position);
        if(holder instanceof ViewHolder){
            ((ViewHolder)holder).mMessage.setText(data.getMessage());
        } else if((holder instanceof ViewHolder2)) {
            ((ViewHolder2)holder).mMessage.setText(data.getMessage());
        }


    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatData data = mDataset.get(position);
        if(data.getRoom_number() != room) {
            return 2;
        }
        else {
            return 1;
        }
    }
}
