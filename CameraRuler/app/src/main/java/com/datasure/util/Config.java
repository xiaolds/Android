package com.datasure.util;

/**
 * Created by xiaolds on 2016/4/10.
 */
public class Config {

    public static double inaccr = 0.25;
    public static double H = 0;
    public static double h = 1.75;             //person's height

    private static double distance = -1;                //the distance from person to object
    private static double totalH = -1;              //the Height of building
    private static double ACCURACY = 0.01;          //高度跟距离的精确度
    private static double frequencyOfBallFresh = 10;    //每秒10次,小球刷新频率

    public static synchronized void setDistance(double distance) {
        Config.distance = distance;
    }

    public static synchronized double getDistance() {
        return distance;
    }

    public static synchronized void setTotalH(double totalH) {
        Config.totalH = totalH;
    }

    public static synchronized double getTotalH() {
        return totalH;
    }

    public static synchronized double getACCURACY() {
        return ACCURACY;
    }

    public static synchronized void setACCURACY(double accuracy) {
        Config.ACCURACY = accuracy;
    }
}
