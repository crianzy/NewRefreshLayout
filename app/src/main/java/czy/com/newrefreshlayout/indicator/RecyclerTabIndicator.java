package czy.com.newrefreshlayout.indicator;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import czy.com.newrefreshlayout.R;

/**
 * Created by wangzenghui on 16/4/25.
 */
public class RecyclerTabIndicator extends RecyclerView {

    protected static final long DEFAULT_SCROLL_DURATION = 200;
    protected static final float DEFAULT_POSITION_THRESHOLD = 0.6f;
    protected static final float POSITION_THRESHOLD_ALLOWABLE = 0.001f;

    protected Paint mIndicatorPaint;
    protected int mTabBackgroundResId;
    protected int mTabMinWidth;
    protected int mTabMaxWidth;
    protected int mTabTextAppearance;
    protected int mTabSelectedTextColor;
    protected boolean mTabSelectedTextColorSet;
    protected int mTabPaddingStart;
    protected int mTabPaddingTop;
    protected int mTabPaddingEnd;
    protected int mTabPaddingBottom;
    protected int mIndicatorHeight;

    protected LinearLayoutManager mLinearLayoutManager;
    protected RecyclerOnScrollListener mRecyclerOnScrollListener;
    protected ViewPager mViewPager;
    protected Adapter<?> mAdapter;

    protected int mIndicatorPosition;
    protected int mIndicatorOffset;
    protected int mIndicatorWidth;
    protected int mInitIndicatorWidth;
    protected int mScrollOffset;
    protected float mOldPositionOffset;
    protected float mPositionThreshold;
    protected boolean mRequestScrollToTab;

    public RecyclerTabIndicator(Context context) {
        this(context, null);
    }

