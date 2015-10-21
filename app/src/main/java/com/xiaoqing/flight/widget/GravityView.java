package com.xiaoqing.flight.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoqing.flight.entity.LineCharData;
import com.xiaoqing.flight.util.LineMeetUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liushenghan on 15/10/17.
 * 绘制重心变化图标
 */
public class GravityView extends View {
    // 表格起点
    float startX;
    float startY;
    float endX;
    float endY;
    // 多少行
    int row;
    // 多少列
    int colum;
    // 表格单位宽度
    float w;
    // 表格单位高度
    float h;
    // 刻度标尺的长度
    float unitH = h * 0.4f;
    // 文本高度 文本长度和高度 要根据 p2来计算得出不然会不准确
    float textH = h;
    // 文本宽度
    float textW = w * 0.5f;
    // 重量和中心的最值
    float maxW = Integer.MIN_VALUE;
    float minW = Integer.MAX_VALUE;
    float maxG = Integer.MIN_VALUE;
    float minG = Integer.MAX_VALUE;
    private LineCharData lcd;
    private double startG;// 最小值
    private double offsetG;
    private double startW;// 最大值
    private double offsetW;
    private List<Point> points = new ArrayList<>();

    private GetGravity gravityListener;

    public GravityView(Context context) {
        super(context);
    }

