package com.heihei.hehe.recyclerview_demo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.heihei.hehe.recyclerview_demo.divider.GridManagerActivity;
import com.heihei.hehe.recyclerview_demo.divider.LinearManagerActivity;
import com.heihei.hehe.recyclerview_demo.divider.StickyActivity;
import com.heihei.hehe.recyclerview_demo.drag.DragActivity;
import com.heihei.hehe.recyclerview_demo.drag.SwipeActivity;
import com.heihei.hehe.recyclerview_demo.spansize.RefreshActivity;
import com.heihei.hehe.recyclerview_demo.spansize.SpanSizeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void linear(View v){
        startActivity(new Intent(this, LinearManagerActivity.class));
    }

    public void grid(View v){
        startActivity(new Intent(this, GridManagerActivity.class));
    }

    public void sticky(View v){
        startActivity(new Intent(this, StickyActivity.class));
    }

    public void spanSize(View v){
        startActivity(new Intent(this, SpanSizeActivity.class));
    }

    public void refresh(View v){
        startActivity(new Intent(this, RefreshActivity.class));
    }

    public void drag(View v){
        startActivity(new Intent(this, DragActivity.class));
    }

    public void swip(View v){
        startActivity(new Intent(this, SwipeActivity.class));
    }
}
