package com.xiaoqing.flight.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ListenerWindowSizeLinearLayout extends RelativeLayout {

	public ListenerWindowSizeLinearLayout(Context context) {
		super(context);
	}

	public ListenerWindowSizeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private OnSizeChangedListener listener;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (listener != null) {
			listener.onSizeChanged(w, h, oldw, oldh);
		}
	}

	public void setOnSizeChangedListener(OnSizeChangedListener listener) {
		this.listener = listener;
	}

	/** * Activity主窗口大小改变时的回调接口(本示例中，等价于软键盘显示隐藏时的回调接口) */
	public interface OnSizeChangedListener {
		public void onSizeChanged(int w, int h, int oldw, int oldh);
	}
}
