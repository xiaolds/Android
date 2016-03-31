package com.datasure.cameraruler;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Camera camera;
    private CameraPreView preView;
    private Button capture;

//    private boolean safeToTakePicture = false;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            String fileName = "DICM" + System.currentTimeMillis() + ".jpg";
            File pictureFile = new File(Environment.getExternalStorageDirectory(),
                    fileName);
            Log.e("PictureFile", pictureFile.getAbsolutePath());

            if(pictureFile == null){
                Log.d("PictureCallback","PictureCallback is Error " );
                return;
            }

            //利用位图缓冲
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏，隐藏ActionBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);



    }


    @Override
    protected void onResume() {
        super.onResume();
        //检查是否含有摄像头
        if(!checkCameraHardware(this)){
            return;
        }
        //获取摄像头
        camera = getCameraInstance();
        //获取SurfaceView
        preView = new CameraPreView(this, camera);
        //获取Layout
        FrameLayout layout = (FrameLayout) findViewById(R.id.camera_preview);
        layout.addView(preView);
        //获取按钮，并为按钮添加单击事件
        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拍摄
                camera.takePicture(null, null, mPicture);
            }
        });

    }

    /**
     * 获取相机实例
     * @return
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
     * 判断本机是否含有摄像头
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
     * 重写触摸事件
     *
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return preView.getGesture().onTouchEvent(event);
    }
}


