package com.bestom.eiface;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import com.bestom.ei_library.commons.constant.SerialCmdCode;
import com.bestom.ei_library.commons.constant.StatusCode;
import com.bestom.ei_library.commons.utils.AssetFileUtil;
import com.bestom.ei_library.commons.utils.SPUtil;
import com.bestom.ei_library.core.api.SerialApi;
import com.bestom.ei_library.core.service.Interface.Listener.RespSampleListener;
import com.wf.wffrdualcamapp;
import com.wf.wffrjni;

public class MyApp extends Application {
    private static final String TAG = "MyApp";
    private Context mContext;

    public static boolean MirrorX = false ;
    public static String DualFilePath;
    public static String[] permissions;
    public static  final int CAMERA_WIDTH =640;
    public static  final int CAMERA_HEIGHT =480;

    private Handler mtimehandler;
    public static PowerManager pm;
    public static PowerManager.WakeLock mWakeLock;
    public static SerialApi mSerialApi;

    private String[] binfiles = {"b1.bin", "f160tm.bin","p0.bin", "p1tc.bin", "p2tc.bin", "p3tc.bin", "p4tc.bin", "q31tm.bin","q103tm.bin", "s11tm.bin"};
    private String[] configfiles = {"ei_config"};

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化
        init();

        //check change Screen state
        checkScreanLight();
    }

    private void initwff(){
        wffrdualcamapp.setState(1);
        wffrdualcamapp.finish_state = 1;

        DualFilePath = new AssetFileUtil(mContext).getDualFilePath();
        Log.d(TAG, "DualFilePath: "+DualFilePath);
        wffrdualcamapp.setAssetPath(DualFilePath);

        //设置识别门槛
        wffrjni.SetRecognitionThreshold( SPUtil.getValue(mContext,"threshold", (int) wffrjni.GetRecognitionThreshold()) );
        //最小人脸占屏幕百分比
        wffrjni.SetMinFaceDetectionSizePercent(5);
        //wffrjni.SetVerbose("",1);
        //wffrjni.setAndroidVerbose(0);
//        wffrjni.EnableImageSaveForDebugging(1);
        //wffrjni.SetSpoofingSensitivity(3);
        //wffrjni.SetDualcamBGReject(0);
        //wffrjni.SetSingleCamSpoofThreshold(-2);
        //wffrjni.SetAntiSpoofBlockingFlag(0);
    }

    @SuppressLint("InvalidWakeLockTag")
    private void init(){
        mContext=this;
        mSerialApi=new SerialApi();

        copyAssets();
        //
        initwff();
        //初始化算法、串口
//        DualFilePath = EIFace.Initialize(mContext);
//        SerialManager.getInstance().turnOn();

        mtimehandler=new Handler();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock= pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
        //设置屏幕保持常亮，需要释放release（）
        mWakeLock.setReferenceCounted(false);
        mWakeLock.acquire();

        Log.d(TAG, "init: finished");
    }

    private void copyAssets(){
        //.bin（二进制）文件
        if(!new AssetFileUtil(mContext).checkFilesExist(binfiles,1)){
            (new AssetFileUtil(mContext)).copyFilesFromAssets(binfiles,1);//copies files from assests to data file
        }
        //配置文件
        if(!new AssetFileUtil(mContext).checkFilesExist(configfiles,0)){
            (new AssetFileUtil(mContext)).copyFilesFromAssets(configfiles,0);//copies files from assests to data file
        }
    }

    private void checkScreanLight(){
        mSerialApi.setStatus(SerialCmdCode.SERIAL_CMD_STATUS, true, new RespSampleListener<String>() {
            @Override
            public void onSuccess(int code, String s) {
                Log.d(TAG, "setStatus onSuccess: code"+code+" ,msg:"+s);
                if (code== StatusCode.SUCCESS.getCode()){
                    //打开radar成功,获取数据
                    checkScreenThread.start();
                }else {
                    Log.d(TAG, "setStatus onSuccess code: "+code+" ,msg is "+s);
                }
            }

            @Override
            public void onFailure(int code, String errMsg) {
                Log.e(TAG, "setStatus onFailure code: "+code+",errmsg is "+errMsg);
            }
        });

    }

    //region checkScreenThread
    private Thread checkScreenThread=new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                try {
                    Thread.sleep(3000);
                    mSerialApi.getRadarInfo(SerialCmdCode.SERIAL_CMD_INFO, new RespSampleListener<Integer>() {
                        @Override
                        public void onSuccess(int code, Integer integer) {
                            Log.d(TAG, "getRadarInfo onSuccess: code"+code+" ,value:"+integer);
                            if (integer <= 60) {
                                mtimehandler.postDelayed(new Runnable() {
                                    public void run() {
                                        if (mWakeLock!=null){

                                            mWakeLock.release();
                                            mWakeLock=null;

                                            //region goio led status change
//                                            mSystemapi.closeLed(new RespListener() {
//                                                @Override
//                                                public void onSuccess(int code, String msg) {
//                                                    SPUtil.putValue(mContext, mSetting.LED_STATE, false);
//                                                    Log.i(TAG,"关闭led成功");
//                                                }
//
//                                                @Override
//                                                public void onFailure(int code, String errMsg) {
//                                                    Log.i(TAG,"关闭led失败");
//                                                }
//                                            });
                                            //endregion

                                        }
                                    }
                                }, 100);
                            }
                            else {
                                mtimehandler.postDelayed(new Runnable() {
                                    @SuppressLint("InvalidWakeLockTag")
                                    public void run() {
                                        if (mWakeLock==null){
                                            mWakeLock= pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
                                        }

                                        mWakeLock.setReferenceCounted(false);
                                        mWakeLock.acquire();

                                        //region gpio led status changed
//                                        mSystemapi.openLed(new RespListener() {
//                                            @Override
//                                            public void onSuccess(int code, String msg) {
//                                                SPUtil.putValue(mContext, mSetting.LED_STATE, true);
//                                                Log.i(TAG,"打开LED成功");
//                                            }
//
//                                            @Override
//                                            public void onFailure(int code, String errMsg) {
//                                                Log.i(TAG,"打开LED失败");
//                                            }
//                                        });
                                        //endregion

                                    }
                                }, 100);
                            }
                        }

                        @Override
                        public void onFailure(int code, String errMsg) {
                            Log.e(TAG,"执行失败,代码："+code+errMsg);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    //endregion




}