    public GravityView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GravityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int h = w * 4 / 5;
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lcd == null) return;
        startX = getWidth() * 0.15f;
        startY = getHeight() * 0.1f;
        row = 35;
        colum = 22;
        w = getWidth() * 0.8f / 23;
        h = getHeight() * 0.8f / 35;
        unitH = h * 0.4f;
        endX = startX + colum * w;
        endY = startY + row * h;
        // 这三个顺序不能变会在drawGrid 进行一下数值的初始化
        drawGrid(canvas);
        drawLinePath(canvas);
        drawLine(canvas);

    }

    // 画三个水平直线 , 四个点,重心变化曲线
    private void drawLine(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        p.setAntiAlias(true);
        // 画点
        for (LineCharData.WeightData item : lcd.getWeightDatas()) {
            float y = (float) (startY + (startW - item.getWeight()) / offsetW * h);

            float x = (float) (startX + (item.getWeightCg() - startG) / offsetG * w) - p.getStrokeWidth() / 2;
            canvas.drawCircle(x, y, 3, p);
        }
        //画曲线
        p.setColor(Color.BLACK);
        p.setStrokeWidth(1);
        p.setStrokeJoin(Paint.Join.ROUND);
        maxW = Integer.MIN_VALUE;
        minW = Integer.MAX_VALUE;
        for (LineCharData.WeightData weightData : lcd.getWeightDatas()) {
            maxW = Math.max(maxW, weightData.getWeight());
            minW = Math.min(minW, weightData.getWeight());
        }
        if (gravityListener != null) {
            Path path = new Path();
            float ty, tx;
            float y = (float) (startY + (startW - minW) / offsetW * h);

            float x1 = (float) (startX + (gravityListener.getWeightCg(minW) - startG) / offsetG * w) - p.getStrokeWidth() / 2;
            path.moveTo(x1, y);
            for (float i = minW + 1; i <= maxW; i += 50) {
                ty = (float) (startY + (startW - i) / offsetW * h);
                tx = (float) (startX + (gravityListener.getWeightCg(i) - startG) / offsetG * w) - p.getStrokeWidth() / 2;
                canvas.drawLine(x1, y, tx, ty, p);
                y = ty;
                x1 = tx;
            }
            ty = (float) (startY + (startW - maxW) / offsetW * h);
            tx = (float) (startX + (gravityListener.getWeightCg(maxW) - startG) / offsetG * w) - p.getStrokeWidth() / 2;
            canvas.drawLine(x1, y, tx, ty, p);
        }
        // 画三条直线
        p.setStrokeWidth(4);
        p.setColor(Color.RED);
        float y = (float) (startY + (startW - lcd.getMaxFlyweight()) / offsetW * h);
        LineMeetUtils.Point point = getPoint(lcd.getMaxFlyweight());
        float x1 = (float) (startX + (point.x - startG) / offsetG * w) - p.getStrokeWidth() / 2;
        float x2 = (float) (startX + (point.y - startG) / offsetG * w) - p.getStrokeWidth() / 2;

        canvas.drawLine(x1, y, x2, y, p);
        y = (float) (startY + (startW - lcd.getMaxLandWeight()) / offsetW * h);
        point = getPoint(lcd.getMaxLandWeight());
        x1 = (float) (startX + (point.x - startG) / offsetG * w) - p.getStrokeWidth() / 2;
        x2 = (float) (startX + (point.y - startG) / offsetG * w) - p.getStrokeWidth() / 2;

        canvas.drawLine(x1, y, x2, y, p);
        y = (float) (startY + (startW - lcd.getMaxNofuleWeight()) / offsetW * h);
        point = getPoint(lcd.getMaxNofuleWeight());
        x1 = (float) (startX + (point.x - startG) / offsetG * w) - p.getStrokeWidth() / 2;
        x2 = (float) (startX + (point.y - startG) / offsetG * w) - p.getStrokeWidth() / 2;

        canvas.drawLine(x1, y, x2, y, p);


    }

    private LineMeetUtils.Point getPoint(float weight) {
        double x = 0, x1 = 0;
        Double f = 1d;
        LineMeetUtils.Point p1 = new LineMeetUtils.Point(startG, weight * 1.0);
        LineMeetUtils.Point p2 = new LineMeetUtils.Point(startG + colum * offsetG, weight * 1.0);
        for (int i = 0; i < lcd.getWeightLimitDatas().size() - 1; i++) {

            LineCharData.WeightLimitData w1 = lcd.getWeightLimitDatas().get(i);
            LineCharData.WeightLimitData w2 = lcd.getWeightLimitDatas().get(i + 1);
            LineMeetUtils.Point p3 = new LineMeetUtils.Point(w1.getWeightCg1(), w1.getWeight());
            LineMeetUtils.Point p4 = new LineMeetUtils.Point(w2.getWeightCg1(), w2.getWeight());
            boolean meet = LineMeetUtils.Meet(p1, p2, p3, p4);
            LineMeetUtils.Point inter1 = LineMeetUtils.Inter(p1, p2, p3, p4);
            if (meet) {
                f = inter1.x;
                if (f.isNaN()) {
                    x = w2.getWeightCg1();
                    if (weight == w1.getWeight()) x = w1.getWeightCg1();
                } else {
                    x = f;
                }
            }
            p3 = new LineMeetUtils.Point(w1.getWeightCg2(), w1.getWeight());
            p4 = new LineMeetUtils.Point(w2.getWeightCg2(), w2.getWeight());
            LineMeetUtils.Point inter2 = LineMeetUtils.Inter(p1, p2, p3, p4);

            meet = LineMeetUtils.Meet(p1, p2, p3, p4);
            if (meet) {
                f = inter2.x;
                if (f.isNaN()) {
                    x1 = w2.getWeightCg2();
                    if (weight == w1.getWeight()) x = w1.getWeightCg2();

                } else {
                    x1 = f;
                }
            }
        }

        return new LineMeetUtils.Point(x, x1);
    }

    // 画最外围的多边形
    private void drawLinePath(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.GRAY);
        p.setStrokeWidth(4);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);

        points.clear();
        Collections.reverse(lcd.getWeightLimitDatas());
        for (LineCharData.WeightLimitData item : lcd.getWeightLimitDatas()) {

            float y = (float) (startY + (startW - item.getWeight()) / offsetW * h);

            float x1 = (float) (startX + (item.getWeightCg1() - startG) / offsetG * w) - p.getStrokeWidth() / 2;

            float x2 = (float) (startX + (item.getWeightCg2() - startG) / offsetG * w) - p.getStrokeWidth() / 2;
            // 用来标识 bug使用
//            canvas.drawLine(0, y, getWidth(), y, p);
            points.add(0, new Point(x1, y));
            points.add(new Point(x2, y));
        }
        Collections.reverse(lcd.getWeightLimitDatas());
        p.setColor(Color.BLACK);
        p.setStrokeJoin(Paint.Join.ROUND);
        Path path = new Path();
        for (int i = 0; i < points.size(); i++) {
            if (i == 0) path.moveTo(points.get(i).x, points.get(i).y);
            else path.lineTo(points.get(i).x, points.get(i).y);
        }
        path.close();
        canvas.drawPath(path, p);
    }

    /**
     * 绘制网格
     *
     * @param canvas
     */
    private void drawGrid(Canvas canvas) {
        for (LineCharData.WeightLimitData item : lcd.getWeightLimitDatas()) {
            maxG = Math.max(Math.max(maxG, item.getWeightCg1()), item.getWeightCg2());
            minG = Math.min(Math.min(minG, item.getWeightCg1()), item.getWeightCg2());
            maxW = Math.max(maxW, item.getWeight());
            minW = Math.min(minW, item.getWeight());
        }


        //计算最大重量的起始值,偏移量,计算最小重心的起始值,偏移量
        float varG = maxG - minG;
        float varW = maxW - minW;
        // 这个地方最好自己计算否则其中的点会在表格的边界上
        offsetW = Math.ceil(Math.ceil(varW / row) / 50) * 50;
        startW = Math.ceil(maxW / offsetW) * offsetW + 2 * offsetW;

        offsetG = Math.ceil(varG / colum);
        startG = Math.floor(minG / offsetG) * offsetG - 2 * offsetG;


        Paint p = new Paint();
        p.setColor(Color.GRAY);
        p.setAntiAlias(true);
        // 加粗刻度
        Paint p2 = new Paint(p);
        p2.setColor(Color.BLACK);
        p2.setStrokeWidth(3);
        p2.setTextSize(30);
        p2.setTextAlign(Paint.Align.RIGHT);
        Rect rect = new Rect();
        p2.getTextBounds(String.valueOf(maxW), 0, String.valueOf(maxW).length(), rect);
        float textL = rect.width();
        textW = textL / String.valueOf(maxW).length();
        startX = textL + 4 * textW;
        textH = rect.height();
        // 基础网格
        for (int i = 0; i <= row; i++) {
            float y = startY + i * h;
            canvas.drawLine(startX - p.getStrokeWidth() / 2, y, startX + colum * w - p.getStrokeWidth() / 2, y, p);
            if (i != row) {
                canvas.drawLine(startX - unitH - p.getStrokeWidth() / 2, y, startX - p.getStrokeWidth() / 2, y, p2);
                if (i % 2 == 1)
                    canvas.drawText(String.valueOf((int) (startW - i * offsetW)), startX - textW, y
                            + textH * 0.5f, p2);
            }

        }
        float sy = startY - 0.3f * h;
        float ey = startY + row * h;

        p2.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i <= colum; i++) {
            float x = startX + i * w;
            canvas.drawLine(x, sy, x, ey, p);
            if (i % 5 == 0) {
                canvas.drawLine(x - p.getStrokeWidth() / 2, ey, x - p.getStrokeWidth() / 2, ey + unitH, p2);
                canvas.drawText(String.valueOf((int) (startG + i * offsetG)), x,
                        ey + unitH + textH * 1.5f, p2);
            }
        }

        canvas.drawLine(startX - p.getStrokeWidth() / 2, startY - unitH, startX - p.getStrokeWidth() / 2, startY + row * h
                + unitH, p2);
        canvas.drawLine(startX - p.getStrokeWidth() / 2, startY + row * h, startX + colum * w - p.getStrokeWidth() / 2, startY
                + row * h, p2);
    }

    // 绘制边界线
    private void drawRect(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);
    }

    public LineCharData getLcd() {
        return lcd;
    }

    public void setLcd(LineCharData lcd) {
        this.lcd = lcd;
        invalidate();
    }

    class Point {
        float x, y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public interface GetGravity {
        float getWeightCg(float weight);
    }

    public void setGravityListener(GetGravity gravityListener) {
        this.gravityListener = gravityListener;
    }
}

