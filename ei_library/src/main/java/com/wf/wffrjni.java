
package com.wf;

public class wffrjni {


    static {
        System.loadLibrary("wffr");
        System.loadLibrary("wffrjni");

    }

    public wffrjni() {
    }

    public static native int SetVerbose(String path, int verbose);

    public static native int setAndroidVerbose(int verbose); 	// Set 0 to disable all prints, set 1 to enable prints

    public static native int EnableImageSaveForDebugging(int enableSaving); // Set 1 to enable image saving in Basepath location for debugging. Set 2 to save images with detected faces

    public static native String GetVersionInfo();

    public static native int VerifyLic(String path);

    public static native int setdbbasepath(String path); //optional API to set base path for database "wffrdb". It should be set before initialize() API to take effect

    public static native int initialize(String path, int width, int height, int widthStep, int frmode, int spoofing);

    public static native int addRecord(String name, String lastName);

    public static native int getLastAddedRecord();	// Return recordID of last added record in DB using API addRecord. If it is -1 then there was no record added.

    public static native int RenamePersonByName(String firstName, String secondName, String newfirstName, String newsecondName); 	// rename person in database.

    public static native Object[] getDbNameList();

    public static native int[] getDbRecordList(int size);

    public static native int DeleteDatabase();

    public static native int DeletePersonFromDb(int recordID);

    public static native int DeletePersonByNameFromDb(String firstname, String lastname);

    public static native int DeletePersonByFirstNameFromDb(String firstname);

    public static native int ExtractPersonByName(String firstname, String lastname);				// Extract a person from "wffrdb" by name to extraction folder "wffrdbExtract".

    public static native int SetSpoofingSensitivity(int senstivity);

    public static native int GetSpoofingSensitivity();

    public static native int GetSpoofingStatus();

    public static native int Release();

    public static native int[][] recognize(byte[] frameByteArray, int width, int height);

    public static native int[][] detectRecognizeMultiThread(byte[] frameByteArray, int width, int height);			// Detection and recognition in parallel threads.

    public static native int[][] detectRecognizeSingleCamSpoofMultiThread(byte[] frameByteArray, int width, int height);	// Detection and recognition in parallel threads with singlecam spoof.

    public static native int SetLastRecImageFormatMultiThread(int imageformat);				// Set format of last recognized image for detectRecognizeMultiThread. 0 for Gray, 1 for NV21
    public static native byte[] GetLastRecImageMultiThread(int width, int height, int imageFormat);	// Get last recognized image buffer for detectRecognizeMultiThread.
    public static native int[][] GetLastRecResultsMultiThread();					// Get coordinates of face for last recognized image for detectRecognizeMultiThread.

    public static native int[][] detectRecognizeQueue(byte[] frameByteArray, int width, int height);	// Detection and recognition in queue mode, FR will be processed in parallel thread in queue
    public static native int getQueueLength();								// Get current queue length

    public static native int[][] recognizeFromImageFile(String imageFileName);

    public static native float[] confidenceValues();

    public static native String[] nameValues();

    public static native float[] faceValues();

    public static native int[] faceTrackIDs();								// Output Tracking ID of faces based on spatial movement over time.

    public static native float GetRecognitionThreshold();

    public static native int SetRecognitionThreshold(float threshold);

    public static native int[][] enroll(byte[] frameByteArray, int width, int height);			// Enroll in video mode, input is NV21

    public static native int[][] enrollPictureMode(byte[] frameByteArray, int width, int height);	// Enroll in picture mode, input is NV21

    public static native int[][] enrollFromImageFile(String imageFileName);				// Enroll from image file.

    public static native int[][] enrollFromJpegBuffer(byte[] jpegByteArray, int jpegByteSize);		// Enroll from image jpeg buffer.

    public static native int[][] VerifyFrameForEnroll(byte[] frameByteArray, int width, int height);		// verify if the image is suitable for enrollment using frame pixel buffer
    public static native int[][] VerifyImageForEnrollJpegBuffer(byte[] jpegByteArray, int jpegByteSize);	// verify if the image is suitable for enrollment using jpeg buffer

    public static native int[][] recognizeSingleCamSpoof(byte[] frameByteArray, int width, int height);		// Recognize from single cam with stable face spoof

    public static native int[][] enrollSingleCamSpoof(byte[] frameByteArray, int width, int height);		// Enroll from single cam with stable face spoof
	
    public static native int[][] recognizeDualcam(byte[] frameByteArrayColor, byte[] frameByteArrayIR, int width, int height);	// Recognize from dual camera - color+IR

