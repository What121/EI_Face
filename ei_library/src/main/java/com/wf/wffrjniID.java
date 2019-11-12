
package com.wf;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class wffrjniID {
    static {
        System.loadLibrary("wffr");
        System.loadLibrary("wffrjniID");
    }

    public wffrjniID() {
    }

    private static native int VerifyLic(String path);

    public static native int initialize(String path, int spoofing);

    public static native int Release();

    private static native int[][] recognize(byte[] frameByteArray, int width, int height);

    private static native int[][] recognizeFromImageFile(String imageFileName);

    private static native int[][] recognizeFromJpegBuffer(byte[] jpegByteArray, int jpegByteArraySize);

    public static native int[][] recognizeDualcam(byte[] frameByteArrayColor, byte[] frameByteArrayIR, int width, int height);	// Recognize from dual camera - color+IR

    private static native float[] confidenceValues();

    private static native int enroll(byte[] frameByteArray, int width, int height);

    public static native int enrollFromImageFile(String imageFileName);

    private static native int enrollFromJpegBuffer(byte[] jpegByteArray, int jpegByteArraySize);

    public static native int GetSpoofingStatus();

    static Semaphore semaphore = new Semaphore(1);

    public static int frInitialized = 0;
    public static int recognizeTime = 100000;
    public static int currentState = 0;
    public static long startTime;
    public static int timeRemaining = 0;
    public static int finish_state = 0;
    static String assetPath = "";

    public static int getTimeLeft(){
        return timeRemaining;
    }


    public static int getFinish_state(){
        return finish_state;
    }


    public static String getAssetPath() {
        return assetPath;
    }

    public static void setAssetPath(String assetPath) {
        wffrjniID.assetPath = assetPath;
    }

    private static int state = 0;
    private static int[][] faceCoordinates;
    private static float[] confidence;


    public static float[] getConfidence() {
        return confidence;
    }

    public static void setConfidence(float[] confidence) {
        wffrjniID.confidence = confidence;
    }


    public static int[][] getFaceCoordinates() {
        return faceCoordinates;
    }

    public static void setFaceCoordinates(int[][] faceCoordinates) {
        wffrjniID.faceCoordinates = faceCoordinates;
    }

    public static int getState() {
        return state;
    }

    public static void setState(int state) {
        wffrjniID.state = state;
    }

    public static int tryEnrollandRecogWithImage(String assetPath,String path){

        int init = -1;
        try{
            semaphore.acquire();
            byte[] image_bytes = extractBytes(path);
            init = wffrjniID.initialize(assetPath,0);
            if (init != 0) {
                System.out.println("wffrjniID: Init Error: " + init);
                semaphore.release();
                return 3;
            }
            Log.i("SDSD","INIT RETVAL : "+init);
            for (int i = 0;i<10;i++){
//                init = wffrjniID.enrollFromImageFile(path);
                init = wffrjniID.enrollFromJpegBuffer(image_bytes,image_bytes.length);
                Log.i("SDSD","ENROLL RETVAL : "+init);

                for (int j =0;j<10;j++){
//                    faceCoordinates = wffrjniID.recognizeFromImageFile(path);
                    faceCoordinates = wffrjniID.recognizeFromJpegBuffer(image_bytes,image_bytes.length);
                    Log.i("SDSD","RECOGNIZE RETVAL : "+faceCoordinates.length);
                }
            }


            wffrjniID.Release();
            semaphore.release();

        }catch (InterruptedException e){
            e.printStackTrace();
            wffrjniID.Release();
            semaphore.release();
        }
        catch (IOException e){
            e.printStackTrace();
            wffrjniID.Release();
            semaphore.release();
        }
        return init;
    }

    public static byte[] extractBytes (String ImagePath) throws IOException {
        // open image

        File file = new File(ImagePath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;

    }


    static byte[] image_bytes;
    public static int startExecution(Context context, byte[] cameraDataColor, byte[] cameraDataIR, int frameWidth, int frameHeight, String path)  {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (currentState == 1 && state == 2) {
            System.out.println("wffrjniID: Recognizing already running, stopping the current process and release resources.");
            System.out.println("wffrjniID: Release");
            wffrjniID.Release();
            frInitialized = 0;

        }
        if (currentState == 2 && state == 1) {
            System.out.println("wffrjniID: Enrolling already running, stopping the current process and release resources.");
            System.out.println("wffrjniID: Release");
            wffrjniID.Release();
            frInitialized = 0;
        }


        if (assetPath != null && !assetPath.equals("")) {
            if (state == 0) {
                if (frInitialized == 1) {
                    System.out.println("wffrjniID: Release");
                    wffrjniID.Release();
                    frInitialized = 0;
                }
            } else if (state == 1) {
                long timeMillis1 = System.currentTimeMillis();
                Log.i("SDSD","PRINT 1");
                if (frInitialized == 0) {
                    try{
                        Log.i("SDSD","PRINT 2");
                        image_bytes = extractBytes(path);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    Log.i("SDSD","PRINT 3");
                    int init = wffrjniID.initialize(assetPath, 1);
                    if (init != 0) {
                        Log.i("SDSD","PRINT 4");
                        System.out.println("wffrjniID: Init Error: " + init);
                        semaphore.release();
                        return 3;
                    }
                    Log.i("SDSD","PRINT 5");
                    if (init==0){
                        Log.i("SDSD","PRINT 6");
                        Log.i("SDSD","INITAILIZE SUCCESS");
                    }

                    Log.i("SDSD","PRINT 7");
                    frInitialized = 1;
                    startTime = timeMillis1;
                    long start = System.currentTimeMillis();
//                    init = wffrjniID.enrollFromImageFile(path);
                    Log.i("SDSD","PRINT 8");
                    init = wffrjniID.enrollFromJpegBuffer(image_bytes,image_bytes.length);
                    long end = System.currentTimeMillis();
                    Log.i("SDSD","PRINT 9");
                    Log.i("SDSD","ENROLL TIME "+(end-start)+" ms");
                    if (init!=0){
                        System.out.println("wffrjniID: ENROLL Error: " + init);
                        semaphore.release();
                        Toast.makeText(context,"Failed to Enroll Image",Toast.LENGTH_SHORT).show();
                        return 3;
                    }

                }
                Log.i("SDSD","PRINT 10");

                faceCoordinates = wffrjniID.recognizeDualcam(cameraDataColor, cameraDataIR, frameWidth, frameHeight);
                Log.i("SDSD","FACE CORDINATES : "+faceCoordinates.length);
                Log.i("SDSD","TIME REMAINING : "+timeRemaining);
                confidence = wffrjniID.confidenceValues();
                long elapsedTime1 = timeMillis1 - startTime;
                if (elapsedTime1 > (long) recognizeTime) {
                    System.out.println("wffrjniID: Release");
                    wffrjniID.Release();
                    frInitialized = 0;
                    state = 0;
                    timeRemaining = 0;
                    finish_state = -1;
                    Log.i("SDSD","PRINT 11");
                } else {
                    Log.i("SDSD","PRINT 12");
                    timeRemaining = (recognizeTime / 1000 - (int) elapsedTime1 / 1000);
                }
            }
            currentState = state;
            Log.i("SDSD","PRINT 13");
            semaphore.release();
            Log.i("SDSD","PRINT 14");
            return 0;
        } else {
            semaphore.release();
            return 1;
        }
    }
    public static void releaseEngine(){
        if (state!=0 && frInitialized==1){
            wffrjniID.Release();
            state = 0;
            frInitialized = 0;
        }
    }

}
