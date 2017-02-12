package czy.com.newrefreshlayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import czy.com.newrefreshlayout.adapter.FragmentAdapter;
import czy.com.newrefreshlayout.bus.BusProvider;
import czy.com.newrefreshlayout.bus.event.RefreshLayoutPullEvent;
import czy.com.newrefreshlayout.fragment.RecycleViewFragment;
import czy.com.newrefreshlayout.indicator.RecyclerTabIndicator;
import czy.com.newrefreshlayout.indicator.TabIndicatorAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Bind(R.id.recycler_tab_indicator)
    RecyclerTabIndicator mRecyclerTabIndicator;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private FragmentAdapter mAdapter;
    private TabIndicatorAdapter mTabIndicatorAdapter;
    private List<String> mTabs = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BusProvider.register(this);


        for (int i = 0; i < 6; i++) {
            mTabs.add("tab==" + i);
            mFragmentList.add(new RecycleViewFragment());
        }

        mAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragmentList, mTabs);
        mViewPager.setAdapter(mAdapter);

        mTabIndicatorAdapter = new TabIndicatorAdapter(this, mViewPager, mTabs, true, true);
        mTabIndicatorAdapter.setTabItemPadding(getResources().getDimensionPixelOffset(R.dimen.discover_tab_indicator_item_padding_left), getResources().getDimensionPixelOffset(R.dimen.discover_tab_indicator_item_padding_right));
        mRecyclerTabIndicator.setUpWithAdapter(mTabIndicatorAdapter);

    }

    @Subscribe
    public void onPullDown(RefreshLayoutPullEvent event) {
        double flag = 0.2;
        if (event.getPercent() < flag) {
            int width = (int) ((1 - event.getPercent() * 1 / flag) * mRecyclerTabIndicator.getInitIndicatorWidth());
//            Log.e(TAG, "onPullDown: event getPercent = " + event.getPercent() + "(event.getPercent() * 5) = " + event.getPercent() * 5 + " , width = " + width);
            mRecyclerTabIndicator.setIndicatorWidth(width);
        } else {
            mRecyclerTabIndicator.setIndicatorWidth(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.unregister(this);
    }
}
