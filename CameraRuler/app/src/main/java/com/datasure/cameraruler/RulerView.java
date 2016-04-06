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

import java.util.Arrays;

/**
 *
 * 用于在照片墙中显示绘制的尺子
 * Created by Lids on 2016/3/31.
 */
public class RulerView extends View {


    private int width;              //padding width
    private int height;             //padding height
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

    private static final int TOUCH_AREA_ZOOM = 40;  //the area which can be touched for zooming the rectangle;

    //通过两条线将方块分为四个象限
    private static final int CENTER = 0X1;
    private static final int RIGHT = 0x3;
    private static final int BUTTOM = 0x4;
    private static final int OUTTER = 0x2;

    /**
     * Constructor
     * @param context
     */
    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final Resources.Theme theme = context.getTheme();
        TypedArray array = theme.obtainStyledAttributes(attrs, R.styleable.RulerView,
                defStyleAttr,0);
        Log.e("Array", array.toString());
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
        Log.e("initWidth", String.valueOf(width));
        Log.e("initHeight", String.valueOf(height));
        array.recycle();
    }

    /*
        初始化位置
         */
    private void init(){
        //initial the painter to paint on the canvas
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        recL = getLeft();
        recT = getTop();
        recR = recL + width;
        recB = recT + height;
        isInit = true;
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
                //drag
                if(mode == DRAG){

                    //recalculate the position
                    int dx = (int)event.getRawX() - lastX;
                    int dy = (int)event.getRawY() - lastY;

                    //判断落点的位置
                    int position = judgeTouchArea((int)event.getRawX(), (int)event.getRawY());
                    Log.e("Position",String.valueOf(position));

                    switch (position){
                        case CENTER:
                            recL = this.getLeft() + dx;
                            recT = this.getTop() + dy;
                            break;
                        case RIGHT:
                            if(width > TOUCH_AREA_ZOOM){
                                width += dx;
                            }
                            break;
                        case BUTTOM:
                            if(height > TOUCH_AREA_ZOOM){
                                height += dy;
                            }
                            break;
                        case OUTTER:
                            width += dx;
                            height += dy;
                            break;
                        default:
                            break;
                    }
                    recR = recL + width;
                    recB = recT + height;

                    //printLog
                    printLog();
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

    private void printLog(){
        Log.e("Layout_width", String.valueOf(getMeasuredWidth()));
        Log.e("Layout_height", String.valueOf(getMeasuredHeight()));
        Log.e("Left",String.valueOf(getLeft()));
        Log.e("Top", String.valueOf(getTop()));
        Log.e("width", String.valueOf(width));
        Log.e("height", String.valueOf(height));
    }

    /**
     * judge which zone the finger has pointed
     * @param lastX
     * @param lastY
     * @return
     */
    private int judgeTouchArea(int lastX, int lastY) {
        if(width < TOUCH_AREA_ZOOM || height < TOUCH_AREA_ZOOM){
            return -1;
        }
        if(lastX > recL && lastX < (recR - TOUCH_AREA_ZOOM)
                && lastY > recT && lastY < (recB - TOUCH_AREA_ZOOM)){
            return CENTER;  //1
        }
        else if(lastX > (recR - TOUCH_AREA_ZOOM) && lastX < recR
                && lastY > recT && lastY < (recB - TOUCH_AREA_ZOOM)){
            return RIGHT;  //
        }
        else if(lastX > recL && lastX < (recR - TOUCH_AREA_ZOOM)
                && lastY > (recB - TOUCH_AREA_ZOOM) && lastY < recB){
            return BUTTOM;
        }
        else if(lastX > (recR - TOUCH_AREA_ZOOM) && lastX < recR
                && lastY > (recB - TOUCH_AREA_ZOOM) && lastY < recB){
            return  OUTTER;
        }

        return -1;
    }

    /**
     * 绘制一个绿色的框
     * @param canvas paint on the canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if(!isInit){init();}
        //change the size
        RectF rect = new RectF(recL, recT, recR, recB);
        printLog();
        //paint the rec
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(TOUCH_AREA_ZOOM / 2);
        paint.setAlpha(0x240);
        canvas.drawRect(rect, paint);
    }
}
