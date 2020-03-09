package com.bestom.eiface.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static long DownClickTime;
    private ActivityManager activityManager;
    private boolean flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         activityManager=  (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ActivityManager.RunningTaskInfo info = activityManager.getRunningTasks(1).get(0);
        String shortClassName = info.topActivity.getShortClassName();    //类名
        String className = info.topActivity.getClassName();              //完整类名
        String packageName = info.topActivity.getPackageName();          //包名
        if (!className.equals("com.bestom.eiface.activity.ResetAndLoaderActivity")){
            if (event.getKeyCode()==24){
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!flag){
                        flag=true;
                        DownClickTime=System.currentTimeMillis();
                        Log.d(TAG, "dispatchKeyEvent: key down");
                    }
                }else if (event.getAction() == MotionEvent.ACTION_UP) {
                    flag=false;
                    long timeD = System.currentTimeMillis()-DownClickTime;
                    Intent intent = new Intent(this, ResetAndLoaderActivity.class);
                    Log.d(TAG, "dispatchKeyEvent: key up,timeD is " +timeD);
                    if (timeD>=3000){
                        if (timeD>=8000) {
                            //跳转 是否进入烧录弹窗
                            intent.putExtra("type", 1);
                            Log.d(TAG, "dispatchKeyEvent: type is 1 to jump");
                        }else {
                            //跳转 确定是否跳转二维码弹窗
                            intent.putExtra("type",0);
                            Log.d(TAG, "dispatchKeyEvent: type is 0 to jump");
                        }

                        startActivity(intent);
                    }else {
                        Log.d(TAG, "dispatchKeyEvent: timeD < 3000 ms");
                    }
                }
            }
        }


        return super.dispatchKeyEvent(event);
    }





}
