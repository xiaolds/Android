package com.datasure.com.datasure.util;

/**
 * This Class is Created to calculate the height</br>
 *  use single instance
 * Created by Lids on 2016/4/7.
 */
public class MathUtil {



    //store the height of somebody
    public double personh =  1.75;         //1.75m

    public double inAccr = 0.25;
    public double h = personh - inAccr;    //the phone's location
    
    public double distance;                //the distance from person to object


    public static MathUtil util = null;

    public static MathUtil getInstance(){
        if(util == null){
            util = new MathUtil();
        }
        return util;
    }

    /**
     * Set the construct as private
     */
    private MathUtil() {
        distance = -1;
    }

    /**
     * retrieve the distance from person to Object
     * @param zAangle angle which the phone rotate from z
     * @return H1
     */
    public double calDistance(double zAangle){
        return h * Math.tan(zAangle);
    }

    /**
     *  Retrive the height below
     * @param beta the angle which phone move
     * @return H1
     */
    public double getH1(double beta) throws Exception{
        if(distance < 0) throw new Exception("You must calculate the distance firstly!");
        return distance * Math.tan(beta);
    }


    /**
     * set the distance = -1;
     * set the instance = null;
     */
    public void destory(){
        distance = -1;
        util = null;
    }


    /************getter & setter*********/
    public double getPersonh() {
        return personh;
    }

    public void setPersonh(double personh) {
        this.personh = personh;
    }

    public double getInAccr() {
        return inAccr;
    }

    public void setInAccr(double inAccr) {
        this.inAccr = inAccr;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getDistance(){
        return distance;
    }

    public void setDistance(double distance){
        this.distance = distance;
    }
}
