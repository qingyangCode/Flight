package com.xiaoqing.flight.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoqing.flight.entity.LineData;
import java.util.ArrayList;

public class MyLineDegreeView extends View {
	public MyLineDegreeView(Context context) {
		super(context);
		init();
		//add
	}

	public MyLineDegreeView(Context context, AttributeSet attrs,
							int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public MyLineDegreeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	// 坐标轴的上下边距
	public int LimitminX = 80;
	public int LimitmaxX = 350;
	public int LimitminY = 10;
	public int LimitmaxY = 120;

	// 这是默认的
	private int suggestW = 200;
	private int suggestH = 200;
	private Paint paint;

	private int lastX = 0;
	private int lastY = 0;


	public ArrayList<LineData> getLines() {
		return lines;
	}

	public void setLines(ArrayList<LineData> lines) {
		this.lines = lines;
	}

	private ArrayList<LineData> lines = new ArrayList<LineData>();

	@SuppressLint("NewApi")
	public void init() {
		paint = new Paint();
		paint.setAntiAlias(true);

		//ArrayList<Integer> tempDatasY = new ArrayList<Integer>();
		//tempDatasY.add(100);
		//tempDatasY.add(90);
		//tempDatasY.add(80);
		//tempDatasY.add(70);
		//tempDatasY.add(60);
		//tempDatasY.add(55);
		//lastX = 55;
		////
		//ArrayList<Integer> tempDatasX = new ArrayList<Integer>();
		//tempDatasX.add(350);
		//tempDatasX.add(340);
		//tempDatasX.add(320);
		//tempDatasX.add(300);
		//tempDatasX.add(270);
		//tempDatasX.add(250);
		//lastY = 250;

		//LineData data = new LineData();
		//data.datasX = tempDatasX;
		//data.datasY = tempDatasY;
		//setData(data);

		// 6s后动态添加一条曲线
		//refreshHandler.sendMessageDelayed(Message.obtain(), 6000);
	}

	public void setData(LineData data) {
		lines.clear();
		lines.add(data);
		invalidate();
	}

	public void addData(LineData data) {
		lines.add(data);
		invalidate();
	}

	public Handler refreshHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			//ArrayList<Integer> tempDatasY = new ArrayList<Integer>();
			//tempDatasY.add(90);
			//tempDatasY.add(80);
			//tempDatasY.add(70);
			//tempDatasY.add(60);
			//tempDatasY.add(55);
			//tempDatasY.add(40);
			////
			//ArrayList<Integer> tempDatasX = new ArrayList<Integer>();
			//tempDatasX.add(350);
			//tempDatasX.add(340);
			//tempDatasX.add(320);
			//tempDatasX.add(300);
			//tempDatasX.add(270);
			//tempDatasX.add(250);

			//LineData data = new LineData();
			//data.datasX = tempDatasX;
			//data.datasY = tempDatasY;
            //
			//setTextSize(35);
			//addData(data);
		};
	};

	// public Handler refreshHandler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// // datasY.add(lastX - 1);
	// // lastX--;
	// // int ii = (int) (Math.random() * 3);
	// // datasX.add(lastY - ii);
	// // lastY -= ii;
	// // Toast.makeText(getContext(), "ii=" + ii, 0).show();
	// // invalidate();
	// // refreshHandler.sendMessageDelayed(Message.obtain(), 1000);
	// };
	// };

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int) (suggestW + getPaddingLeft() + getPaddingRight());
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * Determines the height of this view
	 *
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = suggestH + getPaddingTop() + getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	public int screenW = 0;
	public int screenH = 0;

	// 可以调节
	public int marginLeft = 100;
	public int marginRight = 100;
	public int marginTop = 100;
	public int marginBottom = 100;

	private int getSumW() {
		screenW = getMeasuredWidth();
		int sumW = screenW - marginLeft - marginRight;
		return sumW;
	}

	private int getSumH() {
		screenH = getMeasuredHeight();
		int sumH = screenH - marginTop - marginBottom;
		return sumH;
	}

	class MyPoint {
		public int x;
		public int y;
	}

	private MyPoint getBottomLeft() {
		screenH = getMeasuredHeight();
		MyPoint point = new MyPoint();
		point.x = marginLeft;
		point.y = screenH - marginBottom;
		return point;
	}

	private MyPoint getTopLeft() {
		screenH = getMeasuredHeight();
		MyPoint point = new MyPoint();
		point.x = marginLeft;
		point.y = marginTop;
		return point;
	}

	private MyPoint getBottomRight() {
		screenW = getMeasuredWidth();
		screenH = getMeasuredHeight();
		MyPoint point = new MyPoint();
		point.x = screenW - marginRight;
		point.y = screenH - marginBottom;
		return point;
	}

	private int angleLeftLength = 30;
	private int angleTopLength = 15;

	private Path createBottomRightAngle() {
		MyPoint bottomRight = getBottomRight();
		Path path = new Path();
		path.reset();
		path.moveTo(bottomRight.x, bottomRight.y);
		path.lineTo(bottomRight.x - angleLeftLength, bottomRight.y
				- angleTopLength);
		path.lineTo(bottomRight.x - angleLeftLength, bottomRight.y
				+ angleTopLength);
		path.close();
		return path;
	}

	private Path createTopLeftAngle() {
		MyPoint topleft = getTopLeft();
		Path path = new Path();
		path.reset();
		path.moveTo(topleft.x, topleft.y);
		path.lineTo(topleft.x - angleTopLength, topleft.y + angleLeftLength);
		path.lineTo(topleft.x + angleTopLength, topleft.y + angleLeftLength);
		path.close();
		return path;
	}

	private int textColor = Color.BLUE;
	private int gridColor = Color.LTGRAY;

	private void drawXText(Canvas canvas, Paint paint) {
		paint.setColor(textColor);
		canvas.drawText("(" + LimitminX + ")", getBottomLeft().x - 30,
				getBottomLeft().y + 50, paint);
		canvas.drawText("(" + LimitmaxX + ")", getBottomRight().x - 30,
				getBottomRight().y + 50, paint);
	}

	private void drawYText(Canvas canvas, Paint paint) {
		paint.setColor(textColor);
		canvas.drawText("(" + LimitminY + ")", getBottomLeft().x - 80,
				getBottomLeft().y, paint);
		canvas.drawText("(" + LimitmaxY + ")", getTopLeft().x - 80,
				getTopLeft().y, paint);
	}

	private boolean bShowXGrid = true;
	private boolean bShowYGrid = true;

	private int xGridNum = 10;
	private int yGridNum = 10;

	private void drawYGrid(Canvas canvas, Paint paint) {
		int delY = (int) ((LimitmaxY - LimitminY) / yGridNum);
		for (int i = 1; i < yGridNum; i++) {
			int h = i * delY * getPerH();
			paint.setColor(gridColor);
			canvas.drawLine(getBottomLeft().x, getBottomLeft().y - h,
					getBottomRight().x, getBottomLeft().y - h, paint);
			paint.setColor(textColor);
			canvas.drawText("(" + (LimitminY + i * delY) + ")",
					getBottomLeft().x - 80, getBottomLeft().y - h, paint);
		}
	}

	private void drawXGrid(Canvas canvas, Paint paint) {
		int delX = (int) ((LimitmaxX - LimitminX) / xGridNum);

		for (int i = 1; i < xGridNum; i++) {
			int h = i * delX * getPerW();
			paint.setColor(gridColor);
			canvas.drawLine(getBottomLeft().x + h, getBottomLeft().y,
					getBottomLeft().x + h, getTopLeft().y, paint);
			paint.setColor(textColor);
			canvas.drawText("(" + (LimitminX + i * delX) + ")",
					getBottomLeft().x + h - 30, getBottomLeft().y + 50, paint);
		}
	}

	private int getPerW() {
		int sumW = getSumW();
		int perW = sumW / (LimitmaxX - LimitminX);
		return perW;
	}

	private int getPerH() {
		int sumH = getSumH();
		int perH = sumH / (LimitmaxY - LimitminY);
		return perH;
	}

	private int textSize = 35;

	public void setTextSize(int textSize) {
		this.textSize = textSize;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		screenW = getMeasuredWidth();
		screenH = getMeasuredHeight();

		paint.setStyle(Style.FILL);
		paint.setStrokeCap(Cap.ROUND);
		paint.setColor(Color.YELLOW);
		paint.setStrokeWidth(2);
		paint.setTextSize(textSize);

		// 画文字
		drawXText(canvas, paint);
		drawYText(canvas, paint);

		// 画网格
		if (bShowXGrid) {
			drawXGrid(canvas, paint);
		}
		if (bShowYGrid) {
			drawYGrid(canvas, paint);
		}

		// 画坐标轴
		paint.setColor(Color.RED);
		canvas.drawLine(getBottomLeft().x, getBottomLeft().y,
				getBottomRight().x, getBottomRight().y, paint);
		canvas.drawLine(getBottomLeft().x, getBottomLeft().y, getTopLeft().x,
				getTopLeft().y, paint);

		Path createBottomRightAngle = createBottomRightAngle();
		canvas.drawPath(createBottomRightAngle, paint);

		Path topLeftPath = createTopLeftAngle();
		canvas.drawPath(topLeftPath, paint);

		// 画数据线
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(Color.CYAN);
		for (int i = 0; i < lines.size(); i++) {
			LineData lineData = lines.get(i);
			Path pathFromLineData = getPathFromLineData(lineData);
			if (pathFromLineData != null) {
				canvas.drawPath(pathFromLineData, paint);
			}
		}
	}

	private Path getPathFromLineData(LineData lineData) {
		Path dataPath = new Path();
		dataPath.reset();
		int perW = getPerW();
		int perH = getPerH();
		for (int i = 0; i < lineData.getDatasY().size(); i++) {
			int x = (int) (marginLeft + (lineData.getDatasX().get(i) - LimitminX) * perW);
			int y = (int)(LimitmaxY - lineData.getDatasY().get(i)) * perH + marginTop;

			if (i == 0) {
				dataPath.moveTo(x, y);
			} else {
				dataPath.lineTo(x, y);
			}
		}
		if (lineData.getDatasY() != null && lineData.getDatasX() != null
				&& lineData.getDatasX().size() > 0) {
			if (lineData.getDatasX().size() == lineData.getDatasY().size()) {
				return dataPath;
			}
		}
		return null;
	}
}
