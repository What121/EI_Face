package com.bestom.ei_library.core.api;

import android.os.Handler;
import android.util.Log;

import com.bestom.ei_library.commons.constant.Const;
import com.bestom.ei_library.commons.constant.SerialCmdCode;
import com.bestom.ei_library.commons.constant.StatusCode;
import com.bestom.ei_library.commons.utils.DataTurn;
import com.bestom.ei_library.commons.utils.MyUtil;
import com.bestom.ei_library.commons.listener.DataReceivedListener;
import com.bestom.ei_library.commons.listener.ParseCallback;
import com.bestom.ei_library.commons.listener.RespListener;
import com.bestom.ei_library.commons.listener.RespSampleListener;
import com.bestom.ei_library.core.service.SerialObservable;
import com.bestom.ei_library.core.service.SerialObserver;

/**
 * 基础
 */
class BaseApi {
    private static final String TAG = "BaseApi";
    private com.bestom.ei_library.commons.utils.DataTurn DataTurn =new DataTurn();

    private static Handler mHandler = new Handler();
    

    /***********************************************************
     * 拼接命令
     * @param cmdCode 命令类型
     * @return 返回拼接完成命令
     */
    String jointCmd(SerialCmdCode cmdCode) {
        return jointCmd(cmdCode, "");
    }

    /***********************************************************
     * 拼接命令
     * 555a    03(bodysize)    (d3 **   (84)check )body
     * @param cmdCode 命令类型
     * @param bodyHex 传参体
     * @return 返回拼接完成命令
     */
    String jointCmd(SerialCmdCode cmdCode, String bodyHex) {
        String cmd="";
        String magic= Const.Serial_Send_MAGIC;
        String cmdcode=cmdCode.getHexStr();
        String bodysizeHex,checkvale,cmdcheck ;

        //region 此协议不需要 进行分包处理
        int j;
//        int maxbodysize=150*1024+ 4;
        int maxbodysize=150*1024+3;
        int bodysize=bodyHex.length() / 2 + 2;
        int yusize=bodysize%maxbodysize;
        //循环包次数
        j=(yusize!=0)?bodysize/maxbodysize+1:bodysize/maxbodysize;
        //endregion

        if (j==1){
            bodysizeHex = DataTurn.IntToHex(bodysize, 2);
            checkvale = magic+bodysizeHex+cmdcode+bodyHex;
            cmdcheck= MyUtil.sumCheck(checkvale);
            cmd = checkvale + cmdcheck;
        }else {
            Log.e(TAG, "jointCmd: 未进行分包" );
        }
        Log.i("jointCmd",cmd);
        return cmd;
    }

