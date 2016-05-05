package com.datasure.util;

import android.util.Log;

import java.util.Arrays;

/**
 * This Class is Created to calculate the height</br>
 * use single instance
 * Created by Lids on 2016/4/7.
 */
public class MathUtil {


    public static MathUtil util = null;

    public static MathUtil getInstance() {

        if (util == null) {
            synchronized (MathUtil.class){
                if(util == null) {
                    util = new MathUtil();
                }
            }
        }

        return util;
    }

    /**
     * Set the construct as private
     */
    private MathUtil() {
    }

    /**
     * retrieve the distance from person to Object
     *
     * @param zAangle angle which the phone rotate from z
     * @return H1
     */
    public double calDistance(double zAangle) {
        double phoneH = Config.h + Config.H - Config.inaccr;
        return phoneH * Math.tan(Math.abs(zAangle));
    }

    /**
     * Retrive the height below
     *
     * @param beta the angle which phone move
     * @return H1
     */
    public synchronized double calTotalH(double beta) throws Exception {
        if (Config.getDistance() < 0) throw new Exception("You must calculate the distance firstly!");
        double phoneH = Config.h + Config.H - Config.inaccr;
        Config.setTotalH(phoneH + Config.getDistance() * Math.tan(Math.abs(beta) - Math.PI/2));
        return Config.getTotalH();
    }


    /**
     * set the instance = null;
     */
    public void destory() {
        util = null;
    }


    /**
     * 根据精度信息返回Format信息
     * @param accuracy
     * @return
     */
    public String getFormatString(final double accuracy) {

        //将double转换为String类型，统计小数点后的位数
        String str = String.valueOf(accuracy);
        String strs = str.substring(2, str.length());
        int bit = strs.length();
        return "%." + bit + "f"; //%.2f
    }
}
