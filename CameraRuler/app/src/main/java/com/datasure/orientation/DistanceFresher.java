package com.datasure.orientation;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.datasure.util.Config;
import com.datasure.util.MathUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 负责刷新位置信息
 * Created by xiaolds on 2016/4/9.
 */
public class DistanceFresher extends Fresher{

    private TextView disText;                       //需要刷新的控件
    private MathUtil util = MathUtil.getInstance();
    private double data;            //计算出的距离数据
    private double lastData;
    private double alpha;   //角度信息

    private OrientationWrapper ori;

    private Timer timerForDis;

    private Handler handler;
    private static final int REFRESH_TIME = 100;    //100ms
    private static final double ACCURACY = 0.01;


    public DistanceFresher(TextView disText, OrientationWrapper ori) {
        this.disText = disText;
        this.ori = ori;
        handler = new DistanceHandler();
    }

    private TimerTask taskForDis = new TimerTask(){
        @Override
        public void run() {
            //check the distance
            alpha = ori.getResult()[2];
            data = util.calDistance(alpha);
            if(isStart && Math.abs(lastData - data) > ACCURACY){
                Message message = new Message();
                message.what = 0x1;
                handler.sendMessage(message);
                lastData = data;
            }
        }

        @Override
        public boolean cancel() {
            return super.cancel();
        }
    };




    @Override
    public void initial() {
        timerForDis = new Timer();
        timerForDis.schedule(taskForDis,0,REFRESH_TIME);
    }

    public void destroy(){
        timerForDis.cancel();
    }


    public double getData(){
        return data;
    }


    /**
     * 启动一个新的线程专门负责刷新distance信息，
     * 刷新的原则是distance变化超过精度（0.1）就刷新
     */
    class DistanceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1:   //fresh
                    showData(disText);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void showData(TextView txView) {
        String string = null;
        if(alpha>(Math.PI/2 - 0.1) || alpha <-(Math.PI/2-0.1)){
            string = "MAX";
        }
        else{
            double result = data*Config.getMis()/100;
            string = String.format(util.getFormatStringFormAccuracy(Config.getACCURACY()),result);
        }

        txView.setText(string);
    }

}
