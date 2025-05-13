package com.amap.track.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.amap.track.demo.R;
import com.amap.track.sql.ListData;

import java.util.List;

public class ListAdapter extends BaseAdapter {


    List<ListData> data;

    public Context context;

    public ListAdapter(List<ListData> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setData(List<ListData> data) {
        this.data = data;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null){
            view =    LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
        }

         TextView dataView=  view.findViewById(R.id.data);

        dataView.setText("起点："+data.get(i).getTitle()+"\n终点："+data.get(i).getNameEnd());

        TextView time = view.findViewById(R.id.time);
        time.setText(data.get(i).getTime());
        return view;
    }
}
