package com.heihei.hehe.recyclerview_demo.divider;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heihei.hehe.recyclerview_demo.R;
import com.heihei.hehe.recyclerview_demo.divider.widgt.ItemDivider;

public class GridManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_manager);
        RecyclerView re = (RecyclerView) findViewById(R.id.re);
        re.setLayoutManager(new GridLayoutManager(this,4,GridLayoutManager.VERTICAL,false));
        re.addItemDecoration(new ItemDivider().setDividerWith(2).setDividerColor(Color.MAGENTA));
        re.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item,parent,false)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView tv = (TextView) holder.itemView.findViewById(R.id.text1);
            tv.setText(String.valueOf(position));
//            holder.itemView.setBackgroundColor(Color.BLUE);
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }
}
