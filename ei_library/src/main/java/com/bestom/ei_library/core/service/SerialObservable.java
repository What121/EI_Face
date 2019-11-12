package com.bestom.ei_library.core.service;

import android.util.Log;

import com.bestom.ei_library.core.service.Interface.IObser.Observable;
import com.bestom.ei_library.core.service.Interface.IObser.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 被观察者，数据中转、通知服务
 */
public class SerialObservable implements Observable {
    private static final String TAG = "SerialObservable";

    //关键字volatile:用以声明变量的值可能随时会别的线程修改，
    // 使用volatile修饰的变量会强制将修改的值立即写入主存，
    // 主存中值的更新会使缓存中的值失效(非volatile变量不具备这样的特性，
    // 非volatile变量的值会被缓存，线程A更新了这个值，
    // 线程B读取这个变量的值时可能读到的并不是是线程A更新后的值)。
    // volatile会禁止指令重排。
    private static volatile SerialObservable instance;

    private List<SerialObserver> mSerialObservers;
    private String dataHex;

    public SerialObservable() {
        mSerialObservers=new ArrayList<SerialObserver>();
    }

    public static SerialObservable getInstance(){
        if (instance==null){
            //关键字synchronized:一个线程访问一个对象中的synchronized(this)同步代码块时，
            // 其他试图访问该对象的线程将被阻塞
            //该代码块只会同步执行,不会异步在不同线程
            synchronized (SerialObservable.class){
                if (instance==null){
                    instance=new SerialObservable();
                }
            }
        }
        return instance;
    }

    /**
     *注册观察者
     * @param o 观察者
     */
    @Override
    public void registerObserver(Observer o) {
        Log.i(TAG,"注册消息接收");
        mSerialObservers.add((SerialObserver) o);
    }

    /**
     * 移除观察者
     * @param o
     */
    @Override
    public void removeObserver(Observer o) {
        if (!mSerialObservers.isEmpty()){
            Log.i("receiver","移除消息接收");
            mSerialObservers.remove(o);
        }
    }

    /**
     * 通知观察者更新
     */
    @Override
    public void notifyObserver() {
        Log.i("receiver","被观察者数："+mSerialObservers.size()+"--"+dataHex);
        for (int i=0;i<mSerialObservers.size();i++){
            Log.i("receiver",i+""+mSerialObservers.get(i).toString());
            SerialObserver serialObserver=mSerialObservers.get(i);
            if (serialObserver!=null)
                serialObserver.dataReceived(dataHex);
        }
    }

    /**
     * 观察者更新的新信息
     * @param sHex  16进制字符串
     */
     public void setInfo(String sHex){
        this.dataHex=sHex;
        notifyObserver();
        Log.i("receiver","第二步 observer 更新");
    }
}
