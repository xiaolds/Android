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
 * 负责刷新位置信息
 * Created by xiaolds on 2016/4/9.
 */
public class DistanceFresher extends Fresher{

    private TextView disText;                       //需要刷新的控件
    private MathUtil util = MathUtil.getInstance();
    private double data;
    private double lastData;

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
            data = util.calDistance(ori.getResult()[2]);
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
                    String string = String.format(util.getFormatStringFormAccuracy(Config.getACCURACY()),data*Config.getMis()/100);
//                    Log.e("FormatString:", string);
                    disText.setText(string);
                    break;
                default:
                    break;
            }
        }
    }

}
