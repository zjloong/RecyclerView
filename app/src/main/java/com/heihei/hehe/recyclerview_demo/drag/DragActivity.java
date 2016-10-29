package com.heihei.hehe.recyclerview_demo.drag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heihei.hehe.recyclerview_demo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DragActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> str = new ArrayList<>();
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        for (int i = 0; i < 50; i++) {
            str.add("data: " + i);
        }
        recyclerView = (RecyclerView) findViewById(R.id.re);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4,GridLayoutManager.VERTICAL,false));
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        new ItemTouchHelper(new MyItemTouchHandler(myAdapter)).attachToRecyclerView(recyclerView);
    }

    private class MyAdapter extends MyItemTouchHandler.ItemTouchAdapterImpl{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drag,parent,false)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView tv = (TextView) holder.itemView.findViewById(R.id.text1);
            tv.setText(str.get(position));
        }

        @Override
        public int getItemCount() {
            return str.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            // 拖动排序的回调,这里交换集合中数据的位置
            Collections.swap(str, fromPosition, toPosition);
        }

        @Override
        public void onItemRemove(int position) {
            // 滑动删除的回调,这里删除指定的数据
        }

        @Override
        protected boolean autoOpenSwipe() {
            return false;
        }
    }
}
