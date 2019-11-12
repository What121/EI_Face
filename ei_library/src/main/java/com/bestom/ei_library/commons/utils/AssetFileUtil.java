package com.bestom.ei_library.commons.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetFileUtil {
    private static final String TAG = "AssetFileUtil";
    private String datapath=null;
    private String filepath=null;
    private String cachepath=null;
    private Context context;

    public AssetFileUtil(Context context) {
        this.context = context;
        if (filepath==null){
            filepath=context.getFilesDir().getPath();
        }
        if (cachepath==null){
            cachepath=context.getCacheDir().getPath();
        }
        if (datapath==null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                datapath=context.getDataDir().getPath();
            }else {
                datapath=filepath.substring(0,filepath.lastIndexOf(File.separator)) ;
            }
        }
        filepath=filepath+File.separator;
        datapath=datapath+File.separator;
        cachepath=cachepath+File.separator;

        Log.d(TAG, "AssetFileUtil: init paths\n"+"datapath:"+datapath+"\n filepath:"+filepath+"\n cachepath"+cachepath);
    }

    public boolean checkFilesExist(String filepath){
        File file = new File(filepath);
        if(!file.exists()){
            return false;
        }
        return true;
    }

    public boolean checkFilesExist(String[] filenames,int type){
        String path = null;
        switch (type){
            case 0:
                path=datapath;
                break;
            case 2:
                path=cachepath;
                break;
            case 1:
                path=filepath;
            default:
                break;
        }
        String filepath = null;
        for (String filename:filenames){
            if (filename.equals("db.dat") || filename.equals("db.dat.dat")) {
                filepath = getDualFilePath() + "/wffrdb"+filename;
            }else if (filename.indexOf(".bin")!=-1){
                filepath = getDualFilePath() + File.separator+filename;
            } else {
                filepath = path + filename;
            }
            if(!checkFilesExist(filepath)){
                return false;
            }
        }
        return true;
    }

    public void copyFilesFromAssets(String[] filenames,int type) {
        File file;
        InputStream inputStream;
        OutputStream outputStream;
        String path = null;
        switch (type){
            case 0:
                path=datapath;
                break;
            case 2:
                path=cachepath;
                break;
            case 1:
                path=filepath;
            default:
                break;
        }
        AssetManager assetManager = context.getAssets();
        String filepath = null;
        for (String filename : filenames) {
            if (filename.equals("db.dat") || filename.equals("db.dat.dat")) {
                filepath = getDualFilePath() + "/wffrdb"+filename;
            }else if (filename.indexOf(".bin")!=-1){
                filepath = getDualFilePath() + File.separator+filename;
            } else {
                if (path!=null)
                    filepath = path + filename;
            }
            file=new File(filepath);
            try {
                if (file.exists() && file.length() > 0) {
                    continue;
                } else {
                    file.createNewFile();
                    Log.d("JNI File", "Creating new file " + filepath);
                }
                inputStream = assetManager.open(filename);
                outputStream = new FileOutputStream(file);

                copyFile(inputStream, outputStream);

                inputStream.close();
                inputStream = null;

                outputStream.flush();
                outputStream.close();
                outputStream = null;

            } catch (Exception e) {
                Log.e("JNI ", "Error while copying filesAssets getAssets()");
                e.printStackTrace();
            }
        }
    }

    private void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[10 * 1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
    }



    public String getDualFilePath() {
        String DualFilePath = null;
        try {
//            String basePath = context.getFilesDir().getAbsolutePath();
            String basePath = this.filepath+"dualcam";
            DualFilePath = basePath;
            File dir = new File(basePath);
            if (!dir.isDirectory()) {
                dir.mkdir();
            }
            String databasePath = basePath + "/wffrdb";
            File databaseDir = new File(databasePath);
            if (!databaseDir.isDirectory()) {
                databaseDir.mkdir();
            }
            Log.d("JNI File Path", basePath);
        }
        catch (Exception e)
        {
            Log.e("JNI ", "Error while getting file path");
            e.printStackTrace();
            return null;
        }
        return DualFilePath;
    }
}
