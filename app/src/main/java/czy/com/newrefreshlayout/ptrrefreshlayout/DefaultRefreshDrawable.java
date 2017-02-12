package czy.com.newrefreshlayout.ptrrefreshlayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

import czy.com.newrefreshlayout.R;

/**
 * Created by wangzenghui on 16/6/7.
 */
public class DefaultRefreshDrawable extends RefreshDrawable {
    private static final String TAG = "DefaultRefreshDrawable";

    float flag = 0.2f;

    RectF mBounds;
    int mWidth;
    int mHeight;
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
    int mDrawCircleOffset;
    boolean mRunning;
    float mDegrees;

    float[] pointA = new float[2];
    float[] pointB = new float[2];

    // y = kx + n
    float k;
    float n;

    final Paint mIconPaint = new Paint();
    final Paint mFillPaint = new Paint();

    Bitmap mIcon1;
    Bitmap mIcon2;
    Bitmap mIcon3;
    Bitmap mIcon4;

    Rect mDrawablebounds;

    private Handler mHandler = new Handler();

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final SvgUtils svgUtils = new SvgUtils(paint);
    private List<SvgUtils.SvgPath> paths = new ArrayList<>();
    private final Object mSvgLock = new Object();
    private Thread mLoader;
    private int svgResourceId;
    private float progress = 0.0f;
    private boolean fill = false;
    private int width;
    private int height;
    private Bitmap mTempBitmap;
    private Canvas mTempCanvas;
    private boolean fillAfter = false;
    private int fillColor = Color.YELLOW;
    private float precent100Offset;


    public DefaultRefreshDrawable(Context context, PTRefreshLayout layout) {
        super(context, layout);

        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);

        mFillPaint.setAntiAlias(true);
        mFillPaint.setStrokeJoin(Paint.Join.ROUND);
        mFillPaint.setStrokeCap(Paint.Cap.ROUND);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(Color.BLACK);


        mIconPaint.setAntiAlias(true);
        mIconPaint.setStrokeJoin(Paint.Join.ROUND);
        mIconPaint.setStrokeCap(Paint.Cap.ROUND);
        mIconPaint.setStrokeWidth(mLineWidth);
        mIconPaint.setStyle(Paint.Style.STROKE);
        mIconPaint.setColor(Color.WHITE);

        pointA[0] = 0.6f;
        pointA[1] = 0f;

        pointB[0] = 1f;
        pointB[1] = 1f;

        k = (pointA[1] - pointB[1]) / (pointA[0] - pointB[0]);
        n = pointA[1] - k * pointA[0];

        mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_1);
        mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_2);
        mIcon3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_3);
        mIcon4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_4);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        svgResourceId = R.raw.monitor;

        precent100Offset = dp2px((int) ((1 - flag) * PTRefreshLayout.DRAG_MAX_DISTANCE));

    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mDrawablebounds = bounds;
        mHeight = getRefreshLayout().getFinalOffset();
        mWidth = mHeight;

        mBounds = new RectF(bounds.width() / 2 - mWidth / 2, bounds.top - mHeight / 2, bounds.width() / 2 + mWidth / 2, bounds.top + mHeight / 2);
        mCenterX = mBounds.centerX();
        mCenterY = mBounds.centerY();

        Log.e(TAG, "onBoundsChange: mHeight = " + mHeight + " , mCenterX = " + mCenterX + " , mCenterY = " + mCenterY + " , bounds = " + bounds);

        if (mLoader != null) {
            try {
                mLoader.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, "Unexpected error", e);
            }
        }

        if (mTempBitmap == null || (mTempBitmap.getWidth() != mWidth || mTempBitmap.getHeight() != mHeight)) {
            mTempBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mTempCanvas = new Canvas(mTempBitmap);
        }

        if (svgResourceId != 0) {
            mLoader = new Thread(new Runnable() {
                @Override
                public void run() {

                    svgUtils.load(getContext(), svgResourceId);

                    synchronized (mSvgLock) {
                        width = (int) mWidth;
                        height = (int) mWidth;
                        paths = svgUtils.getPathsForViewport(width, height);
                        Log.e(TAG, "run: paths = " + paths);
                        updatePathsPhaseLocked();
                    }
                }
            }, "SVG Loader");
            mLoader.start();
        }
    }

    private void updatePathsPhaseLocked() {
        final int count = paths.size();
        for (int i = 0; i < count; i++) {
            SvgUtils.SvgPath svgPath = paths.get(i);
            svgPath.path.reset();
            svgPath.measure.getSegment(0.0f, svgPath.length * progress, svgPath.path, true);
            // Required only for Android 4.4 and earlier
            svgPath.path.rLineTo(0.0f, 0.0f);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invalidateSelf();
            }
        });
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

    private int allOffset;
    // max offset 384;

    @Override
    public void offsetTopAndBottom(int offset) {
        allOffset += offset;
        if (mPercent < flag) {
            mOffset = 0;
        } else {
            mOffset += offset;
        }


        Log.e(TAG, "offsetTopAndBottom: mOffset = " + mOffset + " , allOffset = " + allOffset);
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

        // 画圆, 再逐渐放大
        if (mPercent > 0.2) {
            int r = mOffset / 4;

            int saveLayerCount = canvas.saveLayer(0, 0, mDrawablebounds.right, mDrawablebounds.bottom, mPaint, Canvas.ALL_SAVE_FLAG);
            canvas.translate(0, mOffset / 2);
//            canvas.clipRect(mBounds);
            canvas.drawCircle(mCenterX, mCenterY, r, mFillPaint);
            canvas.drawCircle(mCenterX, mCenterY, r, mPaint);

            mIconPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

            if (isRunning()) {
                mDegrees = mDegrees > r * 2 * 4 ? 0 : mDegrees + 2;
                Log.e(TAG, "draw: mDegrees = " + mDegrees);
                invalidateSelf();
            } else {
                mDegrees = 0;
            }

            progress = mOffset / precent100Offset;
            Log.e(TAG, "draw: progress - " + progress + " precent100Offset = " + precent100Offset + " mOffset = " + mOffset);
            if (progress > 1) {
                progress = 1;
            }
            updatePathsPhaseLocked();


            float icon_1_left = mCenterX - r - mDegrees;
            float icon_1_top = mCenterY - r;
            float icon_1_right = icon_1_left + 2 * r;
            float icon_1_bottom = icon_1_top + 2 * r;
            RectF icon_1_rect = new RectF((int) icon_1_left, (int) icon_1_top, (int) icon_1_right, (int) icon_1_bottom);
//            canvas.drawBitmap(mIcon1, null, icon_1_rect, mIconPaint);


            mTempBitmap.eraseColor(0);
            synchronized (mSvgLock) {
                final int count = paths.size();
                Log.e(TAG, "onDraw: count = " + count);
                for (int i = 0; i < count; i++) {
                    final SvgUtils.SvgPath svgPath = paths.get(i);
                    final Path path = svgPath.path;
                    final Paint paint1 = paint;
                    mTempCanvas.drawPath(path, paint1);
                    Log.e(TAG, "onDraw: drawPath");
                }
                canvas.drawBitmap(mTempBitmap, null, icon_1_rect, mIconPaint);
            }


            float icon_2_left = icon_1_right;
            float icon_2_top = icon_1_top;
            float icon_2_right = icon_2_left + 2 * r;
            float icon_2_bottom = icon_2_top + 2 * r;
            RectF icon_2_rect = new RectF((int) icon_2_left, (int) icon_2_top, (int) icon_2_right, (int) icon_2_bottom);
            canvas.drawBitmap(mIcon2, null, icon_2_rect, mIconPaint);

            float icon_3_left = icon_2_right;
            float icon_3_top = icon_2_top;
            float icon_3_right = icon_3_left + 2 * r;
            float icon_3_bottom = icon_3_top + 2 * r;
            RectF icon_3_rect = new RectF((int) icon_3_left, (int) icon_3_top, (int) icon_3_right, (int) icon_3_bottom);
            canvas.drawBitmap(mIcon3, null, icon_3_rect, mIconPaint);

            float icon_4_left = icon_3_right;
            float icon_4_top = icon_3_top;
            float icon_4_right = icon_4_left + 2 * r;
            float icon_4_bottom = icon_4_top + 2 * r;
            RectF icon_4_rect = new RectF((int) icon_4_left, (int) icon_4_top, (int) icon_4_right, (int) icon_4_bottom);
            canvas.drawBitmap(mIcon4, null, icon_4_rect, mIconPaint);

            float icon_5_left = icon_4_right;
            float icon_5_top = icon_4_top;
            float icon_5_right = icon_5_left + 2 * r;
            float icon_5_bottom = icon_5_top + 2 * r;
            RectF icon_5_rect = new RectF((int) icon_5_left, (int) icon_5_top, (int) icon_5_right, (int) icon_5_bottom);
            canvas.drawBitmap(mIcon1, null, icon_5_rect, mIconPaint);

            canvas.restoreToCount(saveLayerCount);
        }

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }
}
