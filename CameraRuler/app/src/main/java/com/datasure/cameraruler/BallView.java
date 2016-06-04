package com.datasure.cameraruler;


import android.accounts.Account;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.datasure.orientation.OrientationWrapper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiaolds on 2016/5/6.
 */
public class BallView extends SurfaceView implements SurfaceHolder.Callback {

    private Timer timer;
    private SurfaceHolder mHolder;
    private OrientationWrapper wrapper;

    public static final float Radius = 32f;

    public BallView(Context context, OrientationWrapper wrapper){
        super(context);
        this.setZOrderOnTop(true);

        mHolder = this.getHolder();
        mHolder.addCallback(this);
        //设置为透明,这里很重要，否则会直接变成黑框
        mHolder.setFormat(PixelFormat.TRANSPARENT);

        this.wrapper = wrapper;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启绘制线程
        drawOutter(holder);
        timer = new Timer();
        timer.schedule(new BallDrawerTask(holder),0,20);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //销毁绘制线程
        timer.cancel();
    }

    /**
     * 使用单独的线程刷新小球界面
     */
    class BallDrawerTask extends TimerTask {

        //holder
        private SurfaceHolder mHolder;
        private PointF oldPoint;
        private long count = 0;

        public BallDrawerTask(SurfaceHolder holder){
            this.mHolder = holder;
        }

        @Override
        public void run() {
            //绘制图形
            if(count++ < 3){
                drawOutter(mHolder);
            }
            float data = wrapper.getResult()[1];

            //get Data
            PointF p = getFormatPointF(data);
//                drawOutter(mHolder);
            drawBall(mHolder,oldPoint,p);

            oldPoint = p;

        }
    }

    //绘制小球
    private void drawBall(SurfaceHolder holder,final PointF old, final PointF fresh) {

        if(old == null || fresh == null || holder==null) return;
        //先清除旧小球

        //获取画板
//        float r = Radius + 3;
//        Rect clearRec = new Rect((int)(old.x-r),(int)(old.y-r),(int)(old.x+r),(int)(old.y+r));
        Rect clearRec = new Rect(0, 16, 288 ,80);
        Canvas canvas = holder.lockCanvas(clearRec);
        //获取橡皮擦
        Paint clearPaint = new Paint();
//        clearPaint.setAntiAlias(true);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        //执行清屏
        canvas.drawRect(clearRec, clearPaint);

        //绘制新小球
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
//        clearPaint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(fresh.x, fresh.y, Radius, paint);
        holder.unlockCanvasAndPost(canvas);
        
    }


    private void drawOutter(SurfaceHolder holder) {
        //尝试绘制
        Canvas canvas = holder.lockCanvas();

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
        holder.unlockCanvasAndPost(canvas);
    }

    private PointF getFormatPointF(float data) {
//        Log.e("BallView Data", "" + data);
        PointF p = new PointF();
        p.y = 48;
        p.x = 96 * data + 144;
        return p;
    }

}
