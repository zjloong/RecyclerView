package com.heihei.hehe.recyclerview_demo.drag;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Describe the function of the class
 *
 * @author zhujinlong@ichoice.com
 * @date 2016/10/28
 * @time 16:50
 * @description Describe the place where the class needs to pay attention.
 */
public class MyItemTouchHandler extends ItemTouchHelper.Callback {

    ItemTouchAdapterImpl adapter;

    public MyItemTouchHandler(@NonNull ItemTouchAdapterImpl adapter) {
        this.adapter = adapter;
    }

    /**
     * 设置 允许拖拽和滑动删除的方向
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 指定可 拖拽方向 和 滑动消失的方向
        int dragFlags, swipeFlags;
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager || manager instanceof StaggeredGridLayoutManager) {
            // 上下左右都可以拖动
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            // 可以上下拖动
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        // 可以左右方向滑动消失
        swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        // 如果某个值传 0 , 表示不支持该功能
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 拖拽后回调,一般通过接口暴露给adapter, 让adapter去处理数据的交换
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // 相同 viewType 之间才能拖动交换
        if (viewHolder.getItemViewType() == target.getItemViewType()) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                //途中所有的item位置都要移动
                for (int i = fromPosition; i < toPosition; i++) {
                    adapter.onItemMove(i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    adapter.onItemMove(i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }
        return false;
    }

    /**
     * 滑动删除后回调,一般通过接口暴露给adapter, 让adapter去删除该条数据
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        // 删除数据
        adapter.onItemRemove(position);
        // adapter 刷新
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //滑动时改变Item的透明度
            final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        }
    }

    /**
     * item被选中(长按)
     * 这里改变了 item的背景色, 也可以通过接口暴露, 让adapter去处理逻辑
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            // 拖拽状态
            viewHolder.itemView.setBackgroundColor(Color.BLUE);
        } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // 滑动删除状态
            viewHolder.itemView.setBackgroundColor(Color.RED);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * item取消选中(取消长按)
     * 这里改变了 item的背景色, 也可以通过接口暴露, 让adapter去处理逻辑
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
        super.clearView(recyclerView, viewHolder);
    }

    /**
     * 是否支持长按开始拖拽,默认开启
     * 可以不开启,然后在长按 item 的时候,手动 调用 mItemTouchHelper.startDrag(myHolder) 开启,更加灵活
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return adapter.autoOpenDrag();
    }

    /**
     * 是否支持滑动删除,默认开启
     * 可以不开启,然后在长按 item 的时候,手动 调用 mItemTouchHelper.startSwipe(myHolder) 开启,更加灵活
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return adapter.autoOpenSwipe();
    }

    // 建议让 adapter 实现该接口
    public static abstract class ItemTouchAdapterImpl extends RecyclerView.Adapter {

        public abstract void onItemMove(int fromPosition, int toPosition);

        public abstract void onItemRemove(int position);

        // 是否自动开启拖拽
        protected boolean autoOpenDrag() {
            return true;
        }

        // 是否自动开启滑动删除
        protected boolean autoOpenSwipe() {
            return true;
        }
    }
}