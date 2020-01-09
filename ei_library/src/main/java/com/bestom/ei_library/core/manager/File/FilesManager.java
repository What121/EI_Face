package com.bestom.ei_library.core.manager.File;

import java.io.File;
import java.io.IOException;

public class FilesManager extends FilesHelper{
	private static final String TAG = "FilesManager";

	private final String _485path="/sys/bst485/val";
	private final String ledpath="/sys/bstled/val";
	private final String relaypath="/sys/bstrelayer/val";
	private final String powerpath="/sys/bstrpower/val";
	private File ledFile,relayFile,powerFile,_485File;

	private static volatile FilesManager instance;

	private FilesManager() {
		ledFile=new File(ledpath);
		relayFile=new File(relaypath);
		powerFile=new File(powerpath);
		_485File=new File(_485path);
	}

	public static FilesManager getInstance(){
		synchronized (FilesManager.class){
			if (instance==null){
				instance=new FilesManager();
			}
		}
		return instance;
	}

	public boolean write485(String value){
		try {
			return write(_485File, value);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean writeLed(String value){
		try {
			return write(ledFile, value);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean writeRelay(String value){
		try {
			return write(relayFile, value);
		} catch (IOException e) {
			e.printStackTrace();
			return  false;
		}
	}

	public String readPower(){
		return read(powerFile);
	}



}
