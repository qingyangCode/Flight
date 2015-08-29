package com.xiaoqing.flight.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.util.LogUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QingYang on 15/7/28.
 */
public class LineCharView extends View {
    private final String TAG = LineCharView.class.getSimpleName();

    private Paint mPaint;
    private Context mContext;
    private List<Double> limitData;//最大最小值数据
    private List<Double> curveData;//曲线数据
    private int LINE_Y_COUNT = 6;//y方向坐标个数
    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    private int iDisplayWidth;
    private int iDisplayHeight;
    private float scale;
    private float view_left;
    private float view_Top;
    private float view_right;
    private float view_bottom;
    private float chart_left;
    private float chart_top;
    private float chart_right;
    private float chart_bottom;
    private float view_windth;
    private float view_height;
    private float chart_width;
    private float chart_height;
    private int line_interval;

    public LineCharView(Context context) {
        super(context);
        init();
    }

    public LineCharView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public LineCharView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        scale = getDiaplayScan();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.gray_333));
        mPaint.setTextSize(13f * scale);



        limitData = new ArrayList<Double>();
        curveData = new ArrayList<Double>();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initValues();
        drawLines(canvas);
        drawLimitData(canvas);
    }

    private void initValues() {
        this.line_interval =(int) ((yMax - yMin) / 6);
    }

    private void drawLines(Canvas canvas) {

        for(int i = 0; i < LINE_Y_COUNT; i++) {
            if (i == 0) {
                //                canvas.drawText("0", chart_left - 10.0f * scale, chart_bottom
                //                        -  avg_unit_px * i, paint);
            } else{
                mPaint.setColor(getResources().getColor(R.color.gray_333));
                canvas.drawText(line_interval * i + "", chart_left - 10.0f * scale,
                        chart_bottom - line_interval * i + (mPaint.descent() - mPaint.ascent()) / 3,
                        mPaint);
                mPaint.setColor(getResources().getColor(R.color.gray_333));
                canvas.drawLine(chart_left, chart_bottom - line_interval * i, iDisplayWidth,
                        chart_bottom - 0 * i, mPaint);
            }
        }

        // 绘制 X,Y 坐标轴
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.gray_333));
        canvas.drawLine(chart_left, chart_bottom, iDisplayWidth, chart_bottom, mPaint);
        canvas.drawLine(chart_left, chart_top - 10.0f * scale, chart_left, chart_bottom, mPaint);
        //drawSpeeds(canvas,speeds);
    }

    private void drawLimitData(Canvas canvas) {
        float startX = chart_left ;
        float   endX = chart_right;
        float   endY = chart_bottom;
        float   unitX = (float)((endX - startX) / 10);
        float   unitY = (float) (chart_height / yMax);

        Path path = new Path();
        for (int i = 0; i < limitData.size(); i++) {
            //path.moveTo();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        iDisplayWidth = getScreenWidth();
        iDisplayHeight = (int)(300.0f * scale);
        this.view_left = 25 * scale;
        this.view_Top = 5 * scale;
        this.view_right = iDisplayWidth;
        this.view_bottom = iDisplayHeight - view_Top;
        this.chart_left = this.view_left + 5 * scale;
        this.chart_top = this.view_Top + 60 * scale;
        this.chart_right = this.view_right - 20 * scale;
        this.chart_bottom = this.view_bottom - 47 * scale;
        this.view_windth = view_right - view_left;
        this.view_height = view_bottom - view_Top;
        this.chart_width = chart_right - chart_left;
        this.chart_height = chart_bottom - chart_top;
        LogUtil.LOGD(TAG, "view_windth= " + view_windth + " =view_height= " + view_height);
        LogUtil.LOGD(TAG, "chart_windth= " + chart_width + " =chart_height= " + chart_height);
        setMeasuredDimension(iDisplayWidth, iDisplayHeight);
    }



    public List<Double> getLimitData() {
        return limitData;
    }

    public void setLimitData(List<Double> data) {
        this.limitData = data;
    }

    public List<Double> getCurveData() {
        return curveData;
    }

    public void setCurveData(List<Double> curveData) {
        this.curveData = curveData;
    }

    public void setXLimit(double min, double max) {
        this.xMin = min;
        this.xMax = max;
    }

    public void setYLimit(double min, double max) {
        this.yMin = min;
        this.yMax = max;
    }

    // 获取当前屏幕属性/
    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = ((Activity) mContext).getWindowManager()
                .getDefaultDisplay();
        display.getMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * 得到当前屏幕密度比例
     * @return
     */
    public float getDiaplayScan() {
        return getDisplayMetrics().scaledDensity;
    }

    public int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

}
