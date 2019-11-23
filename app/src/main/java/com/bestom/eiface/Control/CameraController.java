package com.bestom.eiface.Control;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;

import com.bestom.eiface.MyApp;

import java.util.List;

//摄像头 操作管理类
public class CameraController {
    private static final String TAG = "CameraController";

    private CameraInfor frontCameraInfor,backCameraInfor;

    public static  final int CAMERA_WIDTH = MyApp.CAMERA_WIDTH;
    public static  final int CAMERA_HEIGHT =MyApp.CAMERA_HEIGHT;

    public static CameraController instance;

    public static CameraController getInstant(){
        if (instance == null) {
            synchronized (CameraController.class) {
                if (instance == null) {
                    instance = new CameraController();
                }
            }
        }
        return instance;
    }


    private CameraController() {
    }

    //初始化摄像头
    public void openCamera(boolean isFront) {
        int count = Camera.getNumberOfCameras();
        try {
            if (count > 1) {
                if (isFront) {
                    if (frontCameraInfor!=null&&frontCameraInfor.Camera!=null){
                        frontCameraInfor.Camera.release();
                    }
                    frontCameraInfor=new CameraInfor();
                    frontCameraInfor.Camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    frontCameraInfor.cameraId = 0;
                } else {
                    if (backCameraInfor!=null&&backCameraInfor.Camera!=null){
                        backCameraInfor.Camera.release();
                    }
                    backCameraInfor=new CameraInfor();
                    backCameraInfor.Camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    backCameraInfor.cameraId = 1;
                }
            } else {
//                camera = open();
                Log.e(TAG, "openCamera: camera count is "+count );

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Camera open() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            return Camera.open(i);
        }
        return null;
    }

    //开始预览
    public void startPreview(SurfaceTexture surface, PreviewCallback callback,byte[] callbackBuffer,int Cameraid) {
        CameraInfor cameraInfor = null;
        if (Cameraid==0){
            cameraInfor=frontCameraInfor;
        }else if (Cameraid==1){
            cameraInfor=backCameraInfor;
        }
        try {
            if (cameraInfor != null) {
                initParam(cameraInfor);

                cameraInfor.Camera.setPreviewTexture(surface);
                cameraInfor.Camera.addCallbackBuffer(callbackBuffer);
                cameraInfor.Camera.setPreviewCallbackWithBuffer(callback);

                cameraInfor.Camera.startPreview();
            } else {
                Log.d(getClass().getSimpleName(), "No camera");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        if (frontCameraInfor!=null && frontCameraInfor.Camera!=null){
            closeCamera(frontCameraInfor.Camera);
        }
        frontCameraInfor=null;
        if (backCameraInfor!=null && backCameraInfor.Camera!=null){
            closeCamera(backCameraInfor.Camera);
        }
        backCameraInfor=null;
        instance=null;
    }

    public void closeCamera(Camera camera) {
        try {
            if (camera != null) {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化 摄像头 分辨率参数
    private void initParam(CameraInfor cameraInfor) {
        if (cameraInfor != null) {
            //        List<Camera.Size> pictureSize = camera.getParameters().getSupportedPictureSizes();
            List<Camera.Size> previewSize = cameraInfor.Camera.getParameters().getSupportedPreviewSizes();
            Camera.Parameters params = cameraInfor.Camera.getParameters();
            params.setPreviewSize(CAMERA_WIDTH, CAMERA_HEIGHT);
            params.setPictureSize(CAMERA_WIDTH, CAMERA_HEIGHT);
            if (cameraInfor.cameraId==1){
                params.setPreviewFormat(ImageFormat.NV21);
                Log.d(TAG, "initParam: "+cameraInfor.cameraId+"setPreviewFormat is ImageFormat.NV21");
            }

            cameraInfor.Camera.setParameters(params);
        }
    }

    private class CameraInfor{
        private Camera Camera;
        private int cameraId;
    }
}
