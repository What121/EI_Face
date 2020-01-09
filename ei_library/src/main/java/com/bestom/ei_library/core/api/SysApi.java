package com.bestom.ei_library.core.api;

import android.content.Context;

import com.bestom.ei_library.core.manager.File.FilesManager;
import com.bestom.ei_library.core.manager.OTA.OTAManager;
import com.bestom.ei_library.core.manager.Shell.ShellManager;

import java.io.File;

import static com.bestom.ei_library.commons.constant.Const.REBOOT;

public class SysApi{
    private ShellManager mShellManager;
    private Context mContext;

    public SysApi(Context context){
        mContext=context;
        mShellManager=new ShellManager();
    }

    /**
     * reboot
     */
    public void reboot(){
        mShellManager.MustComd(REBOOT);
    }

    public boolean write485(String val){
        return FilesManager.getInstance().write485(val);
    }

    public boolean writeLed(String val){
        return FilesManager.getInstance().writeLed(val);
    }

    public boolean writeRelay(String val){
        return FilesManager.getInstance().writeRelay(val);
    }

    public String readPower(){
        return FilesManager.getInstance().readPower();
    }

    /**
     * system function
     */
    public void installPackage(File packageFile){
        OTAManager.getInstance(mContext).installPackage(packageFile);
    }

    public boolean checkRKimage(String path){
        return  OTAManager.getInstance(mContext).checkRKimage(path);
    }

    public String getCurrentFirmwareVersion(){
        return OTAManager.getInstance(mContext).getCurrentFirmwareVersion();
    }

    public String getProductName(){
        return OTAManager.getInstance(mContext).getProductName();
    }

    public String getSystemVersion(){
        return OTAManager.getInstance(mContext).getSystemVersion();
    }

    public String getProductSN(){
        return OTAManager.getInstance(mContext).getProductSN();
    }

    public String getImageVersion(String path){
        return OTAManager.getInstance(mContext).getImageVersion(path);
    }

    public String getImageProductName(String path){
        return OTAManager.getInstance(mContext).getImageProductName(path);
    }

    //region ***mShellManager control gpio is hide ,use FilesManager to control gpio file code
//    /**
//     * @param path
//     * @param respListener
//     */
//    private void readValue(String path, RespSampleListener<Integer> respListener){
//        String cmd="cat /sys/"+path+"/val";
//        String result=mShellManager.StingComd(cmd) ;
//        if (result!=null){
//            int i=Integer.parseInt(result);
//            if (i==1||i==0){
//                respListener.onSuccess(StatusCode.SUCCESS.getCode(),i);
//            }else {
//                String msg="gpio  read  error !!!";
//                respListener.onFailure(StatusCode.ERROR.getCode(),msg);
//            }
//        }
//    }
//
//    /**
//     *led GPIO
//     */
//    public void writeValue(String path, int value , RespListener respListener){
//        String cmd="echo "+value+" "+" >/sys/"+path+"/val";
//        boolean flag=mShellManager.MustComd(cmd);
//        if (flag){
//            respListener.onSuccess(StatusCode.SUCCESS.getCode(),StatusCode.SUCCESS.getMsg());
//        }else {
//            String msg="gpio  write  error !!!";
//            respListener.onFailure(StatusCode.ERROR.getCode(),msg);
//        }
//    }
//
//    /**
//     * power GPIO
//     * @return
//     */
//    public int readPower(){
//        String cmd="cat /sys/bstrpower/val";
//        String result=mShellManager.StingComd (cmd) ;
//        if (result!=null)
//            return Integer.parseInt(result);
//        return 3;
//    }
//
    //endregion


}
