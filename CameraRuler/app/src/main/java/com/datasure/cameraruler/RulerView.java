package com.datasure.cameraruler;

/**
 * 最内部的View
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
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
    private Paint paint;

    //position
    private float recL;
    private float recT;
    private float recR;
    private float recB;

    private static final int DRAG = 0x1;    //拖拽
    private static final int ZOOM = 0x2;    //缩放

    int mode = DRAG;
    private String text;            //the text will draw on the canvas
    private boolean isInit = false;

    private int lastX;
    private int lastY;

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

        final Resources.Theme theme = context.getTheme();
        TypedArray array = theme.obtainStyledAttributes(attrs, R.styleable.RulerView,
                defStyleAttr,0);
        if(null != array){
            int n = array.getIndexCount();
            for(int i = 0; i < n; i++){
                int attr = array.getIndex(i);
                switch(attr){
                    case R.styleable.RulerView_height:
                        height = array.getDimensionPixelSize(attr, height);
                        break;
                    case R.styleable.RulerView_width:
                        width = array.getDimensionPixelSize(attr, width);
                        break;
                }   //switch
            }   //for
        }   //if
        array.recycle();
    }

    /*
    初始化位置
     */
    private void init(){
        //计算最开始的位置,BUG
        //TODO
        //initial the painter to paint on the canvas
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        recL = getLeft();
        recT = getTop();
        recR = recL + width;
        recB = recT + height;
        isInit = true;
        Log.e("initRecX", String.valueOf(recL) +";" +  String.valueOf(recR));
        Log.e("initRecY", String.valueOf(recT) + ";" + String.valueOf(recB));
    }

    /**
     * 确定绘制的元素的大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(width, height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 响应触摸事件,共两种，一种是移动，一种是改变大小
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.e("EventMotion", String.valueOf(event.getAction()));
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:   //点击事件
                mode = DRAG;
                lastX = (int)event.getRawX();
                lastY = (int)event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:   //移动事件
                //拖拽
                if(mode == DRAG){
                    //重新计算位置
//                    changeLayout(event);
                    int dx = (int)event.getRawX() - lastX;
                    int dy = (int)event.getRawY() - lastY;

                    recL = this.getLeft() + dx;
                    recT = this.getTop() + dy;

                    recR = recL + width;
                    recB = recT + height;

                    Log.e("Layout_width", String.valueOf(getMeasuredWidth()));
                    Log.e("Layout_height", String.valueOf(getMeasuredHeight()));
                    Log.e("Layout_Left",String.valueOf(getLeft()));
                    Log.e("Layout_Top", String.valueOf(getTop()));
                    Log.e("recL", String.valueOf(recL));
                    this.layout((int) recL, (int) recT, (int) recR, (int) recB);    //layout()会调用onDraw()方法

                    //revalue the lastX and lastY
                    lastX = (int)event.getRawX();
                    lastY = (int)event.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
                //pick up
                mode = DRAG;
                break;
            default: break;
        }
        return true;
    }

    /**
     * 根据手指落点改变Ruler位置
     */
    private void changeLayout(MotionEvent event){
        recL += (event.getX() - event.getRawX());
        recT += (event.getY() - event.getRawY());
        recR = recL + width;
        recB += recT + height;
    }

   /* @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
        if(changed){
            //if the layout has changed, then recaculate the position of the layout
        }
    }*/

    /**
     * 绘制一个绿色的框
     * @param canvas paint on the canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if(!isInit){init();}
        //change the size
        RectF rect = new RectF(recL, recT, recR, recB);
        //paint the rec
        paint.setColor(Color.GREEN);
        canvas.drawRect(rect, paint);
    }
}
