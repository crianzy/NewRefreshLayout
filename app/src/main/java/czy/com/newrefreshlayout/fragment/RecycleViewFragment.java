package czy.com.newrefreshlayout.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import czy.com.newrefreshlayout.R;
import czy.com.newrefreshlayout.adapter.RecycleViewAdapter;
import czy.com.newrefreshlayout.ptrrefreshlayout.PTRefreshLayout;

/**
 * Created by chenzhiyong on 2017/2/9.
 */

public class RecycleViewFragment extends Fragment {


    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;


    @Bind(R.id.refresh_layout)
    PTRefreshLayout mRefreshLayout;

    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_recycle_view, null);
        ButterKnife.bind(this, contentView);
        mRecyclerView.setAdapter(new RecycleViewAdapter(getActivity()));

        mRefreshLayout.setOnRefreshListener(new PTRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        return contentView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
