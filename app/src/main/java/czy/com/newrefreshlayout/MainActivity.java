package czy.com.newrefreshlayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import czy.com.newrefreshlayout.adapter.FragmentAdapter;
import czy.com.newrefreshlayout.fragment.RecycleViewFragment;
import czy.com.newrefreshlayout.indicator.RecyclerTabIndicator;
import czy.com.newrefreshlayout.indicator.TabIndicatorAdapter;

public class MainActivity extends AppCompatActivity {

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
}