    /**************************************************************
     * 注册观察者（限定时间观察者）
     * @param code 应答类型
     * @param timeout 超时时间
     * @param listener 监听
     * @param callback 返回
     */
    void registerAndRemoveObserver(final SerialCmdCode code, long timeout, final RespListener listener, final ParseCallback callback) {
        final boolean[] isHas = {false}; //是否存在结果数据

        final SerialObserver timeRespobserver = new SerialObserver();
        timeRespobserver.setOnDataReceivedListener(new DataReceivedListener() {
            @Override
            public void data(String dataHex) {
                //Log.i(TAG, "DATA HEX:" + dataHex);
                if (dataHex.startsWith(Const.Serial_Send_MAGIC) && dataHex.length() >= 10 &&
                        dataHex.substring(6, 8).equals(code.getHexStr())) {
                    isHas[0] = true; //存在结果数据
                    int bodysize = DataTurn.HexToInt(dataHex.substring(4, 6));
                    int allsize = 6 + bodysize * 2;
                    if (dataHex.length() == allsize) {
                        String checkvalue = dataHex.substring(0, dataHex.length()-2);
                        String check = MyUtil.sumCheck(checkvalue);
                        if (check.equalsIgnoreCase(dataHex.substring(dataHex.length()-2))) {
                            String responsebody = dataHex.substring(6,dataHex.length()-2);
                            Log.i("Serial--response", "" + responsebody);
                            callback.result(responsebody);
                        } else { //校验位错误
                            listener.onFailure(StatusCode.CHECK_ERR.getCode(),
                                    StatusCode.FORMAT_ERR.getMsg());
                        }
                    } else { //数据格式错误
                        listener.onFailure(StatusCode.FORMAT_ERR.getCode(),
                                StatusCode.FORMAT_ERR.getMsg());
                    }
                } else {

                }
            }
        });
        SerialObservable.getInstance().registerObserver(timeRespobserver);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //没得到数据结果，进行超时处理
                if (!isHas[0]) {
                    listener.onFailure(StatusCode.TIMEOUT.getCode(),
                            StatusCode.TIMEOUT.getMsg());
                }
                SerialObservable.getInstance().removeObserver(timeRespobserver);
                mHandler.removeCallbacks(this);
            }
        }, timeout);
    }

    /**************************************************************
     * 注册观察者（限定时间观察者）
     * @param code 应答类型
     * @param timeout 超时时间
     * @param listener 监听
     * @param callback 返回
     */
    void registerAndRemoveObserver(final SerialCmdCode code, long timeout, final RespSampleListener listener, final ParseCallback callback) {
        final boolean[] isHas = {false}; //是否存在结果数据

        final SerialObserver timeRespobserver = new SerialObserver();
        timeRespobserver.setOnDataReceivedListener(new DataReceivedListener() {
            @Override
            public void data(String dataHex) {
                Log.i(TAG, "nDataReceivedListener DATA HEX:" + dataHex);
                if (dataHex.startsWith(Const.Serial_Replay_MAGIC) && dataHex.length() >= 10 &&
                        dataHex.substring(6, 8).equals(code.getHexStr())) {
                    isHas[0] = true; //存在结果数据
                    int bodysize = DataTurn.HexToInt(dataHex.substring(4, 6));
                    int allsize = 6 + bodysize * 2;
                    if (dataHex.length() == allsize) {
                        String checkvalue = dataHex.substring(0, dataHex.length()-2);
                        String check = MyUtil.sumCheck(checkvalue);
                        if (check.equalsIgnoreCase(dataHex.substring(dataHex.length()-2))) {
                            String responsebody = dataHex.substring(8,dataHex.length()-2);
                            Log.i("Serial--response", "" + responsebody);
                            callback.result(responsebody);
                        } else { //校验位错误
                            listener.onFailure(StatusCode.CHECK_ERR.getCode(),
                                    StatusCode.FORMAT_ERR.getMsg());
                        }
                    } else { //数据格式错误
                        listener.onFailure(StatusCode.FORMAT_ERR.getCode(),
                                StatusCode.FORMAT_ERR.getMsg());
                    }
                } else {

                }
            }
        });
        SerialObservable.getInstance().registerObserver(timeRespobserver);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //没得到数据结果，进行超时处理
                if (!isHas[0]) {
                    listener.onFailure(StatusCode.TIMEOUT.getCode(),
                            StatusCode.TIMEOUT.getMsg());
                }
                SerialObservable.getInstance().removeObserver(timeRespobserver);
                mHandler.removeCallbacks(this);
            }
        }, timeout);



    }


    /**************************************************************
     * 注册观察者（无限定时间观察者）
     * @param code 应答类型
     * @param listener 监听
     * @param callback 返回
     */
    void noTimeregisterAndRemoveObserver(final SerialCmdCode code, final RespListener listener, final ParseCallback callback) {
        final boolean[] isHas = {false}; //是否存在结果数据

        final SerialObserver timeRespobserver = new SerialObserver();
        timeRespobserver.setOnDataReceivedListener(new DataReceivedListener() {
            @Override
            public void data(String dataHex) {
                //Log.i(TAG, "DATA HEX:" + dataHex);
                if (dataHex.startsWith(Const.Serial_Send_MAGIC) && dataHex.length() >= 10 &&
                        dataHex.substring(6, 8).equals(code.getHexStr())) {
                    isHas[0] = true; //存在结果数据
                    int bodysize = DataTurn.HexToInt(dataHex.substring(4, 6));
                    int allsize = 6 + bodysize * 2;
                    if (dataHex.length() == allsize) {
                        String checkvalue = dataHex.substring(0, dataHex.length()-2);
                        String check = MyUtil.sumCheck(checkvalue);
                        if (check.equalsIgnoreCase(dataHex.substring(dataHex.length()-2))) {
                            String responsebody = dataHex.substring(6,dataHex.length()-2);
                            Log.i("Serial--response", "" + responsebody);
                            callback.result(responsebody);
                        } else { //校验位错误
                            listener.onFailure(StatusCode.CHECK_ERR.getCode(),
                                    StatusCode.FORMAT_ERR.getMsg());
                        }
                    } else { //数据格式错误
                        listener.onFailure(StatusCode.FORMAT_ERR.getCode(),
                                StatusCode.FORMAT_ERR.getMsg());
                    }
                } else {

                }
            }
        });
        SerialObservable.getInstance().registerObserver(timeRespobserver);

    }


}
