package com.datasure.cameraruler;

import android.content.Context;

import android.graphics.Rect;
import android.graphics.RectF;
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
public class CameraPreView extends SurfaceView
        implements SurfaceHolder.Callback, GestureDetector.OnGestureListener{

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private GestureDetector gesture;
    private Camera.Size cameSize;       //摄像头分辨率


    private static final int NONE = 0x1;
    private static final int ZOOM = 0x2;    //缩放模式
//    private static final int ZOOM_SMALL = 0x3;    //缩放模式

    private int mode = NONE;    //初始模式
    private float oldDistance;  //原始距离
    private static int zoomLevel = 0;


    public CameraPreView(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        //获取界面的分辨率
        cameSize = getResolution();
        Log.e("Preview Size","Width:" + cameSize.width +"\nHeight:" +cameSize.height);


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
            Camera.Parameters parameters = mCamera.getParameters();
            //设置分辨率
            parameters.setPreviewSize(cameSize.width,cameSize.height);
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

        try{
            mCamera.setPreviewDisplay(mHolder);
            Camera.Parameters parameters = mCamera.getParameters();
            //设置分辨率
            parameters.setPreviewSize(cameSize.width,cameSize.height);
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
     * 单击事件，自动对焦,
     * 6/1 添加触摸调整zoom与对焦区域的自动调整功能
     * @param event
     * @return 处理成功返回true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int type = event.getAction() & MotionEvent.ACTION_MASK;
        //判断事件类型
        switch (type) {
            case MotionEvent.ACTION_DOWN:
                mode = NONE;
                this.onSingleTapUp(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //第二根手指按下
                mode = ZOOM;
                oldDistance = getDistance(event.getX(0),event.getY(0), event.getX(1), event.getY(1));
                break;
            case MotionEvent.ACTION_MOVE:
                //双指缩放，通过判断距离来实现
                if(mode == ZOOM) {
                    //判断新的位置的距离
                    float distance = getDistance(event.getX(0),event.getY(0), event.getX(1), event.getY(1));

                    if(distance - oldDistance > 15f){
                        //扩大
                        enlargeZoom();
                        mCamera.startSmoothZoom(zoomLevel);
                        oldDistance = distance;
                    }
                    else if(distance - oldDistance < -15f){
                        //缩小
                        reduceZoom();
                        mCamera.startSmoothZoom(zoomLevel);
                        oldDistance = distance;
                    }
                    else{
//                        oldDistance = oldDistance;
                    }

                }
                else{
                    //none
                }
                break;
            default:break;
        }


        return true;
    }

    //计算欧式几何距离
    private float getDistance(float x1, float y1, float x2, float y2){

        double result = Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));

        return (float)result;
    }

    //扩大Zoom级别
    private void enlargeZoom(){

        zoomLevel++;
        int maxZoomLevel = mCamera.getParameters().getMaxZoom();
        if(zoomLevel >= maxZoomLevel) {
            zoomLevel = maxZoomLevel;
        }
    }

    //缩小Zoom级别
    private void reduceZoom(){
        zoomLevel--;
        if(zoomLevel <= 0) {
            zoomLevel = 0;
        }

    }


    //计算对焦与测光区域
    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 100;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) ((x / this.getWidth()) * 2000 - 1000);
        int centerY = (int) ((y / this.getHeight()) * 2000 - 1000);
        int left = clamp(centerX - (areaSize / 2), -1000, 1000);
        int top = clamp(centerY - (areaSize / 2), -1000, 1000);
        int right = clamp(centerX + (areaSize / 2), -1000, 1000);
        int bottom = clamp(centerY + (areaSize / 2), -1000, 1000);

        RectF rectF = new RectF(left, top, right, bottom);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top),
                Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }


    //获取摄像头的Size，推荐使用1280*720; 16:9
    public Camera.Size getResolution() {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizeList = params.getSupportedPictureSizes();
        Camera.Size s = sizeList.get(3);

        return s;
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
    public boolean onSingleTapUp(MotionEvent event) {

        //触摸的时候动态调整对焦区域
        //获取对焦范围的方框
        Rect focusRect = calculateTapArea(event.getRawX(), event.getRawY(), 1f);
        //获取测光的方框
        Rect meteringRect = calculateTapArea(event.getRawX(), event.getRawY(), 1f);

        //获取Parameters
        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
            focusAreas.add(new Camera.Area(focusRect, 600));
            params.setFocusAreas(focusAreas);
        }
        if (params.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
            meteringAreas.add(new Camera.Area(meteringRect, 600));
            params.setMeteringAreas(meteringAreas);
        }

        mCamera.cancelAutoFocus();

        mCamera.setParameters(params);

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Log.d("CameraPreview", "autoFocus Success~");
            }
        });

        return true;
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
