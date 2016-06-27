package com.cocoonshu.example.expendedmenulist;

import java.util.LinkedList;
import java.util.List;

import com.cocoonshu.example.animation.Animator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Package of expended menu and its adapter
 * @author Cocoonshu
 * @date 2016-06-07 12:34:28
 */
public class DummyMenu {

    private OnItemExpendedListener mOnItemExpendedListener = null;
    private ListView               mRefListView            = null;
    private ExpendedMenuAdapter    mMenuAdapter            = null;
    private String[][]             mMenuContents           = {
            {"主题", "猴屁红", "基佬紫", "阿屁黄"},
            {"速度", "天上飞", "地上跑", "水里游"},
            {"数字", "1", "2", "3", "4", "5", "6", "7", "8"},
            {"字母", "A", "B", "C", "D", "E", "F"},
            {"关于"}
    };
    
    public interface OnItemExpendedListener {
        void onItemExpended(int position);
    }
    
    public static interface OnItemClickListener {
        void onItemClicked(int index);
    }
    
    public DummyMenu() {
        mMenuAdapter = new ExpendedMenuAdapter(mMenuContents);
    }
    
    public void setHostListView(ListView hostListView) {
        mRefListView = hostListView;
        mMenuAdapter.setInflaterContext(mRefListView.getContext());
        mRefListView.setAdapter(mMenuAdapter);
        mMenuAdapter.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClicked(int index) {
                
            }
            
        });
    }
    
    public void setOnItemExpenededListener(OnItemExpendedListener listener) {
        mOnItemExpendedListener = listener;
    }
    
    /**
     * List view adapter
     * @author Cocoonshu
     * @date 2016-06-07 13:34:33
     */
    private static class ExpendedMenuAdapter extends BaseAdapter {

        private Context             mContext             = null;
        private String[][]          mDataSet             = null;
        private List<TextView>      mTextViewPool        = new LinkedList<TextView>();
        private OnItemClickListener mOnItemClickListener = null;
        
        public ExpendedMenuAdapter(String[][] dataSet) {
            mDataSet = dataSet;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
        }

        public void setInflaterContext(Context context) {
            mContext = context;
        }
        
        @Override
        public int getCount() {
            return mDataSet == null ? 0 : mDataSet.length;
        }

        @Override
        public Object getItem(int position) {
            return mDataSet == null ? 0 : mDataSet[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                final ExpendedLinearLayout layout = new ExpendedLinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                layout.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        layout.setExpended(!layout.isExpended());
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClicked(-1);
                        }
                    }
                    
                });
                convertView = layout;
            }
            
            ExpendedLinearLayout container           = (ExpendedLinearLayout) convertView;
            String[]             contents            = (String[]) getItem(position);
            int                  layoutChildrenCount = container.getChildCount();
            int                  diffItemCount       = layoutChildrenCount - contents.length;
            if (diffItemCount > 0) {
                // Should remove child views from layout container
                // #if you don't wanna recycle child views, just remove them
                //     container.removeViews(layoutChildrenCount - diffItemCount - 1, diffItemCount);
                // #else remove and push them into a recycle pool in order to reuse next time
                for (int i = 0; i < diffItemCount; i++) {
                    TextView child = (TextView) container.getChildAt(0);
                    container.removeViewAt(0);
                    pushTextView(child);
                }
            } else if (diffItemCount < 0) {
                // Should add child views to layout container
                for (int i = 0; i < -diffItemCount; i++) {
                    TextView child = popTextView();
                    container.addView(child);
                }
            }
            
            // Traversal children and set up contents
            int childCount = container.getChildCount();
            for (int i = 0; i < childCount; i++) {
                TextView child = (TextView) container.getChildAt(i);
                if (i == 0) {
                    child.setPadding(30, 30, 30, 30); // Get padding from reources
                } else {
                    child.setPadding(100, 30, 100, 30); // Get padding from reources
                }
                String text = contents[i];
                child.setText(text);
            }
            
            return convertView;
        }
        
        private TextView popTextView() {
            int poolSize = mTextViewPool.size();
            if (poolSize == 0) {
                TextView textView = new TextView(mContext);
                textView.setBackgroundColor(0xFFFFFFFF);
                textView.setLayoutParams(
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                return textView;
            } else {
                return mTextViewPool.remove(0);
            }
        }
        
        private void pushTextView(TextView textView) {
            if (textView != null) {
                mTextViewPool.add(textView);
            }
        }
    }

    /**
     * Expended vertical linear layout
     * @author Cocoonshu
     * @date 2016-06-07 13:36:27
     */
    private static class ExpendedLinearLayout extends LinearLayout {
        
        private static final int HANDLE_REQUEST_LAYOUT = 0x0001;
        
        private boolean  mIsExpended    = false;
        private Animator mAnimator      = null;
        private Handler  mLayoutHandler = null;
        
        public ExpendedLinearLayout(Context context) {
            this(context, null);
        }
        
        public ExpendedLinearLayout(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }
        
        public ExpendedLinearLayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            initializeLayout();
        }
        
        @SuppressLint("NewApi")
        public ExpendedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            initializeLayout();
        }
        
        private void initializeLayout() {
            mAnimator = Animator.buildInterpolatorAnimator();
            mAnimator.setRange(0f, 1f);
            setOrientation(LinearLayout.VERTICAL);
            mLayoutHandler = new Handler() {
                
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                    case HANDLE_REQUEST_LAYOUT:
                        requestLayout();
                        break;
                    }
                    msg.recycle();
                }
                
            };
        }
        
        public void setExpended(boolean isExpended) {
            mIsExpended = isExpended;
            mAnimator.go(mIsExpended ? 1f : 0f);
            requestLayout();
            invalidate();
        }
        
        public boolean isExpended() {
            return mIsExpended;
        }
        
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthMeasureMode      = MeasureSpec.getMode(widthMeasureSpec);
            int heightMeasureMode     = MeasureSpec.getMode(heightMeasureSpec);
            int widthMeasureSize      = MeasureSpec.getSize(widthMeasureSpec);
            int heightMeasureSize     = MeasureSpec.getSize(heightMeasureSpec);
            int minWantedHeight       = 0;
            int maxWantedHeight       = 0;
            int currentWantedWidth    = 0;
            int currentWantedHeight   = 0;
            int wantedWidth           = 0;
            int wantedHeight          = 0;
            int childCount            = getChildCount();

            // #1
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            for (int i = 0; i < childCount; i++) {
                View               childView           = getChildAt(i);
                MarginLayoutParams childParams         = (MarginLayoutParams) childView.getLayoutParams();
                int                childMeasuredWidth  = childView.getMeasuredWidth();
                int                childMeasuredHeight = childView.getMeasuredHeight();
                int                childMarginWidth    = childMeasuredWidth + childParams.leftMargin + childParams.rightMargin;
                int                childMarginHeight   = childMeasuredHeight + childParams.topMargin + childParams.bottomMargin;
                
                if (childView.getVisibility() == View.GONE) {
                    continue;
                }
                
                if (childMarginWidth > currentWantedWidth) {
                    currentWantedWidth = childMarginWidth;
                }
                if (childMarginHeight > minWantedHeight) {
                    minWantedHeight = childMarginHeight;
                }
                maxWantedHeight += childMarginHeight;
            }
    
            // #2
            currentWantedHeight = (int) (getAnimationProgress() * (maxWantedHeight - minWantedHeight) + minWantedHeight);
            
            // #3
            // Judge width
            switch (widthMeasureMode) {
            case MeasureSpec.AT_MOST:
                wantedWidth = widthMeasureSize > currentWantedWidth ? currentWantedWidth : widthMeasureSize;
                break;
            case MeasureSpec.EXACTLY:
                wantedWidth = widthMeasureSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                wantedWidth = currentWantedWidth;
                break;
            }
            // Judge height
            switch (heightMeasureMode) {
            case MeasureSpec.AT_MOST:
                wantedHeight = heightMeasureSize > currentWantedHeight ? currentWantedHeight : heightMeasureSize;
                break;
            case MeasureSpec.EXACTLY:
                wantedHeight = heightMeasureSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                wantedHeight = currentWantedHeight;
                break;
            }
            setMeasuredDimension(wantedWidth, wantedHeight);
        }
        
        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int   childCount    = getChildCount();
            int   offsetTop     = 0;
            float offsetPrecent = getAnimationProgress();
            setChildrenDrawingOrderEnabled(true);
            for (int i = 0; i < childCount; i++) {
                View               childView         = getChildAt(i);
                MarginLayoutParams childParams       = (MarginLayoutParams) childView.getLayoutParams();
                int                childTop          = (int) (offsetPrecent * offsetTop + childParams.topMargin);
                int                childMarginHeight = childView.getMeasuredHeight() + childParams.topMargin + childParams.bottomMargin;
                childView.layout(
                        childParams.leftMargin,
                        childTop,
                        childParams.leftMargin + childView.getMeasuredWidth(),
                        childTop + childView.getMeasuredHeight());
                offsetTop += childMarginHeight;
            }

            if (onAnimation()) {
                mLayoutHandler.obtainMessage(HANDLE_REQUEST_LAYOUT).sendToTarget();
            }
        }
        
        @Override
        protected int getChildDrawingOrder(int childCount, int i) {
            int order = childCount - i - 1;
            return order < 0 ? 0 : order;
        }

        private boolean onAnimation() {
            return mAnimator.animate();
        }
        
        private float getAnimationProgress() {
            return mAnimator.getProgress();
        }
    }
    
}
