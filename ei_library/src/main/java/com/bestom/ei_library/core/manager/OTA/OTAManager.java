package com.bestom.ei_library.core.manager.OTA;


import android.content.Context;
import android.util.Log;

import com.bestom.ota.OTACore;

import java.io.File;
import java.io.IOException;

public class OTAManager {
    private static final String TAG = "OTAManager";

    private static Context context;
    private OTACore mOTACore;
    private static volatile OTAManager instance;

    private OTAManager(Context context) {
        this.context=context;
    }

    public static OTAManager getInstance(Context context){
        synchronized (OTAManager.class){
            if (instance==null){
                instance=new OTAManager(context);
            }
        }
        return instance;
    }

    public void installPackage(File packageFile){
        try {
            OTACore.installPackage(context,packageFile);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "installPackage error "+e.getMessage());
        }
    }

    public boolean checkRKimage(String path){
        return OTACore.checkRKimage(path);
    }

    public String getCurrentFirmwareVersion(){
        return OTACore.getCurrentFirmwareVersion();
    }

    public String getProductName(){
        return OTACore.getProductName();
    }

    public String getSystemVersion(){
        return OTACore.getSystemVersion();
    }

    public String getProductSN(){
        return OTACore.getProductSN();
    }

    public String getImageVersion(String path){
        return getImageVersion(path);
    }

    public String getImageProductName(String path){
        return getImageProductName(path);
    }

}
