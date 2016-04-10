package com.datasure.orientation;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.datasure.util.MathUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 负责刷新位置信息
 * Created by xiaolds on 2016/4/9.
 */
public class DistanceFresher {

    private boolean isStart = false;    //标记是否开始刷新
    private TextView disText;           //需要刷新的控件
    private MathUtil util = MathUtil.getInstance();
//    private float[] result;
    private double lastDis;
    private OrientationWrapper ori;


    private Timer timer;
    private Handler handler;
    private static final int REFRESH_TIME = 100;    //100ms
    private static final double ACCURACY = 0.1;

    public DistanceFresher(TextView disText, OrientationWrapper ori) {
        this.disText = disText;
//        this.result = result;
        this.ori = ori;
        handler = new DistanceHandler();
        timer = new Timer();
    }

    private TimerTask task = new TimerTask(){
        @Override
        public void run() {
            //check the distance
            double distance = util.calDistance(ori.getResult()[2]);
            if(isStart &&Math.abs(lastDis - distance) > ACCURACY){
                Message message = new Message();
                message.what = 0x1;
                handler.sendMessage(message);
                lastDis = distance;
            }
        }

        @Override
        public boolean cancel() {
            return super.cancel();
        }
    };

    /**
     * start listen
     */
    public void startListen(){
        isStart = true;
        timer.schedule(task,0,REFRESH_TIME);
    }

    /**
     * stop listen
     */
    public void stopListen(){
        isStart = false;
        timer.cancel();
    }


    public boolean isListen(){
        return isStart;
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
                    disText.setText(String.format("%.1f",util.calDistance(ori.getResult()[2])));
                    break;
                default:
                    break;
            }
        }
    }
}