    public static native int[][] detectRecognizeDualCamSpoofMultiThread(byte[] frameByteArrayColor, byte[] frameByteArrayIR, int width, int height); //Det and Rec in parallel threads with dualcam spoof.

    public static native int[][] enrollDualcam(byte[] frameByteArrayColor, byte[] frameByteArrayIR, int width, int height);	// Enroll from

    public static native int[][] detectHelmet(byte[] frameByteArray, int width, int height);		// Detect if person is wearning helmet. If helmet detected output confidence is 0, -1 otherwise
    public static native int GetHelmetDetected();							// Returns 1 if helmet was detected in last API call of detectHelmet, 0 otherwise
    public static native int SetHelmetThreshold(float thresh);						// Set helmet detection threshold. Default is 0. Range is [-10, 10]. 
    public static native float GetHelmetThreshold();							// Get helmet detection threshold. Decrease threshold to increase helmet detection sensitivity

    public static native int[][] detectObjects(byte[] frameByteArray, int width, int height);

    public static native int GetMinFaceDetectionSizePercent();
    public static native int SetMinFaceDetectionSizePercent(int minFaceSize);	// Should be set before initialize() API is called

    public static native int GetDetectionOnlyMode();
    public static native int SetDetectionOnlyMode(int runDetectionOnly);	// Set to run on detection and turn off recognition

    public static native int GetDetectionAlgoType();
    public static native int SetDetectionAlgoType(int algotype);		// Set to to 0 for faster detection, set to 1 for more accurate but slower detection

    public static native int GetNumProcessingCores();				// Get number of processing core. Get should be called after Initialize()
    public static native int SetNumProcessingCores(int numcores);		// Set number of processing core. Set should be called before first initialize() API call 

    public static native int GetEnrollSaveImageIsColor();
    public static native int SetEnrollSaveImageIsColor(int isColor);		// Set saved enrolled image to color, set 1 for color and 0 for gray. 

    public static native float GetSingleCamSpoofThreshold();
    public static native int SetSingleCamSpoofThreshold(float spoofThresh);	//Set single cam spoof threshold. Default is -5.0. Range is [-30.0f, 30.0]. Decreasing threshold will increase sensitivity

    public static native int GetSingleCamSpoofRecoveryTime();
    public static native int SetSingleCamSpoofRecoveryTime(int recoveryTime);	// Set recovery time in seconds after spoof attack is detected. Default is 3. Range is [1,3]

    public static native int GetAntiSpoofBlockingFlag();			// return 0 if disabled, 1 if enabled
    public static native int SetAntiSpoofBlockingFlag(int enableBlocking);	// 1 to enable and 0 to disable. When enabled, FR will be disabled for few seconds if spoof attack is detected.

    public static native int GetSinglecamBGReject();			  	// return 0 if disabled, 1 if enabled
    public static native int SetSinglecamBGReject(int enableBlocking);		// 1 to enable and 0 to disable. When enabled, BG will be used in anti-spoof for Single cam.

    public static native int GetDualcamBGReject();
    public static native int SetDualcamBGReject(int enableBlocking);		// 1 to enable and 0 to disable. When enabled, BG will be used in anti-spoof for Dual cam.

    public static native int getSaveDetectedFaceFlag();
    public static native int setSaveDetectedFaceFlag(int saveDetectedFaces, String outpath);	// Set to 1 to save tightly cropped faces, 2 to save face with 20% border. Set outpath for saving folder

    public static native int SetUpdateFromPCDB(int enablePCDB, int maxPCDBCount);
    public static native int GetUpdateFromPCDB();

    public static native int SetDeleteExistingNamePCDBUpdate(int enableDelete);		// Enable/Disable deletion of existing ID's with same name in wffrdb DB when running PCDB update from wffrdbpc.
    public static native int GetDeleteExistingNamePCDBUpdate();

    public static native int SetDeleteExistingNameInEnrolling(int enableDelete);	// Enable / Disable deletion of existing ID's with same name in wffrdb for enrolling on device
    public static native int GetDeleteExistingNameInEnrolling();

    public static native int SetEnrollQualityCheckFlag(int enableCheck);		// Enable / Disable face quality check for enrolling like face angle should be frontal and not blurred.
    public static native int GetEnrollQualityCheckFlag();

    public static native int GetEnrollFailureCode();					// Enroll failure code. 0 - pass, 1 - not detected, 2 -multiface, 3 - spooffail, 4 - lowcontrast, 
					   						//5 - faceangle, 6 - blur, 7 - lowlight, 8 - blur, 9 - similarface in db, 10 - multienroll, 
											// 11 - re-enroll blocked, 12 - smallface

