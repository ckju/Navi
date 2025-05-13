package com.amap.track.activity.history;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.amap.track.adapter.ListAdapter;
import com.amap.track.demo.R;
import com.amap.track.sql.DBManager;
import com.amap.track.sql.ListData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 历史数据
 */
public class ListActivity extends Activity {

    public List<ListData> listdata =new ArrayList<>();
    private DBManager db = null;
    private ListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
    }
    protected void initView() {
        db = new DBManager(this);
        listdata = db.queryAll();
        Collections.reverse(listdata);
        adapter = new ListAdapter(listdata, this);
        ListView list = findViewById(R.id.list);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                builder.setTitle("提示")
                        .setMessage("确认执行此操作？")
                        .setPositiveButton("确定", (dialog, which) -> { /* 处理确认操作 */
                            db.deleteNote(listdata.get(position).id);
                            listdata.remove(position);
                            adapter.notifyDataSetChanged();
                        })
                        .setNegativeButton("取消", null); // 取消按钮无需事件
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
        list.setAdapter(adapter);
    }
}