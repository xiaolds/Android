package com.datasure.orientation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by Lids on 2016/4/7.
 * Use Orientation sensor to get data
 */
public class OrientationWrapper {

    /**
     * mark is the result can be retrived
     */
    private boolean isCal = false;

    /**
     * Sensor Manager
     */
    private SensorManager manager;
    /**
     * store the result which Orientation Sensor returned.
     */
    private float[] acceleValue;
    private float[] mageticValue;

    private float[] result;
    /**
     * the context who call the class
     */
    private Context context;


    private Sensor accelerometer;   //accelerometer sensor，加速度传感器
    private Sensor magnetic;        //magnetic sensor,地磁传感器


    /**
     * get the Oriented data calculated by the sensor
     * @return the Orientation data wrapped in a float Array
     */
    public synchronized float[]  getResult() {
        return result;
    }

    /**
     * construct
     */
    public OrientationWrapper(Context context) {
        this.context = context;
        init();
    }


    /**
     * initial the Orientation Service,
     * get the Sensor Manager
     */
    public void init() {
        //get the Sensor Manager
        if(manager == null){
            manager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        }
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetic = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //initial the result Arrays
        acceleValue = new float[3];
        mageticValue = new float[3];
        register();
        result = new float[3];
    }

    /**
     * register the Listener on sensor
     */
    private void register() {
        manager.registerListener(new MySensorListener(),accelerometer,Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(new MySensorListener(),magnetic,Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void destory(){
        if(manager != null){
            manager = null;
        }
        if(isCal){
            isCal = false;
        }
    }


    /**
     * caculate the orientation data
     */
    private void caculateOrientation(){
        synchronized (result){
            result = new float[3];
            float[] R = new float[9];

            manager.getRotationMatrix(R, null, acceleValue, mageticValue);
            manager.getOrientation(R, result);
        }
        Log.e("OriWrapper result:" , Arrays.toString(result));
        isCal = true;
    }

    public boolean isReady(){
        return isCal;
    }


    /**
     * the SensorEvent Listener,<br>
     * when the <em>accuracy</em> or <em>sensor</em> has changed,</br>
     * recall the class.
     */
    class MySensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            //which sensor
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                acceleValue = event.values;
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                mageticValue = event.values;
            }

            caculateOrientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //do nothing
        }
    }

}
