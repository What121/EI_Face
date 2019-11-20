package com.bestom.ota;

import android.content.Context;
import android.os.RecoverySystem;
import android.os.SystemProperties;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class OTACore {
    private static final String TAG = "OTACore";

    static {
        System.loadLibrary("OTA");
    }

    public static void installPackage(Context context, File packageFile)throws IOException {
        RecoverySystem.installPackage(context, packageFile);
    }

    public static boolean checkRKimage(String path){
        String imageProductName = getImageProductName(path);
        Log.d(TAG, "checkRKimage() : imageProductName = " + imageProductName);
        if(imageProductName == null) {
            return false;
        }

        if(imageProductName.trim().equals(getProductName())){
            return true;
        }else {
            return false;
        }
    }

    public static String getCurrentFirmwareVersion() {
        return SystemProperties.get("ro.firmware.version");
    }

    public static String getProductName() {
        return SystemProperties.get("ro.product.model");
    }

    public static String getSystemVersion() {
        String version = SystemProperties.get("ro.product.version");
        if(version == null || version.length() == 0) {
            version = "1.0.0";
        }

        return version;
    }

    public static String getProductSN() {
        String sn = SystemProperties.get("ro.serialno");
        if(sn == null || sn.length() == 0) {
            sn = "unknown";
        }

        return sn;
    }

    private String getOtaPackageFileName() {
        String str = SystemProperties.get("ro.ota.packagename");
        if(str == null || str.length() == 0) {
            return null;
        }
        if(!str.endsWith(".zip")) {
            return str + ".zip";
        }

        return str;
    }

    private String getRKimageFileName() {
        String str = SystemProperties.get("ro.rkimage.name");
        if(str == null || str.length() == 0) {
            return null;
        }
        if(!str.endsWith(".img")) {
            return str + ".img";
        }

        return str;
    }

    public static native String getImageVersion(String path);

    public static native String getImageProductName(String path);

}
