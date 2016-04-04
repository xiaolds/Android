package com.datasure.cameraruler;

/**
 * 最内部的View
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 *
 * 用于在照片墙中显示绘制的尺子
 * Created by Lids on 2016/3/31.
 */
public class RulerView extends View {


    private int width = 200;              //padding width
    private int height = 200;             //padding height

    private int recL;
    private int recT;
    private int recR;
    private int recB;

    private String text;            //the text will draw on the canvas

    /**
     * Constructor
     * @param context
     */
    public RulerView(Context context) {
        super(context);
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 确定绘制的元素的大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(200,200);
    }

    /**
     * 响应触摸事件,共两种，一种是移动，一种是改变大小
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();  //手指触摸的横坐标
        float touchY = event.getY();  //手指触摸的纵坐标
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            //先确定触摸操作在范围内

            if(touchX < recL || touchX > recR ||
                    touchY < recT || touchY > recB){
                return true;
            }
        }

        float rawX = event.getRawX();
        float rawY = event.getRawY();

        float L = rawX - recL;
        float R = recR - rawY;
        float T = rawY - recT;
        float B = recB - rawY;
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            //处理移动效果
            //change the rec
            recL = (int)(touchX - L);
            recR = (int)(touchX + R);
            recT = (int)(touchY - T);
            recB = (int)(touchY + B);
            invalidate();
            Log.e("L", String.valueOf(recL));
            Log.e("T", String.valueOf(recT));
            Log.e("R", String.valueOf(recR));
            Log.e("B", String.valueOf(recB));
        }

        return true;

    }


    /**
     * 绘制一个绿色的框
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //change the size
        recL = getMeasuredWidth()/2 - width/2;
        recT = getMeasuredHeight()/2 - height/2;
        recR = recL + width;
        recB = recT + height;
        Rect rect = new Rect(recL, recT, recR, recB);
        //paint the rec
        paint.setColor(Color.BLUE);
        canvas.drawRect(rect, paint);

    }
}
