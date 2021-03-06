package com.wf;

import android.util.Log;

import java.util.concurrent.Semaphore;


public class wffrdualcamapp {
    private static final String TAG = "wffrdualcamapp";
    
    private static final int AssetError = 1;
    private static final int RecordError = 2;
    private static final int InitializeError = 3;
    public static int frInitialized = 0;
    public static int enrollTime = 10000;
    public static long recognizeTime = 1000000000;
    public static int recognitionSpoofing = 1;
    public static int enrollSpoofing = 1;
    public static int currentState = 0;
    public static long startTime;
    public static long timeRemaining = 0;
    public static long t2;
    private static String assetPath = "";
    static int Process_Running_Error = 50;
    static Object[] DBnames;
    static int[] records;
    private static int state = 0;
    private static int[][] faceCoordinates;
    private static String[] names;
    private static float[] confidence;
    static Semaphore semaphore = new Semaphore(1);

    private static int finish_state = 1;


    public static int startExecution(byte[] cameraDataColor, byte[] cameraDataIR, int frameWidth, int frameHeight, String name)  {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (currentState == 1 && state == 2) {
            System.out.println("WFFRJNI: Recognizing already running, stopping the current process and release resources.");
            System.out.println("WFFRJNI: Release");
            wffrjni.Release();
            frInitialized = 0;

        }
        if (currentState == 2 && state == 1) {
            System.out.println("WFFRJNI: Enrolling already running, stopping the current process and release resources.");
            System.out.println("WFFRJNI: Release");
            wffrjni.Release();
            frInitialized = 0;
        }

        if (assetPath != null && !assetPath.equals("")) {
            //region state=0
            if (state == 0) {
                if (frInitialized == 1) {
                    System.out.println("WFFRJNI: Release");
                    wffrjni.Release();
                    frInitialized = 0;
                }
            }
            //endregion
            //region state=1
            else if (state == 1) {
                long timeMillis1 = System.currentTimeMillis();
                if (frInitialized == 0) {
                    int init = wffrjni.initialize(assetPath, frameWidth, frameHeight, frameWidth, 0, recognitionSpoofing);
                    if (init != 0) {
                        System.out.println("WFFRJNI: Init Recognize Error: " + init);
                        semaphore.release();
                        return -3;
                    }

                    System.out.println("WFFRJNI: Recognize Init");
                    frInitialized = 1;
                    startTime = timeMillis1;
                }
                long t1 = System.currentTimeMillis();
                faceCoordinates = wffrjni.recognizeDualcam(cameraDataColor, cameraDataIR, frameWidth, frameHeight);
                t2 = System.currentTimeMillis() - t1;
                names = wffrjni.nameValues();
                confidence = wffrjni.confidenceValues();
                long elapsedTime1 = timeMillis1 - startTime;
//                System.out.println("WFFRJNI: Rec Time: " + elapsedTime1 + " Process Time " + t1 + "ms"  );
                if (elapsedTime1 > (long) recognizeTime) {
                    System.out.println("WFFRJNI: Release");
                    wffrjni.Release();
                    finish_state = -1;
                    frInitialized = 0;
                    state = 0;
                    timeRemaining = 0;
                } else {
                    timeRemaining = (recognizeTime / 1000 - (int) elapsedTime1 / 1000);
                }
            }
            //endregion
            //region state=2
            else if (state == 2) {
                String no = "";
                if (frInitialized == 0) {
                    int init = wffrjni.initialize(assetPath, frameWidth, frameHeight, frameWidth, 1, enrollSpoofing);
                    if (init != 0) {
                        System.out.println("WFFRJNI: Init Error: " + init);
                        Log.e(TAG, "startExecution: WFFRJNI: Init Error: " + init );
                        semaphore.release();
                        return -3;
                    }

                    System.out.println("WFFRJNI: Enroll Init");
                    Log.d(TAG, "startExecution: WFFRJNI: Enroll Init" );
                    if (name != null ) {
                        name = name.trim();
                    }
                    int addRec = wffrjni.addRecord(name, no);
                    if (addRec != 0) {
                        System.out.println("WFFRJNI: Adding Record Error: " + addRec);
                        Log.e(TAG, "startExecution: WFFRJNI: Adding Record Error: " + addRec );
                        semaphore.release();
                        return -2;
                    }
                    Log.d(TAG, "startExecution: WFFRJNI: wffrjni.addRecord finish" );
                    frInitialized = 1;
                    startTime = System.currentTimeMillis();
                }

                //坐标系
//                faceCoordinates = wffrjni.enrollDualcam(cameraDataColor, cameraDataIR, frameWidth, frameHeight);
                faceCoordinates = wffrjni.enroll(cameraDataColor,frameWidth,frameHeight);
                Log.d(TAG, " wffrjni.enrollDualcam finish" );
                for (int i=0;i<faceCoordinates.length;i++){
                    for (int j=0;j<faceCoordinates[0].length;j++){
                        Log.d(TAG, "注册返回值 faceCoordinate["+i+"]["+j+"]="+faceCoordinates[i][j]+"\n");
                    }
                }
                //姓名
                names = wffrjni.nameValues();
                Log.d(TAG, " wffrjni.nameValues finish" );
                //相识度 为0 适合录入，-1 不适合录入
                confidence = wffrjni.confidenceValues();
                Log.d(TAG, " wffrjni.confidenceValues finish"+ confidence);
                long currentSeconds = System.currentTimeMillis();
                long elapsedTime = currentSeconds - startTime;
                System.out.println("WFFRJNI: Enroll Time: " + elapsedTime);
                if (elapsedTime > (long) enrollTime) {
                    System.out.println("WFFRJNI: Release");
                    timeRemaining = 0;
                    wffrjni.Release();
                    frInitialized = 0;
                    state = 0;
                } else {
                    timeRemaining = (enrollTime / 1000) - (int) (elapsedTime / 1000);
                }
            }
            //endregion

            currentState = state;
            semaphore.release();
            if (state==2&&confidence!=null){
                if (confidence.length>0){
                    return (int)confidence[0];
                }
            }

            return 0;
        } else {
            semaphore.release();
            return -4;
        }
    }