    public RecyclerTabIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerTabIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mIndicatorPaint = new Paint();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLinearLayoutManager.canScrollHorizontally();
        setLayoutManager(mLinearLayoutManager);
        setItemAnimator(null);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.rtl_RecyclerTabLayout,
                defStyle, 0);
        setIndicatorColor(a.getColor(R.styleable
                .rtl_RecyclerTabLayout_rtl_tabIndicatorColor, 0));
        setIndicatorHeight(a.getDimensionPixelSize(R.styleable
                .rtl_RecyclerTabLayout_rtl_tabIndicatorHeight, 0));

        mInitIndicatorWidth = a.getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabIndicatorWidth, 0);
        setIndicatorWidth(mInitIndicatorWidth);

        mTabTextAppearance = a.getResourceId(R.styleable.rtl_RecyclerTabLayout_rtl_tabTextAppearance,
                0);

        mTabPaddingStart = mTabPaddingTop = mTabPaddingEnd = mTabPaddingBottom = a
                .getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabPadding, 0);
        mTabPaddingStart = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingStart, mTabPaddingStart);
        mTabPaddingTop = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingTop, mTabPaddingTop);
        mTabPaddingEnd = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingEnd, mTabPaddingEnd);
        mTabPaddingBottom = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingBottom, mTabPaddingBottom);

        if (a.hasValue(R.styleable.rtl_RecyclerTabLayout_rtl_tabSelectedTextColor)) {
            mTabSelectedTextColor = a
                    .getColor(R.styleable.rtl_RecyclerTabLayout_rtl_tabSelectedTextColor, 0);
            mTabSelectedTextColorSet = true;
        }

        mTabMinWidth = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabMinWidth, 0);
        mTabMaxWidth = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabMaxWidth, 0);
        mTabBackgroundResId = a
                .getResourceId(R.styleable.rtl_RecyclerTabLayout_rtl_tabBackground, 0);
        a.recycle();

        mPositionThreshold = DEFAULT_POSITION_THRESHOLD;
    }


    public void setInitIndicatorWidth(int initIndicatorWidth) {
        mInitIndicatorWidth = initIndicatorWidth;
    }

    public int getInitIndicatorWidth() {
        return mInitIndicatorWidth;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        super.onDetachedFromWindow();
    }


    public void setIndicatorColor(int color) {
        mIndicatorPaint.setColor(color);
    }

    public void setIndicatorHeight(int indicatorHeight) {
        mIndicatorHeight = indicatorHeight;
    }

    public void setIndicatorWidth(int indicatorWidth) {
        mIndicatorWidth = indicatorWidth;
        invalidate();
    }

    public void setAutoSelectionMode(boolean autoSelect) {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        if (autoSelect) {
            mRecyclerOnScrollListener = new RecyclerOnScrollListener(this, mLinearLayoutManager);
            addOnScrollListener(mRecyclerOnScrollListener);
        }
    }

    public void setPositionThreshold(float positionThreshold) {
        mPositionThreshold = positionThreshold;
    }

    public void setTabMinWidth(int tab_min_width) {
        this.mTabMinWidth = tab_min_width;
    }

    public void setUpWithAdapter(RecyclerTabIndicator.Adapter<?> adapter) {
        mAdapter = adapter;
        mViewPager = adapter.getViewPager();
        if (mViewPager.getAdapter() == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }
        mViewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener(this, mAdapter.startEmpty()));
        setAdapter(adapter);
        scrollToTab(mAdapter.startEmpty() ? mViewPager.getCurrentItem() + 1 : mViewPager.getCurrentItem());
    }

    public void setCurrentItem(int position, boolean smoothScroll) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(position, smoothScroll);
            scrollToTab(mAdapter.startEmpty() ? mViewPager.getCurrentItem() + 1 : mViewPager.getCurrentItem());
            return;
        }

        if (smoothScroll && position != mIndicatorPosition) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                startAnimation(position);
            } else {
                scrollToTab(position); //FIXME add animation
            }

        } else {
            scrollToTab(position);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void startAnimation(final int position) {

        float distance = 1;

        View view = mLinearLayoutManager.findViewByPosition(position);
        if (view != null) {
            float currentX = view.getX() + view.getMeasuredWidth() / 2.f;
            float centerX = getMeasuredWidth() / 2.f;
            distance = Math.abs(centerX - currentX) / view.getMeasuredWidth();
        }

        ValueAnimator animator;
        if (position < mIndicatorPosition) {
            animator = ValueAnimator.ofFloat(distance, 0);
        } else {
            animator = ValueAnimator.ofFloat(-distance, 0);
        }
        animator.setDuration(DEFAULT_SCROLL_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollToTab(position, (float) animation.getAnimatedValue(), true);
            }
        });
        animator.start();
    }

    protected void scrollToTab(int position) {
        scrollToTab(position, 0, false);
        mAdapter.setCurrentIndicatorPosition(position);
        mAdapter.notifyDataSetChanged();
    }

    protected void scrollToTab(int position, float positionOffset, boolean fitIndicator) {
        int scrollOffset = 0;

        View selectedView = mLinearLayoutManager.findViewByPosition(position);
        View nextView = mLinearLayoutManager.findViewByPosition(position + 1);

        if (selectedView != null) {
            int width = getMeasuredWidth();
            float scroll1 = width / 2.f - selectedView.getMeasuredWidth() / 2.f;

            if (nextView != null) {
                float scroll2 = width / 2.f - nextView.getMeasuredWidth() / 2.f;

                float scroll = scroll1 + (selectedView.getMeasuredWidth() - scroll2);
                float dx = scroll * positionOffset;
                scrollOffset = (int) (scroll1 - dx);

                mScrollOffset = (int) dx;
                mIndicatorOffset = (int) ((scroll1 - scroll2) * positionOffset);

                if (nextView instanceof RecyclerTabView) {
                    ((RecyclerTabView) nextView).onScroll(positionOffset, false);
                }
            } else {
                scrollOffset = (int) scroll1;
                mScrollOffset = 0;
                mIndicatorOffset = 0;
            }

            if (fitIndicator) {
                mScrollOffset = 0;
                mIndicatorOffset = 0;
            }

            if (mAdapter != null && mIndicatorPosition == position) {
                updateCurrentIndicatorPosition(position, positionOffset - mOldPositionOffset,
                        positionOffset);
            }

            if (selectedView instanceof RecyclerTabView) {
                ((RecyclerTabView) selectedView).onScroll(positionOffset, true);
            }

            mIndicatorPosition = position;

        } else {
            if (getMeasuredWidth() > 0 && mTabMinWidth == mTabMaxWidth) { //fixed size
                int width = mTabMinWidth;
                int offset = (int) (positionOffset * -width);
                int leftOffset = (int) ((getMeasuredWidth() - width) / 2.f);
                scrollOffset = offset + leftOffset;
            }
            mRequestScrollToTab = true;
        }

        mLinearLayoutManager.scrollToPositionWithOffset(position, scrollOffset);

        if (mIndicatorHeight > 0) {
            invalidate();
        }

        mOldPositionOffset = positionOffset;
    }

    protected void updateCurrentIndicatorPosition(int position, float dx, float positionOffset) {
        int indicatorPosition = -1;
        if (dx > 0 && positionOffset >= mPositionThreshold - POSITION_THRESHOLD_ALLOWABLE) {
            indicatorPosition = position + 1;

        } else if (dx < 0 && positionOffset <= 1 - mPositionThreshold + POSITION_THRESHOLD_ALLOWABLE) {
            indicatorPosition = position;
        }
        if (indicatorPosition >= 0 && indicatorPosition != mAdapter.getCurrentIndicatorPosition()) {
            mAdapter.setCurrentIndicatorPosition(indicatorPosition);
//            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onDraw(Canvas canvas) {
        View view = mLinearLayoutManager.findViewByPosition(mIndicatorPosition);
        if (view == null) {
            if (mRequestScrollToTab) {
                mRequestScrollToTab = false;
                scrollToTab(mAdapter.startEmpty() ? mViewPager.getCurrentItem() + 1 : mViewPager.getCurrentItem());
            }
            return;
        }

        mRequestScrollToTab = false;

        int left = view.getLeft() + mScrollOffset - mIndicatorOffset;
        int right = view.getRight() + mScrollOffset + mIndicatorOffset;
        int top = getHeight() - mIndicatorHeight;
        int bottom = getHeight();

        int width = right - left;
        if (mIndicatorWidth > 0) {
            left = left + (width - mIndicatorWidth) / 2;
            right = right - (width - mIndicatorWidth) / 2;

            canvas.drawRect(left, top, right, bottom, mIndicatorPaint);
        }
    }


    protected static class RecyclerOnScrollListener extends OnScrollListener {

        protected RecyclerTabIndicator mRecyclerTabLayout;
        protected LinearLayoutManager mLinearLayoutManager;

        public RecyclerOnScrollListener(RecyclerTabIndicator recyclerTabLayout,
                                        LinearLayoutManager linearLayoutManager) {
            mRecyclerTabLayout = recyclerTabLayout;
            mLinearLayoutManager = linearLayoutManager;
        }

        public int mDx;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mDx += dx;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    if (mDx > 0) {
                        selectCenterTabForRightScroll();
                    } else {
                        selectCenterTabForLeftScroll();
                    }
                    mDx = 0;
                    break;
                case SCROLL_STATE_DRAGGING:
                case SCROLL_STATE_SETTLING:
            }
        }

        protected void selectCenterTabForRightScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;
            for (int position = first; position <= last; position++) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() + view.getWidth() >= center) {
                    mRecyclerTabLayout.setCurrentItem(position, false);
                    break;
                }
            }
        }

        protected void selectCenterTabForLeftScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;
            for (int position = last; position >= first; position--) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() <= center) {
                    mRecyclerTabLayout.setCurrentItem(position, false);
                    break;
                }
            }
        }
    }

    protected static class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private final RecyclerTabIndicator mRecyclerTabLayout;
        private int mScrollState;
        private boolean mStartEmpty;

        public ViewPagerOnPageChangeListener(RecyclerTabIndicator recyclerTabLayout) {
            this(recyclerTabLayout, false);
        }

        public ViewPagerOnPageChangeListener(RecyclerTabIndicator recyclerTabIndicator, boolean startEmpty) {
            mRecyclerTabLayout = recyclerTabIndicator;
            mStartEmpty = startEmpty;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mRecyclerTabLayout.scrollToTab(mStartEmpty ? position + 1 : position, positionOffset, false);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                //其他的 tab item 的颜色变换
                mRecyclerTabLayout.scrollToTab(mStartEmpty ? position + 1 : position);
            }
        }
    }

    public static abstract class Adapter<T extends RecyclerView.ViewHolder>
            extends RecyclerView.Adapter<T> {

        protected ViewPager mViewPager;
        protected int mIndicatorPosition;
        protected boolean mStartEmpty;

        public Adapter(ViewPager viewPager) {
            this(viewPager, false);
        }

        public Adapter(ViewPager viewPager, boolean startEmpty) {
            this.mViewPager = viewPager;
            this.mStartEmpty = startEmpty;
        }

        public ViewPager getViewPager() {
            return mViewPager;
        }

        public void setCurrentIndicatorPosition(int indicatorPosition) {
            mIndicatorPosition = indicatorPosition;
        }

        public int getCurrentIndicatorPosition() {
            return mIndicatorPosition;
        }

        public boolean startEmpty() {
            return mStartEmpty;
        }
    }

    public interface RecyclerTabView {
        void onScroll(float positionOffset, boolean isSelected);
    }

}