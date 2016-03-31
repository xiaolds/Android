package com.datasure.cameraruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 *
 * 用于在照片墙中显示绘制的尺子
 * Created by Lids on 2016/3/31.
 */
public class RulerView extends View {

    private int measuredHeight;     //margin height
    private int measuredWidth;      //margin width

    private int width;              //padding width
    private int height;             //padding height

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

        measuredHeight = attrs.getAttributeResourceValue(null,"marginH",0);
        measuredWidth = attrs.getAttributeResourceValue(null, "marginW", 0);

        width = attrs.getAttributeResourceValue(null, "width", 0);
        height = attrs.getAttributeResourceValue(null, "height", 0);
        int textId = attrs.getAttributeResourceValue(null, "text", 0);


        text = context.getResources().getText(textId).toString();

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

        //调用measure()

    }

    /**
     * 响应触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 绘制画面
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        Rect rect = new Rect(100,100,100,100);

        canvas.drawRect(rect,paint);
        canvas.drawText("Test For drawing text!",100,100,paint);
    }
}
