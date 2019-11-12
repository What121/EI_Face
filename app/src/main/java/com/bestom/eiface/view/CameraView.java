package com.bestom.eiface.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.TextureView;

import com.bestom.eiface.Control.CameraController;
import com.bestom.eiface.Control.CameraDataQueueController;
import com.bestom.eiface.Handler.RegisterHandler;
import com.bestom.eiface.R;
import com.bestom.eiface.activity.CameraActivity;
import com.wf.wffrdualcamapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static com.bestom.eiface.MyApp.MirrorX;

/**
 * frontcamera is IR
 * backcamera is color
 */

public class CameraView extends TextureView implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {
    private static final String TAG = "CameraView";

    private RegisterHandler mRegisterHandler;
    private CameraActivity cameraActivity;
    //初始化camera关键参数
    public byte[] cameraPreviewBuffer;
    private boolean cameraMode = true;
    private boolean cameraMirrorX = false;
    private boolean cameraVertical = true;
    private String name = "";
    private boolean enroll = false;

    private boolean isStartExecutionRunning = false;
    private boolean isStartEnrollRunning = false;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

        if (cameraMirrorX) {
            setScaleX(-1);
        }

    }

    private void init(Context context, AttributeSet attrs) {
        setSurfaceTextureListener(this);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CameraSurfaceView);
            cameraMode = a.getBoolean(R.styleable.CameraSurfaceView_msv_cameraFront,cameraMode);
            cameraMirrorX = a.getBoolean(R.styleable.CameraSurfaceView_msv_cameraMirrorX, cameraMirrorX);
            cameraVertical = a.getBoolean(R.styleable.CameraSurfaceView_msv_cameraVertical, cameraVertical);
            a.recycle();
        }

        if (!cameraMode){
            MirrorX=cameraMirrorX;
        }

    }

    public void IRCamerainit(){
        cameraPreviewBuffer = new byte[(int) (CameraController.CAMERA_HEIGHT * CameraController.CAMERA_WIDTH * 1.5)];//1.5 for yuv image
        if (cameraMode){
            CameraController.getInstant().openCamera(true);
        }else{
            CameraController.getInstant().openCamera(false);
        }
        CameraController.getInstant().startPreview(new SurfaceTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES), this, cameraPreviewBuffer,0);
        Log.i(TAG, "IRCamerainit:  startPreview front" );
    }

    public void setDrawActivity(CameraActivity cameraActivity) {
        this.cameraActivity = cameraActivity;
    }

    public void setRegisterHandler(RegisterHandler registerHandler) {
        mRegisterHandler = registerHandler;
    }

    public void setEnrolled(String name, boolean enroll) {
        this.name = name;
        this.enroll = enroll;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (cameraMode){
            CameraController.getInstant().openCamera(true);
        }else{
            CameraController.getInstant().openCamera(false);
        }
        cameraPreviewBuffer = new byte[(int) (CameraController.CAMERA_HEIGHT * CameraController.CAMERA_WIDTH * 1.5)];//1.5 for yuv image
        CameraController.getInstant().startPreview(surface, this, cameraPreviewBuffer,1);
        Log.d(TAG, "onSurfaceTextureAvailable: startPreview "+(cameraMode?"front":"back"));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged: ");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        CameraController.getInstant().closeCamera();
        Log.d(TAG, "onSurfaceTextureDestroyed: ");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        Log.d(TAG, "onSurfaceTextureUpdated: ");
    }

    //region Saveimages
    public void Saveimages(final String data, String foldername, String filestartname){
        final File dir= new File(Environment.getExternalStorageDirectory()+"/"+foldername);

        if (!dir.exists()) {
            boolean succ = dir.mkdir();
            Log.i("DDDD", "run: is_created "+succ);
        }
        Random random= new Random(1000);
        final String filename= filestartname+random+".jpg";

        new Runnable() {
            @Override
            public void run() {
                File file= new File(dir,filename);
                try {
                  boolean is_created=  file.createNewFile();
                    Log.i("DDDD", "run: is_created "+is_created);


                    FileOutputStream out= new FileOutputStream(file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] imageBytes = baos.toByteArray();
                    imageBytes = Base64.decode(data, Base64.DEFAULT);
                    out.write(imageBytes);

                    Log.i("DDDD", "run: file written");
                    out.close();

                } catch (IOException e) {
                    Log.i("DDDD", "run: inside catch ");
                    e.printStackTrace();
                }

            }
        }.run();
    }
    //endregion

    /*************************************************************/
    //抓拍回调
    @Override
    public void onPreviewFrame(byte[] data, final Camera camera) {
//        data = wffrjni.rotateImage(data, 640, 480, 1,270);

        if (cameraMode) {
//            Log.i(TAG, "onPreviewFrame: from front IR camera");
//            CameraDataQueueController.getInstance().putF(data, SystemClock.uptimeMillis());
            CameraDataQueueController.getInstance().putB(data, SystemClock.uptimeMillis());
        } else {
//            Log.i(TAG, "onPreviewFrame: from back color camera");
//            CameraDataQueueController.getInstance().putB(data, SystemClock.uptimeMillis());
            CameraDataQueueController.getInstance().putF(data, SystemClock.uptimeMillis());
        }

        long startTime = System.currentTimeMillis();

//        Log.i(TAG, "onPreviewFrame: after cameraDetails");
        final byte[][] imagesBufferArray = CameraDataQueueController.getInstance().getDualCameraPreview();
        long endTime = System.currentTimeMillis();
//        Log.i(TAG, "TIME TAKEN EXECUTION : " + (endTime - startTime) + "ms");

        /* save image in folder */
        if(imagesBufferArray!=null&&cameraActivity!=null) {
            Log.i(TAG, "STATE : " + wffrdualcamapp.getState());

            if (!enroll){
                if (!isStartExecutionRunning) {
                    StartExecutionThread startExecutionThread = new StartExecutionThread();
                    FrameInfo frameInfo = new FrameInfo();
                    frameInfo.clrFrame = Arrays.copyOf(imagesBufferArray[0], imagesBufferArray[0].length);
                    frameInfo.irFrame = Arrays.copyOf(imagesBufferArray[1], imagesBufferArray[1].length);
                    frameInfo.name = "";

                    startExecutionThread.execute(frameInfo);
                }
            }else {
                if (!isStartEnrollRunning) {
                    StartEnrollThread startEnrollThread = new StartEnrollThread();
                    FrameInfo frameInfo = new FrameInfo();
                    frameInfo.clrFrame = Arrays.copyOf(imagesBufferArray[0], imagesBufferArray[0].length);
                    frameInfo.irFrame = Arrays.copyOf(imagesBufferArray[1], imagesBufferArray[1].length);
                    frameInfo.name = name;

                    startEnrollThread.execute(frameInfo);
                }
            }
        }
        camera.addCallbackBuffer(cameraPreviewBuffer);
}

    public class FrameInfo {
        public byte[] irFrame, clrFrame;
        public String name;

        public FrameInfo() {

        }
    }

    public class StartExecutionThread extends AsyncTask<FrameInfo, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isStartExecutionRunning = true;
            Log.d(TAG, "Execution onPreExecute: finished" );
        }

        @Override
        protected Void doInBackground(FrameInfo... frameInfos) {
//            int flag = EIFace.startExecution(frameInfos[0].clrFrame, frameInfos[0].irFrame, CameraController.CAMERA_WIDTH, CameraController.CAMERA_HEIGHT, frameInfos[0].name);
            int flag = wffrdualcamapp.startExecution(frameInfos[0].clrFrame, frameInfos[0].irFrame, CameraController.CAMERA_WIDTH, CameraController.CAMERA_HEIGHT, frameInfos[0].name);
            cameraActivity.drawOutput(wffrdualcamapp.getFaceCoordinates(), CameraController.CAMERA_WIDTH, CameraController.CAMERA_HEIGHT, enroll);

            Log.d(TAG, "Execution doInBackground: finished" );
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isStartExecutionRunning = false;
            Log.d(TAG, "Execution onPostExecute: finished" );
        }

    }

    public class StartEnrollThread extends AsyncTask<FrameInfo, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isStartEnrollRunning = true;
            Log.d(TAG, "Enroll onPreExecute: finished" );
        }

        @Override
        protected Void doInBackground(FrameInfo... frameInfos) {
            int flag = wffrdualcamapp.startExecution(frameInfos[0].clrFrame, frameInfos[0].irFrame, CameraController.CAMERA_WIDTH, CameraController.CAMERA_HEIGHT, frameInfos[0].name);

            Log.d(TAG, "Enroll doInBackground: flag" +flag);

            Message message=new Message();
            message.what=flag;
            message.obj=frameInfos[0].clrFrame;
            //Main ui
            cameraActivity.drawOutput(null, CameraController.CAMERA_WIDTH, CameraController.CAMERA_HEIGHT, enroll);
            if (mRegisterHandler!=null)
                mRegisterHandler.sendMessage(message);
            Log.d(TAG, "Enroll doInBackground: finished" );
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isStartEnrollRunning = false;
            Log.d(TAG, "Enroll onPostExecute: finished" );
        }

    }


}