    public static native int SetEnrollQualityParameters(float minLeftRightYawRatio, float minUpDownPitchRatio, float maxInPlaneRoll, float maxBlur, float minContrast, float minFaceAvg);
    public static native float[] GetEnrollQualityParameters();				// Return 6 value of face enroll quality parameters in order of 
											// 0: minLeftRightYawRatio, 1: minUpDownPitchRatio, 2: maxInPlaneRoll, 3: maxBlur, 4: minContrast, 5: minFaceAvg

    public static native int SetRecogQualityParameters(float minLeftRightYawRatio, float minUpDownPitchRatio, float maxBlur);
    public static native float[] GetRecogQualityParameters();				// Return 3 value of face recognition quality parameters in order of 
											// 0: minLeftRightYawRatio, 1: minUpDownPitchRatio, 2: maxBlur


    public static native int GetEnrollMultiCheck();
    public static native int SetEnrollMultiCheck(int enableCheck);		/* check if multiple person are present in enrolling. 0-disable, 1-enable: ignore frame with multiple faces, 
												2-strong enable: discard enrollment if multiface in even single frame
												3-stronger enable: discard enrollment if different persons face found */


    public static native int GetPersonReEnrollCheck();
    public static native int SetPersonReEnrollCheck(int enableCheck);		/* To disable re-enroll by same person. Set to 1 to disable re-enroll by same person. Set 1 to allow re-enroll*/
    public static native float GetReEnrollMatchThreshold();
    public static native int SetReEnrollMatchThreshold(float threshold);	/* Set matching threshold with existing database to block re-enroll.*/
	

    public static native int SetRecogQualityCheckFlag(int enableCheck);		// Enable / Disable face quality check for recognition like face angle should be frontal and not blurred.
    public static native int GetRecogQualityCheckFlag();

    public static native int SetOnlineLicensing(int enable);			// Enable / Disable online license generation automatically on initialize API.
    public static native int GetOnlineLicensingFlag();

    public static native String getSaveDetectedImageName();	// Returns the name of the detected face image stored on disk. It should be called after recognize API.

    public static native String getDetectedWfgName();		// Returns the name of the detected face WFG image stored on disk. It should be called after recognize API.

    public static native int getSaveEnrollImagesStatus();

    public static native int saveEnrollImages(int enableSaving);

    public static native byte[] rotateImage(byte[] frameByteArray, int width, int height, int imageFormat, int rotAngle);		//Rotate input image by 90, 180 or 270 degrees. 
																	//imageFormat: 0-Gray, 1-NV21/NV12, 2-YV12

    public static native byte[] rotateImageAndMirror(byte[] frameByteArray, int width, int height, int imageFormat, int rotAngle);	//Rotate input image by 90, 180 or 270 degrees followed by mirror image about Y axis (left-right flip). imageFormat: 0-Gray, 1-NV21/NV12, 2-YV12

    public static native byte[] swapImagePixels(byte[] frameByteArray, int width, int height);		// Swap pixels in Y channel like input pixels {y1,y2,y3,y4,y5,y6...} to {y2,y1,y4,y3,y6,y5...}

    public static native byte[] resizeImage(byte[] frameByteArray, int inpwidth, int inpheight, int outwidth, int outheight);	

    public static native int lowLightDetection(byte[] frameByteArray, int width, int height, int lightStrength);	// Detect low light condition in image. External light can be illumiated in case of low lights. lightStrength Range [0,10], Default = 5.

    /////////////////////////////// PC / Server Enrolling API's ////////////////////////////
    public static native int GetVersionInfoPC();
    public static native int initializeEnrollPC(String path, String firstname, String lastname, int spoofing);
    public static native int ReleaseEnrollPC();
    public static native int[][] enrollPC(byte[] frameByteArray, int width, int height);
    public static native int[][] enrollSingleCamSpoofPC(byte[] frameByteArray, int width, int height);
    public static native float[] confidenceValuesEnrollPC();
    public static native int SetSpoofingSensitivityEnrollPC(int senstivity);
    public static native int GetSpoofingSensitivityEnrollPC();
    public static native int verifyEnrollFromFilePC(String imageFileName);
    public static native int SetEnrollQualityCheckFlagPC(int  enableCheck);		// Enable / Disable face quality check for enrolling like face angle should be frontal and not blurred.
    public static native int GetEnrollQualityCheckFlagPC();
    public static native int GetSpoofingStatusEnrollPC();
    //////////////////////////////// PC / Server Enrolling API's ////////////////////////////

    public static native int[][] getfp(String path, byte[] frameByteArray, int width, int height);
}
