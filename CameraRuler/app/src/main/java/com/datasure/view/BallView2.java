package com.datasure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.widget.ImageView;

import com.datasure.orientation.OrientationWrapper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiaolds on 2016/5/10.
 */
public class BallView2 extends ImageView{


    private OrientationWrapper wrapper;
    private Timer timer;
    private PointF oldPoint;


    public static final float Radius = 32f;

    public BallView2(Context context, OrientationWrapper wrapper) {
        super(context);
        this.wrapper = wrapper;
        //开启绘制线程
        timer = new Timer();
        timer.schedule(new BallDrawerTask(),0,30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制图形
//            drawOutter(mHolder);
        float data = wrapper.getResult()[1];

        //get Data
        PointF p = getFormatPointF(data);

        drawBall(canvas,oldPoint,p);

        oldPoint = p;
    }

    private void drawOutter(Canvas canvas) {

        //设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10.0f);
        //画方框,96*32
//        Rect rect = new Rect(48,48,16,16);
        Rect rect = new Rect(0,0,96 * 3,32 * 3);
        canvas.drawRect(rect, paint);
        //画三等分线
        canvas.drawLine(32 * 3, 0, 32 *3, 32 *3,paint);
        canvas.drawLine(32 * 6, 0, 32 *3 * 2, 32 *3,paint);
        //刷新提交
    }

    //绘制小球
    private void drawBall(Canvas canvas, final PointF old, final PointF fresh) {

        if(old == null || fresh == null || canvas==null) return;
        //先清除旧小球

        //获取画板
        Rect clearRec = new Rect((int)(old.x-Radius),(int)(old.y-Radius),(int)(old.x+Radius),(int)(old.y+Radius));
        //获取橡皮擦
        Paint clearPaint = new Paint();
        clearPaint.setAntiAlias(true);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        执行清屏
        canvas.drawRect(clearRec, clearPaint);

        //绘制新小球
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(fresh.x, fresh.y, Radius, paint);

    }

    class BallDrawerTask extends TimerTask {



        public BallDrawerTask(){
        }

        @Override
        public void run() {
           invalidate();
        }
    }

    private PointF getFormatPointF(float data) {
//        Log.e("BallView Data", "" + data);
        PointF p = new PointF();
        p.y = 48;
        p.x = 96 * data + 144;
        return p;
    }
}
