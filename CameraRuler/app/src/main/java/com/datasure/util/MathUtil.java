package com.datasure.util;

/**
 * This Class is Created to calculate the height</br>
 * use single instance
 * Created by Lids on 2016/4/7.
 */
public class MathUtil {


    public static MathUtil util = null;

    public static MathUtil getInstance() {
        if (util == null) {
            util = new MathUtil();
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
        double phoneH = Config.h * Config.ratio + Config.H;
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
        double phoneH = Config.h*Config.ratio + Config.H;
        Config.setTotalH(phoneH + Config.getDistance() * Math.tan(Math.abs(beta) - Math.PI/2));
        return Config.getTotalH();
    }

    /**
     * Retrive the width below
     *
     * @param beta the angle which phone move
     * @return H1
     */
    public synchronized double calWidth(double beta) throws Exception {
        if (Config.getDistance() < 0) throw new Exception("You must calculate the distance firstly!");

        return Config.getDistance() * Math.abs(Math.tan(beta));
    }


    /**
     * set the instance = null;
     */
    public void destory() {
        util = null;
    }


    public String getFormatStringFormAccuracy(final double acc){

        String string = String.valueOf(acc);
        int bit = string.length() - 2;

        return "%." + bit + "f";
    }

}
