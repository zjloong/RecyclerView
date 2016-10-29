package com.heihei.hehe.recyclerview_demo.spansize.widgt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.heihei.hehe.recyclerview_demo.R;
import java.lang.reflect.Field;

/**
 * Describe the function of the class
 *
 * @author zhujinlong@ichoice.com
 * @date 2016/10/12
 * @time 12:45
 * @description Describe the place where the class needs to pay attention.
 */
public class SuperRefreshLayout extends SwipeRefreshLayout {

    private static OnRefreshHandler onRefreshHandler;
    private static boolean isRefresh = false;
    private Adapter adapter;
    private int mTouchSlop;
    private float mPrevX;

    public SuperRefreshLayout(Context context) {
        this(context, null);
    }

    public SuperRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorSchemeColors(0xff3b93eb);
        setProgressBackgroundColorSchemeColor(0xffffffff);
        float scale = context.getResources().getDisplayMetrics().density;
        setProgressViewEndTarget(true, (int) (64 * scale + 0.5f));
        //refreshLayout.setProgressViewOffset(false,dip2px(this,-40),dip2px(this,64));
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * 监听器
     */
    public void setOnRefreshHandler(OnRefreshHandler handler) {
        onRefreshHandler = handler;
        super.setOnRefreshListener(new OnRefreshCallBack());
    }

    /**
     * 自动刷新,原生不支持,通过反射修改字段属性
     */
    public void autoRefresh() {
        try {
            setRefreshing(true);
            Field field = SwipeRefreshLayout.class.getDeclaredField("mNotify");
            field.setAccessible(true);
            field.set(this, true);
        } catch (Exception e) {
            if(onRefreshHandler != null){
                onRefreshHandler.refresh();
            }
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        super.setRefreshing(refreshing);
        isRefresh = isRefreshing();
    }

    /**
     * 加载完毕
     * @param hasMore 是否还有下一页
     */
    public void loadComplete(boolean hasMore){
        if(adapter == null){
            throw new RuntimeException("must call method setAdapter to bind data");
        }
        adapter.setState(hasMore ? Adapter.STATE_MORE : Adapter.STATE_END);
    }

    /**
     * 加载出错
     */
    public void loadError(){
        if(adapter == null){
            throw new RuntimeException("must call method setAdapter to bind data");
        }
        adapter.setState(Adapter.STATE_ERROR);
    }

    /**
     * 只支持 RecyclerView 加载更多,且需要通过此方法设置适配器
     */
    public void setAdapter(@NonNull RecyclerView recyclerView,@NonNull SuperRefreshLayout.Adapter mAdapter) {
        adapter = mAdapter;
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (onRefreshHandler != null
                        && !isRefreshing()
                        && (adapter.getState() == Adapter.STATE_MORE || adapter.getState() == Adapter.STATE_ERROR)
                        && newState == RecyclerView.SCROLL_STATE_IDLE
                        && !ViewCompat.canScrollVertically(recyclerView, 1)
                        ) {
                    adapter.setState(Adapter.STATE_LOAIND);
                    onRefreshHandler.loadMore();
                }
            }
        });
    }

    /**
     * 如果滑动控件嵌套过深,可通过该方法控制是否可以下拉
     */
    public void setRefreshEnable(boolean enable){
        // boolean e = !ViewCompat.canScrollVertically(scrollView,-1);
        if(isEnabled() && !enable){
            setEnabled(false);
        }else if(!isEnabled() && enable){
            setEnabled(true);
        }
    }

    /**
     * 解决水平滑动冲突
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = MotionEvent.obtain(event).getX();
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);

                if (xDiff > mTouchSlop) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(event);
    }

    /**
     * 代理回调(底层回调)
     */
    private class OnRefreshCallBack implements OnRefreshListener {

        @Override
        public void onRefresh() {
            if(adapter != null && adapter.getState() != Adapter.STATE_MORE){
                adapter.setState(Adapter.STATE_MORE);
            }
            if(onRefreshHandler != null){
                onRefreshHandler.refresh();
            }
        }
    }

    /**
     * 对方开放的回调
     */
    public static abstract class OnRefreshHandler{

        public abstract void refresh();

        public void loadMore() {

        }
    }

    /**
     * 支持加载更多的代理适配器
     */
    public static abstract class Adapter extends RecyclerView.Adapter {

        static final int STATE_MORE = 0, STATE_LOAIND = 1, STATE_END = 2, STATE_ERROR = 3;
        int state = STATE_MORE;

        public void setState(int state) {
            if (this.state != state) {
                this.state = state;
                notifyItemChanged(getItemCount() - 1);
            }
        }

        public int getState() {
            return state;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return -99;
            }
            return getItemType(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == -99) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.loadmore_default_footer, parent, false)) {
                };
            } else {
                return onCreateItemHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == -99) {
                ProgressBar progressBar = (ProgressBar) holder.itemView.findViewById(R.id.loadmore_default_footer_progressbar);
                TextView textView = (TextView) holder.itemView.findViewById(R.id.loadmore_default_footer_tv);
                if (state == STATE_END) {
                    progressBar.setVisibility(View.GONE);
                    textView.setText("没有更多了");
                } else if (state == STATE_MORE) {
                    progressBar.setVisibility(View.GONE);
                    textView.setText("点击加载");
                } else if (state == STATE_LOAIND) {
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setText("加载中...");
                } else if (state == STATE_ERROR) {
                    progressBar.setVisibility(View.GONE);
                    textView.setText("加载失败,点击重新加载");
                }
                holder.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onRefreshHandler != null && !isRefresh && (state == STATE_MORE || state == STATE_ERROR)) {
                            setState(STATE_LOAIND);
                            onRefreshHandler.loadMore();
                        }
                    }
                });
            } else {
                onBindItemHolder(holder,position);
            }
        }

        @Override
        public int getItemCount() {
            return getCount() == 0 ? 0 : getCount() + 1;
        }

        public int getItemType(int position){
            return super.getItemViewType(position);
        }

        public abstract RecyclerView.ViewHolder onCreateItemHolder(ViewGroup parent, int viewType);

        public abstract void onBindItemHolder(RecyclerView.ViewHolder holder, int position);

        public abstract int getCount();

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            // 处理瀑布流模式 最后的 item 占整行
            if (holder.getLayoutPosition() == getItemCount() - 1) {
                LayoutParams lp = holder.itemView.getLayoutParams();
                if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                    p.setFullSpan(true);
                }
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            // 处理网格布局模式 最后的 item 占整行
            final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
                final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridManager.getSpanSizeLookup();
                final int lastSpanCount = gridManager.getSpanCount();
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return position == getItemCount() - 1 ? lastSpanCount :
                                (spanSizeLookup == null ? 1 : spanSizeLookup.getSpanSize(position));
                    }
                });
            }
        }
    }
}