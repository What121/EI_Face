package com.bestom.ei_library.core.manager.Shell;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class ShellManager {
    Process process = null;

    private final String TAG=this.getClass().getSimpleName();


    public ShellManager() {
    }

    public boolean MustComd(String cmd) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader resultStream = null;
        try {
            // 申请su权限
            process = Runtime.getRuntime().exec("su");

            //region 处理process.getInputStream()的线程
            new Thread()
            {
                @Override
                public void run()
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;

                    try
                    {
                        while((line = in.readLine()) != null)
                        {
                            System.out.println("output: " + line);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
            //endregion


            //region 处理process.getErrorStream()的线程
            new Thread()
            {
                @Override
                public void run()
                {
                    BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line = null;

                    try
                    {
                        while((line = err.readLine()) != null)
                        {
                            System.out.println("err: " + line);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            err.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
            //endregion

            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = cmd+"\n";
            Log.i("command",command);
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();

            //会造成死锁
            //process.waitFor();

        } catch (Exception e) {
//            Log.e(TAG, e.getMessage(), e);
            Log.e(TAG,e.getMessage());
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
            } catch (IOException e) {
//                Log.e("TAG", e.getMessage(), e);
                Log.e(TAG,e.getMessage());
            }
        }
        return true;
    }

    public String StingComd(String cmd) {
        String line = null,error=null;
        DataOutputStream dataOutputStream = null;
        BufferedReader resultStream = null;
        BufferedReader errStream=null;
        String command = cmd+"\n";
        Log.i("command",command);
        try {
            // 申请su权限
            process = Runtime.getRuntime().exec(command);

            resultStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while (((line = resultStream.readLine())!=null)||((error = errStream.readLine())!=null)){
                break;
            }
            if (line!=null)
                System.out.println("output: " + line);
            if (error!=null)
                System.out.println("err: " + error);

            process.waitFor();

        } catch (Exception e) {
//            Log.e(TAG, e.getMessage(), e);
            Log.e(TAG,e.getMessage());
        } finally {
            try {
                if (dataOutputStream != null)
                    dataOutputStream.close();
                if (resultStream!=null)
                    resultStream.close();
                if (errStream!=null)
                    errStream.close();
                process.destroy();
            } catch (IOException e) {
//                Log.e(TAG, e.getMessage(), e);
                Log.e(TAG,e.getMessage());
            }
        }
        if (error!=null)
            return  error;
        return  line;
    }


}
