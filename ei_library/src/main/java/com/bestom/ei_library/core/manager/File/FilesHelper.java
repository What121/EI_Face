package com.bestom.ei_library.core.manager.File;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class FilesHelper {
    private static final String TAG = "FilesHelper";

    public boolean write(File file, String message) throws IOException {
        if (file.exists()) {
            FileWriter fwrite = new FileWriter(file);
            fwrite.write(message);
            fwrite.close();

//            FileOutputStream fout = new FileOutputStream(file);
//            byte[] bytes = message.getBytes();
//            fout.write(bytes);
//            fout.close();

//            if (file.canWrite()) {
//                FileOutputStream fout = new FileOutputStream(file);
//                byte[] bytes = message.getBytes();
//                fout.write(bytes);
//                fout.close();
//            } else {
//                Log.e(TAG, file.toString() + "can not write");
//                IOException io = new IOException();
//                throw io;
//            }
            return true;
        }else {
            return false;
        }
    }

    public String read(File file){
        String result = null;
        if (file.exists()) {
            try {
                FileReader fread = new FileReader(file);
                BufferedReader buffer = new BufferedReader(fread);
                String str = null;
                StringBuilder sb = new StringBuilder();
                while ((str = buffer.readLine()) != null) {
                    sb.append(str);
                }
                result = sb.toString();
            } catch (IOException e) {
                Log.e(TAG, "IO Exception");
            }
        }
        return result;
    }


}
