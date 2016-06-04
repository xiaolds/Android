package com.datasure.orientation;

import android.widget.TextView;

/**
 * Created by xiaolds on 2016/4/10.
 */
public abstract class Fresher {

    boolean isStart = false;        //标记是否开始刷新

    public static final int REFRESH_TIME = 100;    //100ms
    public static final double ACCURACY = 0.1;

    public abstract void initial();
    public abstract void destroy();

    /**
     * start listen
     */
    public void startListen(){
        isStart = true;
    }

    /**
     * stop listen
     */
    public void stopListen(){
        isStart = false;
    }

    protected abstract void showData(TextView txView);
}
