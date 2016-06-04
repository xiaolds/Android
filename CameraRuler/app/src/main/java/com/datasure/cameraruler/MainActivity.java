package com.datasure.cameraruler;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.datasure.orientation.DistanceFresher;
import com.datasure.orientation.HeightFresher;
import com.datasure.orientation.OrientationWrapper;
import com.datasure.orientation.WidthFresher;
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
    private WidthFresher widthFresher;
    private ImageButton btnShowH;
    private TextView txHeight;
    private TextView txHeightTip;
    private ImageView imageArrow;
    private TextView txTip;

    //Orientation Sensor
    private OrientationWrapper ori;

    //Initial State
    private static State state = State.INITIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断是否为第一次使用软件，是的话初始化数据库
        isFirstUsing();

        //设置全屏，隐藏ActionBar
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();
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
        txHeightTip = (TextView) findViewById(R.id.id_tx_height_tip);           //显示的高度提示
        imageArrow = (ImageView) findViewById(R.id.id_image_arrows2);           //界面中心十字准星
        txTip = (TextView) findViewById(R.id.id_tx_tips);

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
                //检测倾斜度防止误操作
                float result = ori.getResult()[1];
                if(result > Math.PI/8 || result < -Math.PI/8){
                    Toast.makeText(getApplicationContext(),"请将手机拿正",Toast.LENGTH_SHORT).show();
                    return;
                }
                //get Distance & change state of Button
                changeState(v);
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

        heightFresher = new HeightFresher(txHeight, ori);
        heightFresher.initial();

        widthFresher = new WidthFresher(txHeight,ori);
        widthFresher.initial();

        changeToInit();
        //init config text
        initConfig();
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
                else if(Config.getModule_height() && btn.equals(btnShowH)){
                    state = State.START_CAL_H;
                    txHeightTip.setText("Height(m)");
                    changeToStartCal();
                }
                else if(!Config.getModule_height() && btn.equals(btnShowH)){
                    state = State.START_CAL_H;
                    txHeightTip.setText("Width(m)");
                    changeToCalWidth();
                }
                break;
            case START_CAL_H:
                if(Config.getModule_height() && btn.equals(capture)) {
                    state = State.GOT_HEI;
                    changeToGotHei();
                }
                else if (!Config.getModule_height() && btn.equals(capture)){
                    state = State.GOT_HEI;
                    changeToGotWidth();

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
        widthFresher.stopListen();
        widthFresher.destroy();
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

    private static final String DST_FOLDER_NAME = "CameraRuler";

    /**
     * the callback when tack picture
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            String path = initPath();

            //store the picture
            String fileName = "DICM" + System.currentTimeMillis() + ".jpg";
            String jpegName = path + "/" + fileName;

            //use bitmap cache
            final Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

            try{
                FileOutputStream fos = new FileOutputStream(jpegName);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();
                camera.stopPreview();
                camera.startPreview();
                Toast.makeText(MainActivity.this, "拍照成功，照片保存在"+jpegName+"文件之中！", Toast.LENGTH_SHORT).show();
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "拍照失败!未找到文件夹", Toast.LENGTH_LONG).show();
            }
            catch (IOException ioe){
                Toast.makeText(MainActivity.this, "拍照失败！"+ioe.toString(), Toast.LENGTH_LONG).show();
            }

        }
    };

    /**初始化保存路径
     * @return
     */
    private static   String storagePath = "";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static String initPath(){
        if(storagePath.equals("")){
            storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if(!f.exists()){
                f.mkdirs();
            }
        }
        return storagePath;
    }

    /**
     * 判断是否是第一次使用软件，是的话初始化数据库
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
            editor.putFloat("h",(float) Config.h);
            editor.putFloat("H",(float) Config.H);
            editor.putInt("mis",100);
            editor.commit();
        }

        return isFirstUsing;
    }

    private synchronized double getH() {
        SharedPreferences setting = this.getSharedPreferences("setting", 0);
        return setting.getFloat("H",(float) Config.H);
    }

    private synchronized double geth() {
        SharedPreferences setting = this.getSharedPreferences("setting", 0);
        return setting.getFloat("h",(float) Config.H);
    }


    /******************改变状态相关的函数************/
    //与Width相关的两个状态转换函数
    private void changeToCalWidth(){
        capture.setBackgroundResource(R.mipmap.measure_shutter1);
        txState.setText(R.string.tx_dis_start_calH);
        txHeight.setVisibility(View.VISIBLE);
        btnShowH.setVisibility(View.INVISIBLE);
        distanceFresher.stopListen();
        heightFresher.stopListen();
        widthFresher.setIsX1Get(true);
        txHeightTip.setVisibility(View.VISIBLE);
    }

    private void changeToGotWidth(){
        capture.setBackgroundResource(R.mipmap.measure_shutter0);
        txState.setText(R.string.tx_dis_gotH);
        txHeight.setVisibility(View.VISIBLE);
        btnShowH.setVisibility(View.INVISIBLE);
        distanceFresher.stopListen();
        widthFresher.stopListen();
        heightFresher.stopListen();
    }

    //与高度测量相关的几个函数
    private void changeToGotDis(){

        capture.setBackgroundResource(R.mipmap.measure_shutter0);
        txState.setText(R.string.tx_dis_gotD);
        txHeight.setVisibility(View.INVISIBLE);

        if(Config.getModule_height()){
            btnShowH.setBackgroundResource(R.mipmap.button_height);
        }
        else{
            btnShowH.setBackgroundResource(R.mipmap.button_width);
            widthFresher.startListen();

        }
        btnShowH.setVisibility(View.VISIBLE);
        distanceFresher.stopListen();
        heightFresher.stopListen();

        txHeightTip.setVisibility(View.INVISIBLE);

        Config.setDistance(distanceFresher.getData());
        //cancel tip
        txTip.setVisibility(View.INVISIBLE);
        imageArrow.setVisibility(View.INVISIBLE);
    }

    private void changeToStartCal() {
        capture.setBackgroundResource(R.mipmap.measure_shutter1);
        txState.setText(R.string.tx_dis_start_calH);
        txHeight.setVisibility(View.VISIBLE);
        btnShowH.setVisibility(View.INVISIBLE);
        distanceFresher.stopListen();
        heightFresher.startListen();
        txHeightTip.setVisibility(View.VISIBLE);

        //show tip
        txTip.setVisibility(View.VISIBLE);
        imageArrow.setVisibility(View.VISIBLE);
    }

    private void changeToGotHei() {
        capture.setBackgroundResource(R.mipmap.measure_shutter0);
        txState.setText(R.string.tx_dis_gotH);
        txHeight.setVisibility(View.VISIBLE);
        btnShowH.setVisibility(View.INVISIBLE);
        distanceFresher.stopListen();
        heightFresher.stopListen();
        txHeightTip.setVisibility(View.VISIBLE);

        Config.setTotalH(heightFresher.getData());
        //cancel tip
        txTip.setVisibility(View.INVISIBLE);
        imageArrow.setVisibility(View.INVISIBLE);
    }

    private void changeToInit() {
        capture.setBackgroundResource(R.mipmap.measure_shutter1);
        txState.setText(R.string.tx_dis_init);
        txHeight.setVisibility(View.INVISIBLE);
        btnShowH.setVisibility(View.INVISIBLE);
        distanceFresher.startListen();
        heightFresher.stopListen();
        widthFresher.stopListen();
        widthFresher.setIsX1Get(false);
        txHeightTip.setVisibility(View.INVISIBLE);
        Config.setDistance(-1);
        Config.setTotalH(-1);
        //cancel tip
        txTip.setVisibility(View.INVISIBLE);
        imageArrow.setVisibility(View.INVISIBLE);
    }





    /**
     * init config
     *
     */
    public void initConfig(){
        String H = String.format("%.2f",getH());
        String h = String.format("%.2f",geth());
        String total = String.format("%.2f",getH() + geth());

        String str = "h:" + h +
                "\nH:" + H +
                "\nH+h:" + total;
        txConfig.setText(str);
    }

    /********************菜单相关函数*************/

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //处理
        switch (item.getItemId()) {
            //处理拍摄按钮
            case R.id.id_menu_capture:
                //保存照片
                Toast.makeText(MainActivity.this,"拍摄中，请稍后...",Toast.LENGTH_SHORT).show();
                camera.takePicture(null, null, mPicture);
                return true;
            case R.id.id_menu_mis:
                MisFragment mis = new MisFragment();
                mis.show(getSupportFragmentManager(), "MisFragment");
                return true;
            //处理设置基线按钮
            case R.id.id_menu_setting:
                HeightFragment fragment1 = new HeightFragment();
                fragment1.show(getSupportFragmentManager(),"HeightFragment");
                return true;
            //修改测量方式
            case R.id.menu_switch_width:

                //修改文字
                if(Config.getModule_height()){
                    item.setTitle("测量宽度");
                    Config.setModule_height(false);
                }
                else{
                    item.setTitle("测量高度");
                    Config.setModule_height(true);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}


