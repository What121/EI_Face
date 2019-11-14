package com.bestom.ei_library.core.manager.Serial;

import android.os.Message;
import android.util.Log;

import com.bestom.ei_library.Handler.SerialManagerHandler;
import com.bestom.ei_library.commons.constant.Const;
import com.bestom.ei_library.commons.exceptions.SerialException;
import com.bestom.ei_library.commons.utils.DataTurn;

import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * 串口管理类
 */
public class SerialManager extends SerialHelper {
    private static final String TAG = "SerialManager";
    private static volatile SerialManager instance;
    private static final int Replay_WHAT = 1;

    private String sPort = "/dev/ttyS3";
    private int iBaudRate = 115200;
    //private int iBaudRate = 9600;

    private com.bestom.ei_library.commons.utils.DataTurn DataTurn =new DataTurn();

    private String rData = "";
    private StringBuilder dataBuilder;

    private boolean _iSerialflag=false;
    private CheckDataThread mCheckDataThread;
    private SerialManagerHandler mSerialManagerHandler;

    private SerialManager() {
        mSerialManagerHandler = new SerialManagerHandler();
        dataBuilder = new StringBuilder();
        config(sPort,iBaudRate);
    }

    public static SerialManager getInstance() {
        if (instance == null) {
            synchronized (SerialManager.class) {
                if (instance == null) {
                    instance = new SerialManager();
                }
            }
        }
        return instance;
    }

    /**********************************************************************
     * 配置串口
     * @param port 串口号
     * @param iBaud 波特率
     */
    public void config(String port, int iBaud) {
        this.sPort = port;
        this.iBaudRate = iBaud;

        //初始化port,baudrate
        setPort(sPort);
        setBaudRate(iBaudRate);
    }

    /**********************************************************************
     * 打开串口
     */
    public void turnOn() {
        try {
            open();
            setPort(sPort);
            setBaudRate(iBaudRate);
        } catch (SecurityException e) {
            Log.e(TAG, "打开串口失败:没有串口读/写权限!");
        } catch (IOException e) {
            Log.e(TAG, "打开串口失败:未知错误!");
        } catch (InvalidParameterException e) {
            Log.e(TAG, "打开串口失败:参数错误!");
        }
    }

    /**********************************************************************
     * 关闭串口
     */
    public void turnOff() {
        //stopSend();
        _iSerialflag=false;
        close();
    }

    /**********************************************************************
     * 向串口发送16进制字符串数据
     * @param sHex 16进制的指令字符串
     */
    public void sendHex(String sHex) throws SerialException {
        if (!isOpen()) {
            throw new SerialException("请打开串口，再发送数据！");
        }
        super.sendHex(sHex);
    }

    private class CheckDataThread extends Thread {
        @Override
        public void run() {
            super.run();
            int index;
            while (_iSerialflag){
                index= dataBuilder.indexOf(Const.Serial_Replay_MAGIC);
                    if ((index == 0)&&dataBuilder.length() >= 6) {
//                    55 a5 03 d1 00/01 **(check)
                            String body_size_Hex = dataBuilder.substring(4, 6);
                            int body_size = new  DataTurn().HexToInt(body_size_Hex);
                            int all_size = 6 + body_size * 2;
                            if (dataBuilder.length() >= all_size) {
                                Log.i("receiver","*******标准数据通过，发送更新，");
                                Message msg = new Message();
                                msg.what = Replay_WHAT;
                                msg.obj = dataBuilder.substring(0, all_size);
                                mSerialManagerHandler.sendMessage(msg);
                                dataBuilder.delete(0, all_size);
                            }
                    }else if ((dataBuilder.length()>0)&&(index>0)){
                        Log.e(TAG, "ERROR-Serial DATA HEX(index="+index+"):" + dataBuilder);
                        Log.e(TAG, "index：" + dataBuilder.indexOf(Const.Serial_Replay_MAGIC));
                        dataBuilder.delete(0,index);
                    }
            }
        }
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        dataBuilder.append(DataTurn.ByteArrToHex(buffer));
        Log.d("receiver", "onDataReceived dataBuilder is "+dataBuilder.toString());
        if (mCheckDataThread==null){
            _iSerialflag=true;
            mCheckDataThread=new CheckDataThread();
            mCheckDataThread.start();
        }

    }

}
