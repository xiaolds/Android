package com.datasure.cameraruler;

import android.content.Context;

import android.hardware.Camera;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Arrays;


/**
 * Created by xiaolds on 2016/3/15.
 */
public class CameraPreView extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener{

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private GestureDetector gesture;

    public CameraPreView(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        //实例化GestureDetector
        gesture = new GestureDetector(context,this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        //打开相机预览界面
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            Log.e("CameraView","camera created!");
        }
        catch (IOException e){
            Log.d("surfaceCreated", "Error Settion camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if(mHolder.getSurface() == null){
            return;
        }

        try{
            mCamera.stopPreview();
        }
        catch(Exception e){
            //ignore
        }

        //尝试在此处修改
        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        }
        catch (Exception e){
            Log.d("sufaceChanged","surfaceChange is Error");
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mCamera != null){
            Log.e("CameraView","it release the camera!");
            mCamera.release();
            mCamera = null;
        }

    }

    public GestureDetector getGesture() {
        return gesture;
    }

    /**
     * 单击事件，自动对焦
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //触摸的时候动态调整对焦区域
        //获取Parameters
        final Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(params);

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){
                    //获取焦距
                    Log.e("FocalLength",""+ params.getFocalLength());
                    float[] distance = new float[3];
                    params.getFocusDistances(distance);
                    Log.e("FocusDistance", Arrays.toString(distance));
                    Log.e("isZoomSupported:", "" + params.isZoomSupported());
                    Log.e("ZoomValue", "" + params.getZoom());
                }
            }
        });
        return true;
    }

    /****** getter and setter**********/


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }


    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }


}
