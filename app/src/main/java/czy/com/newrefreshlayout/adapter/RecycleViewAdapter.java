package czy.com.newrefreshlayout.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import czy.com.newrefreshlayout.R;

/**
 * Created by chenzhiyong on 2017/2/9.
 */

public class RecycleViewAdapter extends RecyclerView.Adapter {
    private List<String> stringList = new ArrayList<>();
    private Context mContext;

    public RecycleViewAdapter(Context context) {
        mContext = context;
        stringList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            stringList.add("String -- " + i);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_recycle_view_item, parent, false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecycleViewHolder) holder).mTxt.setText(stringList.get(position));
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    static class RecycleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt)
        TextView mTxt;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
