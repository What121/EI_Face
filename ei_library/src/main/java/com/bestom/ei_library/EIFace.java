package com.bestom.ei_library;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import com.bestom.ei_library.commons.constant.EICode;
import com.bestom.ei_library.commons.constant.Settings;
import com.bestom.ei_library.commons.utils.AssetFileUtil;
import com.bestom.ei_library.commons.utils.FLogs;
import com.bestom.ei_library.commons.utils.SPUtil;
import com.bestom.ei_library.core.api.DBApi;
import com.bestom.ei_library.core.manager.Serial.SerialManager;
import com.wf.wffrdualcamapp;
import com.wf.wffrjni;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class EIFace {
    private static final String TAG = "EIFace";
    private static Context mContext;

//    private static String[] binfiles = {"b1.bin", "f160tm.bin","p0.bin", "p1tc.bin", "p2tc.bin", "p3tc.bin", "p4tc.bin", "q31tm.bin","q103tm.bin", "s11tm.bin"};
    private static String[] binfiles = {"b1.bin", "f160tm.bin","p0.bin", "p1tc.bin", "p2tc.bin", "p3tc.bin", "p4tc.bin", "q35tm.bin","q103tm.bin", "s11tm.bin"};
    private static String[] configfiles = {"ei_config"};
    private static String DualFilePath;
    private static String datapath ;
    private static String filepath ;
    private static String cachepath ;

    private static CountDownLatch mCountDownLatch;

    static Semaphore semaphore = new Semaphore(1);

    static {
        System.loadLibrary("wffr");
        System.loadLibrary("wffrjni");

    }

    private static class  sqThread extends Thread {
        @Override
        public void run() {
            try {
                //在线授权
                SetOnlineLicensing(1);
                setVerifyLic(DualFilePath);
                sleep(5*1000);
                mCountDownLatch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String Initialize(Context context){
        mContext=context;
        datapath=AssetFileUtil.getInstance(mContext).getDatapath();
        filepath=AssetFileUtil.getInstance(mContext).getFilepath();
        cachepath=AssetFileUtil.getInstance(mContext).getCachepath();
        DualFilePath=AssetFileUtil.getInstance(mContext).getDualFilePath();

        mCountDownLatch=new CountDownLatch(1);

        //检测资源文件
        copyAssets();
        Log.d(TAG, "DualFilePath: "+DualFilePath);

        new sqThread().run();

        try {
            Log.d(TAG, "Initialize: await()");
            mCountDownLatch.await();
//            //授权文件路径
//            setVerifyLic(DualFilePath);
            //初始化算法接口的一些参数
            initwff();
            //initDB()
            initDB();
//        openSerial();

            //log control
            setLog(true,0);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return DualFilePath;
    }

    public static void Release(){
        closeSerial();
        wffrjni.Release();
    }

    public static String getDualFilePath() {
        return DualFilePath;
    }

    public static String getDatapath() {
        return datapath;
    }

    public static String getFilepath() {
        return filepath;
    }

    public static String getCachepath() {
        return cachepath;
    }

    //region wffrdualcamapp api
    public static int getState(){
        return wffrdualcamapp.getState();
    }

    public static void setState(int i){
        wffrdualcamapp.setState(i);
    }

    public static int startExecution(byte[] clrFrame, byte[] irFrame, int frameWidth, int frameHeight){
        return startExecution(clrFrame,irFrame,frameWidth,frameHeight,"");
    }

    public static int startExecution(byte[] clrFrame, byte[] irFrame, int frameWidth, int frameHeight, String msg){
        try {
            semaphore.acquire();
            int i = -3;
            if (TextUtils.isEmpty(msg)) {
                i = wffrdualcamapp.startExecutionFast(clrFrame, irFrame, frameWidth, frameHeight, msg);
//                i = wffrdualcamapp.startExecution(clrFrame, irFrame, frameWidth, frameHeight, msg);
            }else {
                String id = msg.substring(msg.lastIndexOf(',')+1).trim();
                String name = msg.substring(0, msg.lastIndexOf(',')).trim();
                if (DBApi.queryPersonInfoByID(id).getCount()<=0){
                    //算法注册
                    i = wffrdualcamapp.startExecution(clrFrame, irFrame, frameWidth, frameHeight, id);
                    int RecordID = wffrjni.getLastAddedRecord();
                    boolean flag = AssetFileUtil.getInstance(mContext).checkFilesExist(new String[]{"pid"+RecordID},1);
                    Log.d(TAG, "register RecordID: "+RecordID);
                    //算法注册通过
                    if (i==0){
                        //region 检查太慢，总是卡死 检查注册的资源文件是否存在,并写入DB 可靠操作
//                        if (flag) {
//                            DBApi.insertPersonInfo(RecordID, id, name);
//                        }else {
//                            return EICode.DB_ERROR.getCode();
//                        }
                        //endregion
                        DBApi.insertPersonInfo(RecordID, id, name);
                    }
                }else {
                    return EICode.DB_ERROR_ID.getCode();
                }
            }
            semaphore.release();
            return i;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int stopExecution(){
        return wffrdualcamapp.stopExecution();
    }

    public static int EnrollFromJpegFile(String JpegFilePath, String msg){
        try {
            semaphore.acquire();
            int i = -3;
            String id = msg.substring(msg.lastIndexOf(',')+1).trim();
            String name = msg.substring(0, msg.lastIndexOf(',')).trim();
            if (DBApi.queryPersonInfoByID(id).getCount()<=0){
                //算法注册
                i = wffrdualcamapp.runEnrollFromJpegFile(JpegFilePath, id);
                Log.d(TAG, "register RecordID: "+i);
                //算法注册通过
                if (i>=0){
                    //region 检查注册的资源文件是否存在
                    if (AssetFileUtil.getInstance(mContext).checkFilesExist(new String[]{"pid"+i},1)) {
                        DBApi.insertPersonInfo(i, id, name);
                    }else {
                        i=EICode.DB_ERROR.getCode();
                    }
                    //endregion
                }else {
                    i=EICode.DB_ERROR_RECORDID.getCode();
                }
            }else {
                i=EICode.DB_ERROR_ID.getCode();
            }
            semaphore.release();

            semaphore.acquire();
            wffrdualcamapp.updateFromPCDB();
            semaphore.release();
            Log.d(TAG, "registerImg return: "+i);
            return i;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void setFinishState(int val){
        wffrdualcamapp.setFinishState(val);
    }

    public static int getFinishState(){
        return wffrdualcamapp.getFinishState();
    }

    public static int[][] getFaceCoordinates(){
        return wffrdualcamapp.getFaceCoordinates();
    }

    public static float[] getConfidence(){
        return wffrdualcamapp.getConfidence();
    }

    public static String getNames(){
        String id=getIDs();
        if (!TextUtils.isEmpty(id.trim())){
            return DBApi.queryPersonNameByID(id);
        }
        return "";
    }

    public static String getIDs(){
        if (wffrdualcamapp.getNames().length!=0){
            return wffrdualcamapp.getNames()[0];
        }
        return "";
    }

    public static long getT2(){
        return wffrdualcamapp.t2;
    }

    /**
     * set asset path
     * @param AssetPath
     */
    private static void setAssetPath(String AssetPath){
        wffrdualcamapp.setAssetPath(DualFilePath);
    }

    public static String getAssetPath(){
        return wffrdualcamapp.getAssetPath();
    }


    /**
     *Lic Path
     * @param path
     */
    private static void setVerifyLic(String path){
        wffrdualcamapp.VerifyLic(path);
    }

    /**
     * SetOnlineLicensing
     * @param enable:1 diable:0
     */
    private static void SetOnlineLicensing(int enable){
        wffrdualcamapp.SetOnlineLicensing(enable);
    }

    /**
     * GetOnlineLicensingFlag
     * @return
     */
    public static int GetOnlineLicensingFlag(){
        return  wffrdualcamapp.GetOnlineLicensingFlag();
    }


    /**
     *  Should be set before initialize() API is called
     *  SetMinFaceDetectionSizePercent
     * @param val
     * @return
     */
    public static int SetMinFaceDetectionSizePercent(int val){
        SPUtil.putValue(mContext,"facemodel", val);
        return wffrjni.SetMinFaceDetectionSizePercent(val);
    }

    /**
     * etMinFaceDetectionSizePercent
     * @return
     */
    public static int GetMinFaceDetectionSizePercent(){
        return wffrjni.GetMinFaceDetectionSizePercent();
    }

    /**
     * 设置识别门槛
     * @param value
     * @return
     */
    public static int SetRecognitionThreshold(float value){
        SPUtil.putValue(mContext,"threshold", value);
        return wffrjni.SetRecognitionThreshold(value);
    }

    /**
     * 获取 识别门槛
     * @return
     */
    public static float GetRecognitionThreshold(){
        return SPUtil.getValue(mContext,"threshold", wffrjni.GetRecognitionThreshold());
    }

    public static long getTimeLeft(){
        return wffrdualcamapp.getTimeLeft();
    }


    public static Cursor getDBALL(){
        return DBApi.queryAll();
    }

    public static String getDBNamebyID(String ID){
        return DBApi.queryPersonNameByID(ID);
    }

    public static int getEIDatabase(){
        return wffrdualcamapp.getDatabase();
    }

    public static String[] getEIDatabaseIDs(){
        return (String[]) wffrdualcamapp.getDatabaseNames();
    }

    public static int[] getEIDatabaseRecords(){
        return wffrdualcamapp.getDatabaseRecords();
    }

    public static int deletePerson(int recordID){
        int var = wffrdualcamapp.deletePerson(recordID);
        DBApi.deletePersonInfoByRecordID(recordID);
        return var;
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
        if(!AssetFileUtil.getInstance(mContext).checkFilesExist(binfiles,1)){
            (AssetFileUtil.getInstance(mContext)).copyFilesFromAssets(binfiles,1);//copies files from assests to data file
        }
        //配置文件
        if(!AssetFileUtil.getInstance(mContext).checkFilesExist(configfiles,0)){
            (AssetFileUtil.getInstance(mContext)).copyFilesFromAssets(configfiles,0);//copies files from assests to data file
        }
    }

    private static void initwff(){
        setState(1);

        setFinishState(1);

        setAssetPath(DualFilePath);

        //设置识别门槛
        SetRecognitionThreshold(SPUtil.getValue(mContext,"threshold", GetRecognitionThreshold()));
        //最小人脸占屏幕百分比
        SetMinFaceDetectionSizePercent(10);
        //wffrjni.SetVerbose("",1);
        //wffrjni.setAndroidVerbose(0);
//        wffrjni.EnableImageSaveForDebugging(1);
        //wffrjni.SetSpoofingSensitivity(3);
        //wffrjni.SetDualcamBGReject(0);
        //wffrjni.SetSingleCamSpoofThreshold(-2);
        //wffrjni.SetAntiSpoofBlockingFlag(0);
    }

    private static void initDB(){
        DBApi.init(mContext);
    }


    /**
     * su 提权 操作
     * @return
     */
//    public static boolean su(){
//       return ShellManager.getInstance().MustComd("");
//    }




}
