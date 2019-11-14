package com.bestom.ei_library.core.api;

import com.bestom.ei_library.core.manager.File.FilesManager;
import com.bestom.ei_library.core.manager.Shell.ShellManager;

import static com.bestom.ei_library.commons.constant.Const.REBOOT;

public class SysApi extends BaseApi {
    private ShellManager mShellManager;

    public SysApi(){
        mShellManager=new ShellManager();
    }

    /**
     * reboot
     */
    public void reboot(){
        mShellManager.MustComd(REBOOT);
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
