package com.heihei.hehe.recyclerview_demo.divider.widgt;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.heihei.hehe.recyclerview_demo.R;

/**
 * Describe the function of the class
 *
 * @author zhujinlong@ichoice.com
 * @date 2016/9/19
 * @time 17:54
 * @description Describe the place where the class needs to pay attention.
 */
public class StickyRecyclerView extends RecyclerView {

    private int lineHeight,titleHeight;
    private int lineColor,titleColor,titleTextColor;

    public StickyRecyclerView(Context context) {
        this(context,null);
    }

    public StickyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public StickyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.StickyRecyclerView);
        // 分割线的高度
        lineHeight = array.getDimensionPixelOffset(R.styleable.StickyRecyclerView_dividerHeight,1);
        // 分栏的高度
        titleHeight = array.getDimensionPixelOffset(R.styleable.StickyRecyclerView_titleHeight,dip2px(context,35));
        // 分割线颜色
        lineColor = array.getColor(R.styleable.StickyRecyclerView_dividerColor,Color.LTGRAY);
        // 分栏背景色
        titleColor = array.getColor(R.styleable.StickyRecyclerView_titleColor,Color.LTGRAY);
        // 分栏文字颜色
        titleTextColor = array.getColor(R.styleable.StickyRecyclerView_titleTextColor,Color.BLUE);
        array.recycle();
        // 不用说,肯定是线性布局了,默认就实现
        setLayoutManager(new LinearLayoutManager(context));
    }

    @Deprecated
    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
    }

    // 让 adapter 必须继承 StickyAdapter
    public void setAdapter(@NonNull StickyAdapter stickyAdapter){
        addItemDecoration(new StickyDivider(stickyAdapter));
        super.setAdapter(stickyAdapter);
    }

    /**
     * 自定义分割线,通过分割线绘制title
     */
    private class StickyDivider extends ItemDecoration{

        private StickyAdapter adapter;
        private Paint paint;

        StickyDivider(@NonNull StickyAdapter adapter) {
            super();
            this.adapter = adapter;
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(titleHeight * 0.5f);
        }

        /**
         * 计算 item间间隙(是普通分割线 ,还是title)
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if(!adapter.needTitle(((LayoutParams) view.getLayoutParams()).getViewLayoutPosition())){
                outRect.top = lineHeight;
            }else {
                outRect.top = titleHeight;
            }
        }

        /**
         * 底层绘制,绘制分栏title
         */
        @Override
        public void onDraw(Canvas c, RecyclerView parent, State state) {
            super.onDraw(c, parent, state);
            int left = parent.getPaddingLeft();
            int right = parent.getMeasuredWidth() - parent.getPaddingRight();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                int position = ((LayoutParams) child.getLayoutParams()).getViewLayoutPosition();
                int bottom = child.getTop() - ((LayoutParams) child.getLayoutParams()).topMargin;
                if(!adapter.needTitle(position)){
                    // 画分割线
                    int top = bottom - lineHeight;
                    paint.setColor(lineColor);
                    c.drawRect(left, top, right, bottom, paint);
                }else {
                    //画TITLE
                    int top = bottom - titleHeight;
                    paint.setColor(titleColor);
                    c.drawRect(left, top, right, bottom, paint);
                    drawText(c,adapter.getItemViewTitle(position),left + titleHeight * 0.25f,bottom - titleHeight * 0.25f);
                }
            }
        }

        /**
         * 上层绘制,绘制顶部悬停title
         */
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, State state) {
            super.onDrawOver(c, parent, state);
            // 悬停title
            int left = parent.getPaddingLeft();
            int right = parent.getMeasuredWidth() - parent.getPaddingRight();
            int top = parent.getPaddingTop();
            int bottom = top + titleHeight;
            paint.setColor(titleColor);
            c.drawRect(left,top,right,bottom,paint);
            int pos = ((LinearLayoutManager)(parent.getLayoutManager())).findFirstVisibleItemPosition();
            drawText(c,adapter.getItemViewTitle(pos),left + titleHeight * 0.25f,bottom - titleHeight * 0.25f);
        }

        void drawText(Canvas c, String itemViewTitle, float x, float y){
            if(!TextUtils.isEmpty(itemViewTitle)){
                paint.setColor(titleTextColor);
                //paint.getTextBounds(itemViewTitle, 0, itemViewTitle.length(), mBounds);
                c.drawText(itemViewTitle, x,y, paint);
            }
        }
    }

    public static abstract class StickyAdapter extends Adapter{

        // 获取当前 item 的标题
        public abstract String getItemViewTitle(int position);

        // 如果标题和前面的item的标题一样,就不需要绘制
        boolean needTitle(int position){
            return position > -1 && (position == 0 || !getItemViewTitle(position).equals(getItemViewTitle(position - 1)));
        }
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}