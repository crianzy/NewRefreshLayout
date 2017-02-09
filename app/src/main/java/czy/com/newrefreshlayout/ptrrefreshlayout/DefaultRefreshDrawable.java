package czy.com.newrefreshlayout.ptrrefreshlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.TypedValue;

/**
 * Created by wangzenghui on 16/6/7.
 */
public class DefaultRefreshDrawable extends RefreshDrawable {
    private static final String TAG = "DefaultRefreshDrawable";

    RectF mBounds;
    float mWidth;
    float mHeight;
    float mCenterX;
    float mCenterY;
    float mPercent;
    final float mMaxAngle = (float) (180f * .35);
    final float mRadius = dp2px(12);
    final float mLineLength = (float) (Math.PI / 180 * mMaxAngle * mRadius);
    final float mLineWidth = dp2px(2);
    final Paint mPaint = new Paint();
    final Path mPath = new Path();
    int mOffset;
    boolean mRunning;
    float mDegrees;

    float[] pointA = new float[2];
    float[] pointB = new float[2];

    // y = kx + n
    float k;
    float n;

    public DefaultRefreshDrawable(Context context, PTRefreshLayout layout) {
        super(context, layout);

        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);

        pointA[0] = 0.6f;
        pointA[1] = 0f;

        pointB[0] = 1f;
        pointB[1] = 1f;

        k = (pointA[1] - pointB[1]) / (pointA[0] - pointB[0]);
        n = pointA[1] - k * pointA[0];
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mHeight = getRefreshLayout().getFinalOffset();
        mWidth = mHeight;

        mBounds = new RectF(bounds.width() / 2 - mWidth / 2, bounds.top - mHeight / 2, bounds.width() / 2 + mWidth / 2, bounds.top + mHeight / 2);
        mCenterX = mBounds.centerX();
        mCenterY = mBounds.centerY();
    }

    @Override
    public void setPercent(float percent) {
        Log.e(TAG, "setPercent: percent = " + percent);
        mPercent = percent;
        invalidateSelf();
    }

    @Override
    public void setColorSchemeColors(int[] colorSchemeColors) {
        if (colorSchemeColors != null && colorSchemeColors.length > 0) {
            mPaint.setColor(colorSchemeColors[0]);
        }
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        Log.e(TAG, "offsetTopAndBottom: offset = " + offset);
        mOffset += offset;
        invalidateSelf();
    }

    @Override
    public void start() {
        mRunning = true;
        mDegrees = 0;
        invalidateSelf();
    }

    @Override
    public void stop() {
        mRunning = false;
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void draw(Canvas canvas) {

        canvas.save();

        canvas.translate(0, mOffset / 2);
        canvas.clipRect(mBounds);

        if (mOffset > mHeight && !isRunning()) {
            canvas.rotate((mOffset - mHeight) / mHeight * 360, mCenterX, mCenterY);
        }

        if (isRunning()) {
            canvas.rotate(mDegrees, mCenterX, mCenterY);
            mDegrees = mDegrees > -360 ? mDegrees - 10 : 0;
            invalidateSelf();
        }

        if (mPercent >= 0f) {

            float[] pointBottom = new float[2];

            pointBottom[0] = mCenterX;
            pointBottom[1] = (mCenterY + (float) Math.sqrt(Math.pow(mLineLength, 2) / 2)) * (k * mPercent + n);

            float diffX;
            if (mPercent <= 0.6f) {
                diffX = (float) Math.sqrt(Math.pow(mLineLength * 0.6f, 2) / 2);
            } else {
                diffX = (float) Math.sqrt(Math.pow(mLineLength * mPercent, 2) / 2);
            }

            mPath.reset();
            mPath.moveTo(pointBottom[0], pointBottom[1]);
            mPath.lineTo(mCenterX + diffX, pointBottom[1] - diffX);
            mPath.lineTo(mCenterX, pointBottom[1] - 2 * diffX);
            mPath.lineTo(mCenterX - diffX, pointBottom[1] - diffX);
            mPath.close();

            canvas.drawPath(mPath, mPaint);
        }

        canvas.restore();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }
}
