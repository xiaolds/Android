package com.datasure.cameraruler;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.datasure.orientation.DistanceFresher;
import com.datasure.orientation.HeightFresher;
import com.datasure.orientation.OrientationWrapper;
import com.datasure.util.Config;
import com.datasure.util.MathUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Camera camera;
    private CameraPreView preView;
    private ImageButton capture;
    private TextView state;
    private TextView config;
    private TextView distanceText;
    private DistanceFresher distanceFresher;
    private HeightFresher heightFresher;
    private ImageButton btnTotalH;
    private TextView heightText;

    //use the Orientation Sensor
    private OrientationWrapper ori;
//    private float[] result; //store the result of Orientation sensor
    private boolean isGetDistance = false;
    private boolean isGetHeight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏，隐藏ActionBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        //get the instance of Orientation sensor
        ori = new OrientationWrapper(this);
        capture = (ImageButton) findViewById(R.id.button_capture);
        state = (TextView) findViewById(R.id.id_btn_state);
        config = (TextView) findViewById(R.id.id_config_data);
        distanceText = (TextView) findViewById(R.id.id_distance);
        btnTotalH = (ImageButton) findViewById(R.id.id_btn_totalH);
        heightText = (TextView) findViewById(R.id.id_txt_totalH);
    }


    @Override
    protected void onResume() {
        super.onResume();

        //Check is there a camera
        if(!checkCameraHardware(this)){
            return;
        }
        //get the camera
        camera = getCameraInstance();
        //get SurfaceView
        preView = new CameraPreView(this, camera);
        //get Layout
        FrameLayout layout = (FrameLayout) findViewById(R.id.camera_preview);
        layout.addView(preView);
        //get the Button and set ClickListener
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get Distance & change state of Button
                changeBtnState();
//                camera.takePicture(null, null, mPicture);     //capture
            }
        });

        btnTotalH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeHbtnState();
            }
        });

        //init sensor
        ori.init();
        //when ori has initialed,start calculate the distance
        distanceFresher = new DistanceFresher(distanceText, ori);
        //start listen and fresh the textView
        distanceFresher.initial();
        distanceFresher.startListen();

        heightFresher = new HeightFresher(heightText, ori);
        heightFresher.initial();
        //init config text
        initConfig();
    }

    /**
     * init config
     *
     */
    private void initConfig(){
        double H = Config.H;
        double h = Config.h;
        String str = "h:" + h +
                        "\nH:" + H +
                        "\nH+h:" + (H + h);
        config.setText(str);
    }

    /**
     * change the Button capture's state
     */
    private void changeBtnState(){
        //change the state first
        isGetDistance = !isGetDistance;
        if(isGetDistance){
            capture.setBackgroundResource(R.mipmap.ic_action_reload);
            state.setText(R.string.capture_clicked);
            state.setTextColor(Color.RED);
            distanceFresher.stopListen();
            Config.setDistance(distanceFresher.getData());
            heightFresher.startListen();
            //display the Button
            btnTotalH.setVisibility(View.VISIBLE);
            heightText.setVisibility(View.VISIBLE);
        }
        else {
            capture.setBackgroundResource(R.mipmap.ic_action_camera_green);
            state.setText(R.string.capture_unclicked);
            state.setTextColor(Color.BLUE);
            distanceFresher.startListen();
            heightFresher.stopListen();
            btnTotalH.setVisibility(View.INVISIBLE);
            heightText.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * change the state of Button which can get Height
     */
    public void changeHbtnState(){
        isGetHeight = !isGetHeight;
        if(isGetHeight){
            heightFresher.stopListen();
            Config.setTotalH(heightFresher.getData());
            Log.e("HeightFresher","stop");
        }
        else {
            heightFresher.startListen();
            Log.e("HeightFresher","start");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        ori.destory();
        distanceFresher.stopListen();
        distanceFresher.destroy();
        heightFresher.stopListen();
        heightFresher.destroy();
    }

    private MathUtil util = MathUtil.getInstance();


    /**
     * get the instance of camera
     * @return the instance of camera
     */
    public static Camera getCameraInstance(){
        Camera camera = null;
        try{
            camera = Camera.open();
        }
        catch (Exception e){

        }
        return camera;
    }

    /**
     * check is there a camera
     * @param context
     * @return
     */
    private boolean checkCameraHardware(Context context){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            //has a device
            return true;
        }
        else{
            return  false;
        }
    }

    /**
     * touch event
     *
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return preView.getGesture().onTouchEvent(event);
    }

    /**
     * the callback when tack picture
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            String fileName = "DICM" + System.currentTimeMillis() + ".jpg";
            File pictureFile = new File(Environment.getExternalStorageDirectory(),
                    fileName);

            if(pictureFile == null){
                return;
            }

            //use bitmap cache
            final Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

            try{
                FileOutputStream fos = new FileOutputStream(pictureFile);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                camera.stopPreview();
                camera.startPreview();
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }

        }
    };
}


