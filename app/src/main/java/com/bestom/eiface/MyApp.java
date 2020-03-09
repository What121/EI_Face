package com.bestom.eiface;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import com.bestom.ei_library.EIFace;
import com.bestom.ei_library.commons.utils.SPUtil;
import com.bestom.ei_library.core.api.SerialApi;
import com.bestom.ei_library.core.api.SysApi;
import com.bestom.ei_library.commons.constant.Settings;

public class MyApp extends Application {
    private static final String TAG = "MyApp";
    private Context mContext;

    public static boolean MirrorX = false ;
    public static String DualFilePath;
    public static String Datapath;
    public static String Cachepath;
    public static String Filepath;

    public static String[] permissions;
    public static  final int CAMERA_WIDTH =640;
    public static  final int CAMERA_HEIGHT =480;

    private Handler mtimehandler;
    public static PowerManager pm;
    public static PowerManager.WakeLock mWakeLock;
    public static SerialApi mSerialApi;
    public static SysApi mSysApi;

    public static boolean face_state;
    public static int face_times;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化
        init();


        checkScreenThread.start();
    }

    @SuppressLint("InvalidWakeLockTag")
    private void init(){
        mContext=this;
        mSerialApi=new SerialApi();
        mSysApi=new SysApi(mContext);
        face_state= SPUtil.getValue(mContext, Settings.FACE_STATE,true);
        face_times= SPUtil.getValue(mContext,Settings.FACE_TIMES,1);

        //初始化算法
        EIFace.Initialize(mContext);
        DualFilePath=EIFace.getDualFilePath();
        Datapath = EIFace.getDatapath();
        Filepath = EIFace.getFilepath();
        Cachepath = EIFace.getCachepath();

        mSysApi.writeLed("0");
        SPUtil.putValue(mContext, Settings.FACE_IR,false);

        mtimehandler=new Handler();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock= pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
        //设置屏幕保持常亮，需要释放release（）
        mWakeLock.setReferenceCounted(false);
        mWakeLock.acquire();

        //region test 485
        mSysApi.write485("1");
        EIFace.open485Serial();
        EIFace.serial485_sendTxt("fdsdfsfsdfsdfsdf");
        //endregion

        Log.d(TAG, "init: finished");
    }
    //region checkScreenThread
    private Thread checkScreenThread=new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                try {
                    Thread.sleep(3000);

                    //region test use random int control screen and led
                    int i = (int)(1+Math.random()*(10-1+1));
                    Log.d(TAG, "run: random int is "+i);
                    if (i%2==0){
                        mtimehandler.postDelayed(new Runnable() {
                            public void run() {
                                Log.d(TAG, "mtimehandler: wakelock release ");
                                if (mWakeLock!=null){

                                    mWakeLock.release();
                                    mWakeLock=null;

                                    //region goio led status change
                                    mSysApi.writeLed("0");
                                    //endregion

                                }
                            }
                        }, 100);
                    }
                    else {
                        mtimehandler.postDelayed(new Runnable() {
                            @SuppressLint("InvalidWakeLockTag")
                            public void run() {
                                Log.d(TAG, "mtimehandler: wakelock acquire ");
                                if (mWakeLock == null) {
                                    mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
                                }

                                mWakeLock.setReferenceCounted(false);
                                mWakeLock.acquire();

                                //region gpio led status changed
                                mSysApi.writeLed("1");
                                //endregion

                            }
                        }, 100);
                    }
                    //endregion

                    //region 获取雷达数据
                    /*
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
                    */
                    //endregion

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    //endregion




}
