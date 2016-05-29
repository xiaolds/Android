package com.datasure.orientation;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.datasure.util.Config;
import com.datasure.util.MathUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiaolds on 2016/5/29.
 */
public class WidthFresher extends Fresher {

    //获取角度的时候的距离
    private float angle;
    //左侧长度
    private double x1;
    //右侧距离
    private double x2;
    //需要刷新的控件
    private TextView disText;
    //传感器
    private OrientationWrapper wrapper;
    //单独线程刷新
    private Timer timer;

    //工具类
    private MathUtil util = MathUtil.getInstance();
    //数据
    private double data;
    private double lastData;

    private boolean isX1Get = false;

    private Handler handler;
    private static final int REFRESH_TIME = 100;    //100ms
    private static final double ACCURACY = 0.01;

    public WidthFresher(TextView disText, OrientationWrapper ori){
        this.disText = disText;
        this.wrapper = ori;
    }


    //覆写
    @Override
    public void startListen(){
        super.startListen();
        //要求记录alpha
        angle = wrapper.getResult()[0];
    }

    @Override
    public void initial() {
        timer = new Timer();
        timer.schedule(taskForWidth,0,REFRESH_TIME);
        handler = new WidthHandler();
    }

    @Override
    public void destroy() {
        timer.cancel();
    }

    private TimerTask taskForWidth = new TimerTask(){
        @Override
        public void run() {

            try {
                data = util.calWidth(wrapper.getResult()[0]-angle);
//                Log.e("WidthFresher",""+data);
                //判断是否开始刷新
                if(isStart && Math.abs(lastData - data) > ACCURACY){
                    //判断处于哪个阶段
                    Message message = new Message();
                    if(!isX1Get) {
                        //如果还没有获取x1,刷新左侧的宽度
                        x1 = data;
                        message.what = 0x3;
                        handler.sendMessage(message);
                        Log.e("WidthFresher","0x3");
                    }
                    else{
                        //刷新右边的x2
                        message.what = 0x4;
                        handler.sendMessage(message);
                        Log.e("WidthFresher","0x4");
                    }


                    lastData = data;
                }
            }
            catch (Exception e){
                //不做处理
            }

        }

        @Override
        public boolean cancel() {
            return super.cancel();
        }
    };


    private void flushLeft(){

    }



    /**
     * 启动一个新的线程专门负责刷新distance信息，
     * 刷新的原则是distance变化超过精度（0.1）就刷新
     */
    class WidthHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x3:   //fresh
                    String string = String.format(util.getFormatStringFormAccuracy(Config.getACCURACY()),data*Config.getMis()/100);
                    disText.setText(string);
                    break;

                case 0x4:
                    String string2 = String.format(util.getFormatStringFormAccuracy(Config.getACCURACY()),(data+x1)*Config.getMis()/100);
                    disText.setText(string2);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * getter & setter
     */
    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public synchronized void setIsX1Get(boolean b){
        this.isX1Get = b;
    }

    public synchronized boolean getIsX1Get() {
        return isX1Get;
    }


}
