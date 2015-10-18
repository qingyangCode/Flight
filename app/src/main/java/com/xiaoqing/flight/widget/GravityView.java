package com.xiaoqing.flight.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoqing.flight.entity.LineCharData;

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
    int row = 30;
    // 多少列
    int colum = 17;
    // 表格单位宽度
    float w = 45;
    // 表格单位高度
    float h = 15;
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
        if(lcd==null) return;
        startX = getWidth() * 0.15f;
        startY = getHeight() * 0.2f;
        row = 30;
        colum = 17;
        w = 50;
        h = 20;
        unitH = h * 0.4f;
        textH = h;
        textW = w * 0.5f;
        endX = startX + colum * w;
        endY = startY + row * h;

        drawGrid(canvas);
        drawLinePath(canvas);
        drawLine(canvas);

    }

    // 画三个水平直线 , 四个点,重心变化曲线
    private void drawLine(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        p.setStrokeWidth(4);
        p.setAntiAlias(true);
        // 画点
        for (LineCharData.WeightData item : lcd.getWeightDatas()) {
            float y = (float) (startY + (startW - item.getWeight()) / offsetW * h);

            float x = (float) (startX + (item.getWeightCg() - startG) / offsetG * w) - p.getStrokeWidth() / 2;
            canvas.drawCircle(x, y, 10, p);
        }
        //画曲线
        p.setColor(Color.BLACK);
        p.setStrokeWidth(1);

        if (gravityListener != null) {
            Path path = new Path();
            float y = (float) (startY + (startW - minW) / offsetW * h);

            float x1 = (float) (startX + (gravityListener.getWeightCg(minW) - startG) / offsetG * w) - p.getStrokeWidth() / 2;
            path.moveTo(x1,y);
            for(float i = minW+1;i<=maxW;i+=2){
                y =  (float) (startY + (startW - i) / offsetW * h);
                x1 = (float) (startX + (gravityListener.getWeightCg(i) - startG) / offsetG * w) - p.getStrokeWidth() / 2;
                path.lineTo(x1,y);
            }
            canvas.drawPath(path,p);
        }
        // 画三条直线
        p.setStrokeWidth(4);
        p.setColor(Color.YELLOW);
       float y =  (float) (startY + (startW - lcd.getMaxFlyweight()) / offsetW * h);
        canvas.drawLine(startX,y,getWidth(),y,p);
        y =  (float) (startY + (startW - lcd.getMaxLandWeight()) / offsetW * h);
        canvas.drawLine(startX,y,getWidth(),y,p);
        y =  (float) (startY + (startW - lcd.getMaxNofuleWeight()) / offsetW * h);
        canvas.drawLine(startX,y,getWidth(),y,p);

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
            canvas.drawLine(0, y, getWidth(), y, p);
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
        offsetW = Math.ceil(Math.ceil(varW / row) / 100) * 100;
        startW = Math.ceil(maxW / offsetW) * offsetW;

        offsetG = Math.ceil(varG / colum);
        startG = Math.floor(minG / offsetG) * offsetG;


        Paint p = new Paint();
        p.setColor(Color.GRAY);
        p.setAntiAlias(true);
        // 加粗刻度
        Paint p2 = new Paint(p);
        p2.setColor(Color.BLACK);
        p2.setStrokeWidth(3);
        p2.setTextSize(30);
        p2.setTextAlign(Paint.Align.RIGHT);
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

