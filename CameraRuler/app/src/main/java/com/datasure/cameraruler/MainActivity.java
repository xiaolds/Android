package com.datasure.cameraruler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.datasure.setting.HeightFragment;
import com.datasure.setting.MisFragment;
import com.datasure.util.Config;
import com.datasure.util.MathUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    //相机相关
    private Camera camera;
    private CameraPreView preView;

    //控件
    private ImageButton capture;
    private TextView txState;
    private TextView txConfig;
    private TextView txDistance;
    private DistanceFresher distanceFresher;
    private HeightFresher heightFresher;
    private ImageButton btnShowH;
    private TextView txHeight;
    private TextView txHeightTip;

    //Orientation Sensor
    private OrientationWrapper ori;

    //State
    private static State state = State.INITIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFirstUsing();

        //设置全屏，隐藏ActionBar
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();
//        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0f0000ff")));
        //强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        //get the instance of Orientation sensor
        ori = new OrientationWrapper(this);
        capture = (ImageButton) findViewById(R.id.id_btn_capture);              //拍摄按钮
        txState = (TextView) findViewById(R.id.id_tx_capture_state);            //拍摄按钮底下的提示按钮
        txConfig = (TextView) findViewById(R.id.id_tx_config);                  //左侧显示H h的TextView
        txHeight = (TextView) findViewById(R.id.id_txt_height);                 //显示高度的TextView
        txDistance = (TextView) findViewById(R.id.id_tx_distance);              //显示距离
        btnShowH = (ImageButton) findViewById(R.id.id_btn_totalH);              //点击显示高度的按钮
        txHeightTip = (TextView) findViewById(R.id.id_tx_height_tip);           //

    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //菜单点击

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //处理
        switch (item.getItemId()) {
            //处理拍摄按钮
            case R.id.id_menu_capture:
                //TODO
                return true;
            case R.id.id_menu_mis:
                MisFragment mis = new MisFragment();
                mis.show(getSupportFragmentManager(), "MisFragment");
                return true;
            //处理设置基线按钮
            case R.id.id_menu_setting:
                /*Intent intent = new Intent(SettingsActivity.ACTION_SETTING);
                startActivity(intent);
                */
                HeightFragment fragment1 = new HeightFragment();
                fragment1.show(getSupportFragmentManager(),"HeightFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        //添加小球
        BallView ballView = new BallView(this, ori);
        FrameLayout layout1 = (FrameLayout) findViewById(R.id.ball);
        layout1.addView(ballView);

        //get the Button and set ClickListener
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get Distance & change state of Button
                changeState(v);
//                camera.takePicture(null, null, mPicture);     //capture TODO
            }
        });

        btnShowH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeState(v);
            }
        });

        //init sensor
        ori.init();
        //when ori has initialed,start calculate the distance
        distanceFresher = new DistanceFresher(txDistance, ori);
        //start listen and fresh the textView
        distanceFresher.initial();
//        distanceFresher.startListen();

        heightFresher = new HeightFresher(txHeight, ori);
        heightFresher.initial();

        changeToInit();
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
        txConfig.setText(str);
    }

    /**
     * change the Button capture's state
     */
    private void changeState(final View view){
        ImageButton btn = (ImageButton) view;
        switch (state){
            case INITIAL:
                if (btn.equals(capture)){
                    state = State.GOT_DIS;
                    changeToGotDis();
                }
                break;
            case GOT_DIS:
                if(btn.equals(capture)){
                    state = State.INITIAL;
                    changeToInit();
                }
                else if(btn.equals(btnShowH)){
                    state = State.START_CAL_H;
                    changeToStartCal();
                }
                break;
            case START_CAL_H:
                if(btn.equals(capture)) {
                    state = State.GOT_HEI;
                    changeToGotHei();
                }
                break;
            case GOT_HEI:
                if(btn.equals(capture)){
                    state = State.INITIAL;
                    changeToInit();
                }
                break;
            default: break;
        }

    }

    private void changeToGotDis(){
        capture.setBackgroundResource(R.mipmap.measure_shutter0);
        txState.setText(R.string.tx_dis_gotD);
        txHeight.setVisibility(View.INVISIBLE);
        btnShowH.setVisibility(View.VISIBLE);
        distanceFresher.stopListen();
        heightFresher.stopListen();
        txHeightTip.setVisibility(View.INVISIBLE);
        //TODO
        Config.setDistance(distanceFresher.getData());
    }

    private void changeToStartCal() {
        capture.setBackgroundResource(R.mipmap.measure_shutter1);
        txState.setText(R.string.tx_dis_start_calH);
        txHeight.setVisibility(View.VISIBLE);
        btnShowH.setVisibility(View.INVISIBLE);
        distanceFresher.stopListen();
        heightFresher.startListen();
        txHeightTip.setVisibility(View.VISIBLE);
    }

    private void changeToGotHei() {
        capture.setBackgroundResource(R.mipmap.measure_shutter0);
        txState.setText(R.string.tx_dis_gotH);
        txHeight.setVisibility(View.VISIBLE);
        btnShowH.setVisibility(View.INVISIBLE);
        distanceFresher.stopListen();
        heightFresher.stopListen();
        txHeightTip.setVisibility(View.VISIBLE);
        //TODO
        Config.setTotalH(heightFresher.getData());
    }

    private void changeToInit() {
        capture.setBackgroundResource(R.mipmap.measure_shutter1);
        txState.setText(R.string.tx_dis_init);
        txHeight.setVisibility(View.INVISIBLE);
        btnShowH.setVisibility(View.INVISIBLE);
        distanceFresher.startListen();
        heightFresher.stopListen();
        txHeightTip.setVisibility(View.INVISIBLE);
        //TODO 暂时在这里进行设置Distance
        Config.setDistance(-1);
        Config.setTotalH(-1);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(camera != null){
            Log.e("MainActivity","it release the camera!");
            camera.stopPreview();
            camera.release();
            camera = null;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        distanceFresher.stopListen();
        distanceFresher.destroy();
        heightFresher.stopListen();
        heightFresher.destroy();
        ori.destory();
        Log.e("MainActivity","onStop runned");
    }

    private MathUtil util = MathUtil.getInstance();


    /**
     * get the instance of camera
     * @return the instance of camera
     */
    private Camera getCameraInstance(){
        if (camera != null) return camera;
        try{
            camera = Camera.open();
            Log.e("MainActivity","Try to get the camera instance");
        }
        catch (Exception e){
            Log.e("MainActivity","Get Camera failed!");
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

    /**
     * 判断是否是第一次使用软件，是的话初始化几个值
     * @return
     */
    private boolean isFirstUsing() {

        boolean isFirstUsing;
        SharedPreferences setting = this.getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = setting.edit();
        //try to get the isFirstUsing
        isFirstUsing = setting.getBoolean("isFirstUsing",true);

        if(isFirstUsing) {
            editor.putBoolean("isFirstUsing", false);
            editor.putFloat("h",1.7f);
            editor.putFloat("H",0f);
            editor.putInt("mis",100);
            editor.commit();
        }

        return isFirstUsing;
    }
}


