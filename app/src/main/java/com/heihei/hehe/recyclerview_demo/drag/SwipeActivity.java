package com.heihei.hehe.recyclerview_demo.drag;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.heihei.hehe.recyclerview_demo.R;
import java.util.ArrayList;
import java.util.List;

public class SwipeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> str = new ArrayList<>();
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        for (int i = 0; i < 11; i++) {
            str.add("data: " + i);
        }
        recyclerView = (RecyclerView) findViewById(R.id.re);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        new ItemTouchHelper(new MyItemTouchHandler(myAdapter)).attachToRecyclerView(recyclerView);
    }

    private class MyAdapter extends MyItemTouchHandler.ItemTouchAdapterImpl{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipe,parent,false)) {
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

        }

        @Override
        public void onItemRemove(int position) {
            str.remove(position);
        }

        @Override
        protected boolean autoOpenDrag() {
            return false;
        }
    }
}