    public static int startExecutionFast(byte[] cameraDataColor, byte[] cameraDataIR, int frameWidth, int frameHeight, String name)  {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (currentState == 1 && state == 2) {
            System.out.println("WFFRJNI: Recognizing already running, stopping the current process and release resources.");
            System.out.println("WFFRJNI: Release");
            wffrjni.Release();
            frInitialized = 0;

        }
        if (currentState == 2 && state == 1) {
            System.out.println("WFFRJNI: Enrolling already running, stopping the current process and release resources.");
            System.out.println("WFFRJNI: Release");
            wffrjni.Release();
            frInitialized = 0;
        }

        if (assetPath != null && !assetPath.equals("")) {
            //region state=0
            if (state == 0) {
                if (frInitialized == 1) {
                    System.out.println("WFFRJNI: Release");
                    wffrjni.Release();
                    frInitialized = 0;
                }
            }
            //endregion
            //region state=1
            else if (state == 1) {
                long timeMillis1 = System.currentTimeMillis();
                if (frInitialized == 0) {
                    int init = wffrjni.initialize(assetPath, frameWidth, frameHeight, frameWidth, 0, recognitionSpoofing);
                    if (init != 0) {
                        System.out.println("WFFRJNI: Init Recognize Error: " + init);
                        semaphore.release();
                        return -3;
                    }

                    System.out.println("WFFRJNI: Recognize Init");
                    frInitialized = 1;
                    startTime = timeMillis1;
                }
                long t1 = System.currentTimeMillis();
                faceCoordinates = wffrjni.detectRecognizeDualCamSpoofMultiThread(cameraDataColor, cameraDataIR, frameWidth, frameHeight);
                t2 = System.currentTimeMillis() - t1;
                names = wffrjni.nameValues();
                confidence = wffrjni.confidenceValues();
                long elapsedTime1 = timeMillis1 - startTime;
//                Log.e("**Time**", "\nRec Time::"+elapsedTime1+"\nProcess Time:"+t1+"ms" );
//                System.out.println("WFFRJNI: Rec Time: " + elapsedTime1 + " Process Time " + t1 + "ms"  );
                if (elapsedTime1 > (long) recognizeTime) {
                    System.out.println("WFFRJNI: Release");
                    wffrjni.Release();
                    finish_state = -1;
                    frInitialized = 0;
                    state = 0;
                    timeRemaining = 0;
                } else {
                    timeRemaining = (recognizeTime / 1000 - (int) elapsedTime1 / 1000);
                }
            }
            //endregion

            currentState = state;
            semaphore.release();
            if (state==2&&confidence!=null){
                if (confidence.length>0){
                    return (int)confidence[0];
                }
            }

            return 0;
        } else {
            semaphore.release();
            return -4;
        }
    }


