package com.datasure.util;

/**
 * Created by xiaolds on 2016/4/10.
 */
public class Config {

    public static final double ratio = (7-1.5)/7;
    public static double H = 0;
    public static double h = 1.72;             //person's height

    private static double distance = -1;                //the distance from person to object
    private static double totalH = -1;              //the Height of building
    private static double ACCURACY = 0.01;          //高度跟距离的精确度
    private static double frequencyOfBallFresh = 10;    //每秒10次,小球刷新频率
    private static int mis = 100;
    private static boolean module_height = true;

    public static synchronized void setModule_height(boolean b){
        Config.module_height = b;
    }

    public static synchronized boolean getModule_height(){
        return module_height;
    }

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

    public static synchronized void setMis(int mis){
        Config.mis = mis;
    }

    public static synchronized int getMis(){
        return Config.mis;
    }
}
