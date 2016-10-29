package com.heihei.hehe.recyclerview_demo.spansize;

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

public class SpanSizeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_span_size);
        RecyclerView re = (RecyclerView) findViewById(R.id.re);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position % 5 == 0){
                    return 4;
                }else if(position % 5 == 1){
                    return 3;
                }else if(position % 5 == 2){
                    return 1;
                }else{
                    return 2;
                }
            }
        });
        re.setLayoutManager(layoutManager);
        re.addItemDecoration(new ItemDivider().setDividerWith(2).setDividerColor(Color.BLUE));
        MyAdapter myAdapter = new MyAdapter();
        re.setAdapter(myAdapter);
    }

    private class MyAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            TextView tv = (TextView) holder.itemView.findViewById(R.id.text1);
            tv.setText(String.valueOf(position));
           // holder.itemView.setBackgroundColor(Color.RED);

//            tv.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    //itemTouchHelper.startDrag(holder);
//                    //itemTouchHelper.startSwipe(holder);
//                    return false;
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return 100;
        }

    }
}