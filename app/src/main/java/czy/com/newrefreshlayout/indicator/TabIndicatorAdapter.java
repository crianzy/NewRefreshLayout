package czy.com.newrefreshlayout.indicator;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import czy.com.newrefreshlayout.util.AppUtil;

/**
 * Created by wangzenghui on 16/4/25.
 */
public class TabIndicatorAdapter extends RecyclerTabIndicator.Adapter {
    private static final String TAG = "TabIndicatorAdapter";

    private Context mContext;
    private List<String> mTabs;

    private static final int NORAML_TAB = 0;
    private static final int EMPTY_TAB = 1;

    private boolean mStartEmpty;
    private boolean mEndEmpty;
    private int mPaddingLeft;
    private int mPaddingRight;

    public TabIndicatorAdapter(Context context, ViewPager viewPager, List<String> tabs) {
        this(context, viewPager, tabs, false, false);
    }

    /**
     * @param context
     * @param viewPager
     * @param tabs
     * @param startEmpty 是否允许第一项的Title 在滑动到最左边时位于中间
     * @param endEmpty   是否允许最后一项的Title 在滑动到最右侧时位于中间
     */
    public TabIndicatorAdapter(Context context, ViewPager viewPager, List<String> tabs, boolean startEmpty, boolean endEmpty) {
        super(viewPager, startEmpty);
        mStartEmpty = startEmpty;
        mEndEmpty = endEmpty;
        if (startEmpty) {
            tabs.add(0, "");
        }

        if (endEmpty) {
            tabs.add("");
        }

        this.mContext = context;
        this.mTabs = tabs;
    }


    public void setTabItemPadding(int paddingLeft, int paddingRight) {
        mPaddingLeft = paddingLeft;
        mPaddingRight = paddingRight;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = new TabTitleView(mContext);
        if (viewType == NORAML_TAB) {
            int displayHeight = RecyclerView.LayoutParams.MATCH_PARENT;
            int displayWidth = RecyclerView.LayoutParams.WRAP_CONTENT;
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(displayWidth, displayHeight);
            view.setLayoutParams(params);
            view.setPadding(mPaddingLeft, 0, mPaddingRight, 0);
            return new TabViewHolder(view);

        } else {
            int displayHeight = RecyclerView.LayoutParams.MATCH_PARENT;
            int displayWidth = AppUtil.getScreenWidth(mContext)  / 2;
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(displayWidth, displayHeight);
            view.setLayoutParams(params);
            return new EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TabViewHolder) {
            ((TabTitleView) holder.itemView).setTitle(mTabs.get(position), getCurrentIndicatorPosition() == position);

        } else if (holder instanceof EmptyViewHolder) {
            if (startEmpty() && position == 0 && !TextUtils.isEmpty(mTabs.get(1))) {
                long time1 = System.currentTimeMillis();
                EmptyViewHolder tabViewHolder = (EmptyViewHolder) holder;
                int tabWidth = (AppUtil.getScreenWidth(mContext) - getTabViewWidth(1)) / 2;
                tabViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(tabWidth, RecyclerView.LayoutParams.MATCH_PARENT));
                long time2 = System.currentTimeMillis();

            } else if (mEndEmpty && position == getItemCount() - 1 && !TextUtils.isEmpty(mTabs.get(getItemCount() - 2))) {
                EmptyViewHolder tabViewHolder = (EmptyViewHolder) holder;
                int tabWidth = (AppUtil.getScreenWidth(mContext)  - getTabViewWidth(getItemCount() - 2)) / 2;
                tabViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(tabWidth, RecyclerView.LayoutParams.MATCH_PARENT));
            }
        }
    }

    private int getTabViewWidth(int pos) {
        TabTitleView view = new TabTitleView(mContext);
        int displayHeight = RecyclerView.LayoutParams.MATCH_PARENT;
        int displayWidth = RecyclerView.LayoutParams.WRAP_CONTENT;
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(displayWidth, displayHeight);
        view.setLayoutParams(params);
        view.setPadding(mPaddingLeft, 0, mPaddingRight, 0);
        view.setTitle(mTabs.get(pos), true);
        view.measure(0, 0);
        return view.getMeasuredWidth();
    }

    @Override
    public int getItemViewType(int position) {
        if (mStartEmpty) {
            if (position == 0) {
                return EMPTY_TAB;
            }
        }

        if (mEndEmpty) {
            if (position == getItemCount() - 1) {
                return EMPTY_TAB;
            }
        }
        return NORAML_TAB;
    }

    @Override
    public int getItemCount() {
        if (mTabs != null) {
            return mTabs.size();
        }
        return 0;
    }

    public class TabViewHolder extends RecyclerView.ViewHolder {
        public TabViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getViewPager().setCurrentItem(mStartEmpty ? getAdapterPosition() - 1 : getAdapterPosition(), false);
                }
            });
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
