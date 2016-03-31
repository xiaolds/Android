package com.datasure.cameraruler;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //实例化GestureDetector
        gesture = new GestureDetector(context,this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
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
            mCamera.release();
            mCamera = null;
        }

    }

    /****** getter and setter**********/
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

//        Log.d("FocusArea",String.valueOf(params.getMaxNumFocusAreas()));
//        if(params.getMaxNumMeteringAreas() > 0){
//
//            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
//
//            Rect areaRect1 = new Rect(-100, -100, 100, 100);    // specify an area in center of image
//            meteringAreas.add(new Camera.Area(areaRect1, 600)); // set weight to 60%
//            Rect areaRect2 = new Rect(800, -1000, 1000, -800);  // specify an area in upper right of image
//            meteringAreas.add(new Camera.Area(areaRect2, 400)); // set weight to 40%
//            params.setMeteringAreas(meteringAreas);
//        }
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
