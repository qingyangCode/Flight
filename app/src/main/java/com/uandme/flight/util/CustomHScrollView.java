package com.uandme.flight.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by QingYang on 15/7/27.
 */
public class CustomHScrollView extends HorizontalScrollView{
    private GestureDetector mGestureDetector;
    private View.OnTouchListener mGestureListener;

    private static final String TAG = "CustomHScrollView";


    /**
     * @function CustomHScrollView constructor
     * @param context  Interface to global information about an application environment.
     *
     */
    public CustomHScrollView(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(new HScrollDetector());
        setFadingEdgeLength(0);
    }


    /**
     * @function CustomHScrollView constructor
     * @param context Interface to global information about an application environment.
     * @param attrs A collection of attributes, as found associated with a tag in an XML document.
     */
    public CustomHScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(new HScrollDetector());
        setFadingEdgeLength(0);
    }

    /**
     * @function  CustomHScrollView constructor
     * @param context Interface to global information about an application environment.
     * @param attrs A collection of attributes, as found associated with a tag in an XML document.
     * @param defStyle style of view
     */
    public CustomHScrollView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        mGestureDetector = new GestureDetector(new HScrollDetector());
        setFadingEdgeLength(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    // Return false if we're scrolling in the y direction
    class HScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(Math.abs(distanceX) > Math.abs(distanceY)) {
                return true;
            }

            return false;
        }
    }
}
