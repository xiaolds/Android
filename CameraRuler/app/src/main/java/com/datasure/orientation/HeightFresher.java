package com.datasure.orientation;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.datasure.util.Config;
import com.datasure.util.MathUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiaolds on 2016/4/10.
 */
public class HeightFresher extends Fresher {

    private MathUtil util = MathUtil.getInstance();

    private Timer timerForTalH;
    private double data;
    private double lastData;
    private double alpha;

    private TextView disText;
    private OrientationWrapper ori;
    private Handler handler;

    public HeightFresher(TextView disText, OrientationWrapper ori) {
        this.ori = ori;
        this.disText = disText;
    }

    @Override
    public void initial() {
        timerForTalH = new Timer();
        handler = new HeightHandler();
        timerForTalH.schedule(taskForToalH, 0,REFRESH_TIME);
    }

    @Override
    public void destroy() {
        timerForTalH.cancel();
    }

    public double getData(){
        return data;
    }


    private TimerTask taskForToalH = new TimerTask() {
        @Override
        public void run() {
            try{

                if(!isStart) return;
                alpha = ori.getResult()[2];
                data = util.calTotalH(alpha);
                if(Math.abs(lastData - data) > Fresher.ACCURACY){
                    Message message = new Message();
                    message.what = 0x2;
                    handler.sendMessage(message);
                    lastData = data;
                }

            }
            catch (Exception e){
                return;
            }
        }
    };

    /**
     * 启动一个新的线程专门负责刷新distance信息，
     * 刷新的原则是distance变化超过精度（0.1）就刷新
     */
    class HeightHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x2:   //fresh
                    showData(disText);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void showData(TextView txView) {
        double result = data*Config.getMis()/100;
        String string = null;
        if(result < 0){
            string = "NaN";
        }
        else{
            string = String.format(util.getFormatStringFormAccuracy(Config.getACCURACY()),result);
        }

        txView.setText(string);
    }
}
