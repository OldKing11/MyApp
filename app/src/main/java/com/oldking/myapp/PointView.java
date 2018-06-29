package com.oldking.myapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by OldKing on 2018/1/13.
 */

public class PointView extends View {

    // 最外层圆环渐变色环颜色
    private final int[] mColors = new int[] {
            0x00FFFFFF,
            0xFFFFFFFF,
            0x00FFFFFF
    };

    // 宽度
    private int width;

    // 高度
    private int height;

    // 半径
    private int radius;

    // 最外层渐变圆环画笔
    private Paint mGradientRingPaint;

    // 绘制外层圆环的矩形
    private RectF mOuterArc;

    // 圆环起始角度
    private static final float mStartAngle = 55f;

    // 圆环结束角度
    private static final float mEndAngle = 250f;

    // View默认宽高值
    private int defaultSize;

    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;

    // 中间进度圆环画笔
    private Paint mMiddleRingPaint;

    // 中间进度圆环画笔
    private Paint mMiddleProgressPaint;

    private int point;

    // 中间进度圆环的值
    private float oval4;

    // 指针图片
    private Bitmap mBitmap, drop;

//    // 指针图片宽度
//    private int mBitmapWidth;
//
//    // 指针图片高度
//    private int mBitmapHeight;

    // 指针图片画笔
    private Paint mPointerBitmapPaint;

    private float mCurrentAngle = 0f;

    public PointView(Context context) {
        this(context, null);
    }

    public PointView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * dp2px
     */
    private int dp2px(int values) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (values * density + 0.5f);
    }

    /**
     * 初始化
     */
    private void init() {
        //设置默认宽高值
        defaultSize = dp2px(250);

        //设置图片线条的抗锯齿
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        //最外层圆环渐变画笔设置
        mGradientRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置圆环渐变色渲染
        mGradientRingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        float position[] = { 0.125f, 0.5f, 0.875f };
        Shader mShader = new SweepGradient(radius, radius, mColors, position);
        mGradientRingPaint.setShader(mShader);
        mGradientRingPaint.setStrokeCap(Paint.Cap.ROUND);
        mGradientRingPaint.setStyle(Paint.Style.STROKE);
        mGradientRingPaint.setStrokeWidth(dp2px(2));

        //中间圆环画笔设置
        mMiddleRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMiddleRingPaint.setColor(0x80FFFFFF);

        //中间圆环进度画笔设置
        mMiddleProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMiddleProgressPaint.setColor(0xFFFFFFFF);

        //指针图片画笔
        mPointerBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointerBitmapPaint.setColor(0xFFFFFFFF);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zhen);
        drop = BitmapFactory.decodeResource(getResources(), R.drawable.drop);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(resolveMeasure(widthMeasureSpec, defaultSize),
                resolveMeasure(heightMeasureSpec, defaultSize));
    }

    /**
     * 根据传入的值进行测量
     */
    public int resolveMeasure(int measureSpec, int defaultSize) {

        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {

            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;

            case MeasureSpec.AT_MOST:
                //设置warp_content时设置默认值
                result = Math.min(specSize, defaultSize);
                break;
            case MeasureSpec.EXACTLY:
                //设置math_parent 和设置了固定宽高值
                break;

            default:
                result = defaultSize;
        }

        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        //确定View宽高
        width = w;
        height = h;

        //圆环半径
        radius = width / 2;

        //外层圆环
        float oval1 = radius - mGradientRingPaint.getStrokeWidth() * 0.5f;
        mOuterArc = new RectF(-oval1, -oval1, oval1, oval1);

        //中间和内层圆环
        float oval2 = radius * 5 / 8;
        float oval3 = radius * 3 / 4;
//        mInnerArc = new RectF(-oval2, -oval2, oval2, oval2);

        //中间进度圆环
        oval4 = radius * 5 / 8;
//        mMiddleProgressArc = new RectF(-oval4, -oval4, oval4, oval4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //设置画布绘图无锯齿
        canvas.setDrawFilter(mPaintFlagsDrawFilter);

//        drawArc(canvas);
//        drawCalibration(canvas);
//        drawArcText(canvas);
//        drawCenterText(canvas);
//        drawBitmapProgress(canvas);

        //画最外层的渐变圆环
        canvas.save();
        canvas.translate(width / 2, height / 2);
        canvas.rotate(90);
        canvas.drawArc(mOuterArc, mStartAngle, mEndAngle, false, mGradientRingPaint);
        canvas.restore();


        canvas.save();
        canvas.translate(width / 2, height / 2);
        canvas.drawCircle(0, 0, dp2px(3), mMiddleRingPaint);
        canvas.rotate(145);
        float dst = radius * 7 / 8;
        for (int i = 0; i < 51; i++) {
            canvas.drawCircle(dst, 0, dp2px(3), mMiddleRingPaint);
            canvas.rotate(5);
        }
        canvas.restore();

        canvas.save();
        canvas.translate(width / 2, height / 2);
        canvas.rotate(145);
        for (int i = 0; i < mCurrentAngle / 2 + 1; i++) {
            canvas.drawCircle(dst, 0, dp2px(3), mMiddleProgressPaint);
            canvas.rotate(5);
        }
        canvas.restore();

        canvas.save();
        canvas.translate(width / 2, height / 2);
        canvas.rotate(140 + mCurrentAngle / 100f * 250);
        canvas.drawBitmap(mBitmap, oval4, 0, mPointerBitmapPaint);
        canvas.restore();

        canvas.save();
        canvas.translate(width / 2, height / 2);
        canvas.rotate(140 + mCurrentAngle / 100f * 250);
        canvas.drawBitmap(drop, radius - drop.getWidth() * 0.5f, 0, mPointerBitmapPaint);
//        canvas.drawBitmap(drop, radius - mGradientRingPaint.getStrokeWidth() * 0.5f, 0, mPointerBitmapPaint);
        canvas.restore();
    }

    public void setPoint(int point) {
        this.point = point;
        startRotateAnim();
    }

    /**
     * 开始指针旋转动画
     */
    public void startRotateAnim() {

        ValueAnimator mAngleAnim = ValueAnimator.ofFloat(mCurrentAngle, point);
        mAngleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mAngleAnim.setDuration(3000);
        mAngleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mCurrentAngle = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        mAngleAnim.start();

//        ValueAnimator mNumAnim = ValueAnimator.ofInt(mMinNum, mMaxNum);
//        mNumAnim.setDuration(3000);
//        mNumAnim.setInterpolator(new LinearInterpolator());
//        mNumAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//
//                mMinNum = (int) valueAnimator.getAnimatedValue();
//                postInvalidate();
//            }
//        });
//        mNumAnim.start();
    }
}
