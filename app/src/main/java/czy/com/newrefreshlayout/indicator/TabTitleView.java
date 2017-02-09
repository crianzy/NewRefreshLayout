package czy.com.newrefreshlayout.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import czy.com.newrefreshlayout.R;


/**
 * Created by wangzenghui on 16/4/25.
 */
public class TabTitleView extends FrameLayout implements RecyclerTabIndicator.RecyclerTabView {

    private final TextView mTitleTextView;

    private float mDefaultScale;

    public TabTitleView(Context context) {
        this(context, null);
    }

    public TabTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDefaultScale = 0.85f;

        mTitleTextView = new TextView(getContext());
        mTitleTextView.setTextColor(context.getResources().getColor(R.color.white));

        addView(mTitleTextView);
        LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        mTitleTextView.setLayoutParams(lp);
    }


    public void setTitle(String title, boolean isSelected) {
        mTitleTextView.setText(title);
        if (isSelected) {
            mTitleTextView.setAlpha(1);
            mTitleTextView.setScaleX(1);
            mTitleTextView.setScaleY(1);
        } else {
            mTitleTextView.setAlpha(0.35f);
            mTitleTextView.setScaleX(mDefaultScale);
            mTitleTextView.setScaleY(mDefaultScale);
        }
    }

    @Override
    public void onScroll(float positionOffset, boolean isSelected) {
        if (isSelected) {
            float scale = 1 - (1 - mDefaultScale) * positionOffset;
            mTitleTextView.setScaleY(scale);
            mTitleTextView.setScaleX(scale);
            mTitleTextView.setAlpha(1 - 0.65f * positionOffset);
        } else {
            float scale = mDefaultScale + (positionOffset) * (1 - mDefaultScale);
            mTitleTextView.setScaleY(scale);
            mTitleTextView.setScaleX(scale);
            mTitleTextView.setAlpha(1 - (1 - positionOffset) * 0.65f);
        }
    }
}