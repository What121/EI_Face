package com.bestom.ei_library;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.bestom.ei_library.commons.utils.AssetFileUtil;
import com.bestom.ei_library.commons.utils.FLogs;
import com.bestom.ei_library.commons.utils.SPUtil;
import com.bestom.ei_library.core.manager.serial.SerialManager;
import com.wf.wffrdualcamapp;
import com.wf.wffrjni;

import java.util.concurrent.Semaphore;

public class EIFace {
    private static final String TAG = "EIFace";
    private static Context mContext;

    private static String[] binfiles = {"b1.bin", "f160tm.bin","p0.bin", "p1tc.bin", "p2tc.bin", "p3tc.bin", "p4tc.bin", "q31tm.bin","q103tm.bin", "s11tm.bin"};
    private static String[] configfiles = {"ei_config"};
    public static String DualFilePath;

    static Semaphore semaphore = new Semaphore(1);

    public static String Initialize(Context context){
        mContext=context;

        openSerial();
        //检测资源文件
//        copyAssets();
        //初始化算法接口的一些参数
//        initwff();
        //log control
        setLog(true,0);

        return DualFilePath;
    }

    public static void Release(){
        closeSerial();
    }

    //region wffrdualcamapp api
    public static int getState(){
        return wffrdualcamapp.getState();
    }

    public static void setState(int i){
        wffrdualcamapp.setState(i);
    }

    public static int startExecution(byte[] clrFrame, byte[] irFrame, int frameWidth, int frameHeight, String name){
        try {
            semaphore.acquire();
            int i = wffrdualcamapp.startExecution(clrFrame, irFrame, frameWidth, frameHeight, name);

            if (!TextUtils.isEmpty(name)){
                int RecordID = wffrjni.getLastAddedRecord();
            }
            semaphore.release();
            return i;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getFinishstate(){
        return wffrdualcamapp.finish_state;
    }

    public static int[][] getFaceCoordinates(){
        return wffrdualcamapp.getFaceCoordinates();
    }

    public static long getT2(){
        return wffrdualcamapp.t2;
    }

    public static float[] getConfidence(){
        return wffrdualcamapp.getConfidence();
    }

    public static String[] getNames(){
        return wffrdualcamapp.getNames();
    }

    public static long getTimeLeft(){
        return wffrdualcamapp.getTimeLeft();
    }

    public static int deletePerson(int recordID){
        return wffrdualcamapp.deletePerson(recordID);
    }

    public static int getDatabase(){
        return wffrdualcamapp.getDatabase();
    }

    public static String[] getDatabaseNames(){
        return (String[]) wffrdualcamapp.getDatabaseNames();
    }

    public static int[] getDatabaseRecords(){
        return wffrdualcamapp.getDatabaseRecords();
    }

    //endregion

    /**********************************************************************
     * 打开串口
     */
    private static void openSerial() {
        SerialManager.getInstance().turnOn();
    }

    /**********************************************************************
     * 配置串口
     * @param sPort 串口号
     * @param iBaud 波特率
     * @return 配置返回
     */
    private void config(String sPort, int iBaud) {
        SerialManager.getInstance().config(sPort, iBaud);
    }

    /**********************************************************************
     * 关闭串口
     */
    private static void closeSerial() {
        SerialManager.getInstance().turnOff();
    }

    /**********************************************************************
     * 设置Log日志
     * @param isOpen 是否打开日志
     * @param level 0:v, 1:d, 2:i, 3:w, 4:e,
     */
    private static void setLog(boolean isOpen, int level) {
        FLogs.setLogFlag(isOpen);
        FLogs.setLogLevel(level);
    }

    private static void copyAssets(){
        //.bin（二进制）文件
        if(!new AssetFileUtil(mContext).checkFilesExist(binfiles,1)){
            (new AssetFileUtil(mContext)).copyFilesFromAssets(binfiles,1);//copies files from assests to data file
        }
        //配置文件
        if(!new AssetFileUtil(mContext).checkFilesExist(configfiles,0)){
            (new AssetFileUtil(mContext)).copyFilesFromAssets(configfiles,0);//copies files from assests to data file
        }
    }

    private static void initwff(){
        setState(1);
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


    /**
     * su 提权 操作
     * @return
     */
//    public static boolean su(){
//       return ShellManager.getInstance().MustComd("");
//    }




}