    public static int runEnrollFromJpegFile(String imageFileName, String name) {
        try {
            semaphore.acquire();
            int recordID;
            if (currentState > 0 && frInitialized == 1) {
                System.out.println("WFFRJNI: Video mode already running, stopping the current process and release resources.");
                System.out.println("WFFRJNI: Release");
                wffrjni.Release();
                frInitialized = 0;
                currentState = 0;
                state = 0;
            }

            if (assetPath != null && !assetPath.equals("")) {

                int init = wffrjni.initialize(assetPath, 0, 0, 0, 1, 0);
                if (init != 0) {
                    System.out.println("WFFRJNI: Init Error: " + init);
                    semaphore.release();
                    return -3;
                }
                String lastName = "";
                System.out.println("WFFRJNI: Enroll Init");
                if (name != null && name.contains(" ")) {
                    lastName = name.substring(name.lastIndexOf(' '));
                    name = name.substring(0, name.lastIndexOf(' '));

                }

                int addRec = wffrjni.addRecord(name, lastName);
                if (addRec != 0) {
                    System.out.println("WFFRJNI: Adding Record Error: " + addRec);
                    semaphore.release();
                    return -2;
                }
                faceCoordinates = wffrjni.enrollFromImageFile(imageFileName);
                names = wffrjni.nameValues();
                confidence = wffrjni.confidenceValues();
                recordID=wffrjni.getLastAddedRecord();
                wffrjni.Release();

                semaphore.release();
                return recordID;
            } else {
                semaphore.release();
                return -1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -4;
        }


    }

    /** Force stop recognition/enroll process and release engine instance**/
    public static int stopExecution() {
        try {
            semaphore.acquire();
            if (frInitialized == 1)
            {
                wffrjni.Release();
                frInitialized = 0;
                state = 0;
            }

            semaphore.release();
            return 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static int updateFromPCDB() {
        try {
            semaphore.acquire();
            if (currentState > 0 && frInitialized == 1) {
                System.out.println("WFFRJNI: Video mode already running, stopping the current process and release resources.");
                System.out.println("WFFRJNI: Release");
                wffrjni.Release();
                frInitialized = 0;
                currentState = 0;
                state = 0;
            }

            if (assetPath != null && !assetPath.equals("")) {
                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
                if (init != 0) {
                    System.out.println("WFFRJNI: Init Recognize Error: " + init);
                    semaphore.release();
                    return 3;
                }

                wffrjni.Release();

                semaphore.release();
                return 0;
            } else {
                semaphore.release();
                return 1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static void releaseEngine(){
        wffrjni.Release();
        state = 0;
        frInitialized = 0;
    }


    public static int getDatabase() {
       try {
            	semaphore.acquire();

	       if ((getState() == 0) && (frInitialized == 0)) {
		    int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
		    if (init != 0) {
		        System.out.println("WFFRJNI: Init DB Error: " + init);
			semaphore.release();
		        return 3;
		    }

		    System.out.println("WFFRJNI: DB Init");

		    DBnames = wffrjni.getDbNameList();

		    if (getDatabaseNames() != null)
		        records = wffrjni.getDbRecordList(getDatabaseNames().length);

		    wffrjni.Release();
		    state = 0;
		    frInitialized = 0;

	      	    semaphore.release();
		    return 0;
		} else {
	      	    semaphore.release();
		    return Process_Running_Error;
		}
        } catch (InterruptedException e) {
            e.printStackTrace();
	    return 1;
        }       
    }

    public static void setFinishState(int val) {
        finish_state = val ;
    }

    public static int getFinishState() {
        return finish_state;
    }

    public static int getState() {
        return state;
    }

    public static void setState(int value)  {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        state = value;

        semaphore.release();

    }

    public static void setAssetPath(String path) {
        assetPath = path;
    }

    public static void VerifyLic(String path){
        wffrjni.VerifyLic(path);
    }

    public static void SetOnlineLicensing(int enable){
        wffrjni.SetOnlineLicensing(enable);
    }

    public static int GetOnlineLicensingFlag(){
       return   wffrjni.GetOnlineLicensingFlag();
    }

    public static String getAssetPath() {
        return assetPath;
    }

    public static long getTimeLeft() {
        return timeRemaining;
    }

    public static int[][] getFaceCoordinates() {
        return faceCoordinates;
    }

    public static String[] getNames() {
        return names;
    }

    public static float[] getConfidence() {
        return confidence;
    }

    public static Object[] getDatabaseNames() {
        return DBnames;
    }

    public static int[] getDatabaseRecords() {
        return records;
    }

    public static int deletePerson(int recordID) {

       try {
            	semaphore.acquire();

		if ((getState() == 0) && (frInitialized == 0)) {
		    int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
		    System.out.println("Init: " + init);
		    if (init != 0) {
		        System.out.println("WFFRJNI: Init DB Error: " + init);
			semaphore.release();
		        return 3;
		    }
		    int val = wffrjni.DeletePersonFromDb(recordID);
		    System.out.println("Val: " + val);
		    wffrjni.Release();
		    state = 0;
		    frInitialized = 0;

		    semaphore.release();
		    return val;
		} else {
		    semaphore.release();
		    return Process_Running_Error;
		}
        } catch (InterruptedException e) {
            e.printStackTrace();
	    return 1;
        }
    }

    public static int deletePersonbyName(String name) {

       try {
         	semaphore.acquire();
		if ((getState() == 0) && (frInitialized == 0)) {
		    int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
		    System.out.println("Init: " + init);
		    if (init != 0) {
		        System.out.println("WFFRJNI: Init DB Error: " + init);
			semaphore.release();
		        return 3;
		    }
		    String firstname = name;
		    String lastname = "";
		    System.out.println("WFFRJNI: Enroll Init");
		    if (name != null && name.contains(" ")) {
		        lastname = name.substring(name.lastIndexOf(' '));
		        firstname = name.substring(0, name.lastIndexOf(' '));
		    }
		    int val = wffrjni.DeletePersonByNameFromDb(firstname,lastname);
		    System.out.println("Val: " + val);
		    wffrjni.Release();
		    state = 0;
		    frInitialized = 0;

		    semaphore.release();
		    return val;
		} else {
		    semaphore.release();
		    return Process_Running_Error;
		}
        } catch (InterruptedException e) {
            e.printStackTrace();
	    return 1;
        }
    }

    public static int deleteDatabase() {
       try {
            	semaphore.acquire();
		if ((getState() == 0) && (frInitialized == 0)) {
		    int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
		    if (init != 0) {
		        System.out.println("WFFRJNI: Init DB Error: " + init);
			semaphore.release();
		        return 3;
		    }
		    int val = wffrjni.DeleteDatabase();

		    wffrjni.Release();
		    state = 0;
		    frInitialized = 0;

		    semaphore.release();
		    return val;
		} else {

		    semaphore.release();
		    return Process_Running_Error;
        	}
        } catch (InterruptedException e) {
            e.printStackTrace();
	    return 1;
        }
    }


}
