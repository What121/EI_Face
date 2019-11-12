package com.bestom.eiface.Control;

import com.bestom.eiface.view.CameraView;


//摄像头视图 操作管理类
public class CameraViewController {
    private static final String TAG = "CameraViewController";

    public CameraView frontCameraView,backCameraView;
    public static CameraViewController instance;

    public static CameraViewController getInstant(){
        if (instance == null) {
            synchronized (CameraViewController.class) {
                if (instance == null) {
                    instance = new CameraViewController();
                }
            }
        }
        return instance;
    }

    public void putFCameraView(CameraView cameraView){
        frontCameraView=cameraView;
    }

    public void putBCameraView(CameraView cameraView){
        backCameraView=cameraView;
    }

}
