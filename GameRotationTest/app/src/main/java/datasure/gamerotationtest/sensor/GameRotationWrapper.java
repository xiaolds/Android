package datasure.gamerotationtest.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by Lids on 2016/5/5.
 */
public class GameRotationWrapper {


    private SensorManager manager;
    private Sensor sensor;
    private Context context;



    public GameRotationWrapper(Context context) {

        this.context = context;
        init();
    }


    private void init() {
        if(null == manager) {
            manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensor = manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
            manager.registerListener(new SensorEventListener() {

                long currentTime = 0;
                long oldTime = 0;

                @Override
                public void onSensorChanged(SensorEvent event) {
                    if(event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
                        if(((currentTime = System.currentTimeMillis()) - oldTime) > 1000){
                            Log.e("GameSensorValue", Arrays.toString(event.values));
                            oldTime = currentTime;
                        }

                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    Log.e("GameSensor accuracy", "" + accuracy);
                }
            },sensor,Sensor.TYPE_GAME_ROTATION_VECTOR);
        }
    }

    public void destory(){
        if(null != manager) {
//            manager.unregisterListener();
            manager = null;
        }
    }
}
