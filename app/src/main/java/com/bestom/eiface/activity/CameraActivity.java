package com.bestom.eiface.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestom.ei_library.commons.constant.SerialCmdCode;
import com.bestom.ei_library.commons.constant.StatusCode;
import com.bestom.ei_library.commons.utils.PermissionsUtils;
import com.bestom.ei_library.core.service.Interface.Listener.RespSampleListener;
import com.bestom.eiface.Control.CameraController;
import com.bestom.eiface.Control.CameraViewController;
import com.bestom.eiface.MyApp;
import com.bestom.eiface.R;
import com.bestom.eiface.view.CameraDetectView;
import com.bestom.eiface.view.CameraView;
import com.wf.wffrdualcamapp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.bestom.eiface.MyApp.permissions;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CameraActivity";

    private Context mContext;
    private Activity mActivity;
    private Unbinder mUnbinder;

    @BindView(R.id.layout_frontcamera) RelativeLayout frontLayout;
    private CameraView frontCameraView;
    private CameraDetectView frontDetecView;
    @BindView(R.id.layout_backcamera) RelativeLayout backLayout;
    private CameraView backCameraView;
    private CameraDetectView backDetecView;
    @BindView(R.id.sysinfo_tv) TextView systemView;
    @BindView(R.id.upregister_tv) TextView upregisterView;
    @BindView(R.id.register_tv) TextView registerView;
    @BindView(R.id.setting_tv) TextView settingView;
    @BindView(R.id.userinfo_headimg) CircleImageView headImageView;
    @BindView(R.id.userinfo_name) TextView nameText;
    @BindView(R.id.userinfo_no) TextView noText;

    private PermissionsUtils.IPermissionsResult permissionsResult=new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
            initCameraView();
        }

        @Override
        public void forbitPermissons() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mContext=this;
        mActivity=this;
        mUnbinder= ButterKnife.bind(mActivity);

        //动态申请权限
        permissions = new String[]{Manifest.permission.CAMERA};
//        PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);

        initview();
    }

    private void  initview(){
        frontLayout.setVisibility(View.GONE);
        backLayout.setVisibility(View.VISIBLE);
        systemView.setOnClickListener(this);
        upregisterView.setOnClickListener(this);
        registerView.setOnClickListener(this);
        settingView.setOnClickListener(this);
    }

    private void initCameraView(){
        frontCameraView=findViewById(R.id.cameraView_front);
        frontDetecView=findViewById(R.id.cameraDetect_front);
        backCameraView=findViewById(R.id.cameraView_back);
        backDetecView=findViewById(R.id.cameraDetect_back);

        frontCameraView.setDrawActivity(this);
        frontCameraView.IRCamerainit();
        backCameraView.setDrawActivity(this);
        CameraViewController.getInstant().putFCameraView(frontCameraView);
        CameraViewController.getInstant().putBCameraView(backCameraView);
    }

    @Override
    protected void onResume() {
//        initCameraView();
        PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        drawOutput(null, CameraController.CAMERA_WIDTH, CameraController.CAMERA_HEIGHT, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sysinfo_tv:
                MyApp.mSerialApi.setStatus(SerialCmdCode.SERIAL_CMD_STATUS, true, new RespSampleListener<String>() {
                    @Override
                    public void onSuccess(int code, String s) {
                        if (code== StatusCode.SUCCESS.getCode()){
                            Log.d(TAG, "setStatus onSuccess code: "+code+" ,values is "+s);
                            //打开radar成功,获取数据
//                            checkScreenThread.start();
                        }else {
                            Log.d(TAG, "setStatus onSuccess code: "+code+" ,msg is "+s);
                        }
                    }

                    @Override
                    public void onFailure(int code, String errMsg) {
                        Log.e(TAG, "setStatus onFailure code: "+code+",errmsg is "+errMsg);
                    }
                });


                Toast.makeText(mContext,"系统信息待开通...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting_tv:
                Toast.makeText(mContext,"设置待开通...",Toast.LENGTH_SHORT).show();
                //初始化状态
                wffrdualcamapp.setState(0);
                Intent DBintent = new Intent(this, RegisterDBActivity.class);
                startActivity(DBintent);
                break;
            case R.id.upregister_tv:
                Toast.makeText(mContext,"上传注册待开通...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.register_tv:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                //Activity切换动画
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
                break;
            default:
                break;
        }
    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void drawOutput(final int facesArray[][], final int imageWidth, final int imageHeight, final boolean enroll) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (wffrdualcamapp.finish_state==-1){
                    finish();
                }

                if(facesArray!=null){
                    float confidenceValuesCamera[] = wffrdualcamapp.getConfidence();//get Confidence Array from NDK
                    String nameValuesCamera[] = wffrdualcamapp.getNames();//get name Array from NDK

                    ArrayList<Integer> clrleftCornerValues = new ArrayList<>();
                    ArrayList<Integer> clrtopCornerValues = new ArrayList<>();
                    ArrayList<Integer> clrrightCornerValues = new ArrayList<>();
                    ArrayList<Integer> clrbottomCornerValues = new ArrayList<>();

                    ArrayList<Integer> irleftCornerValues = new ArrayList<>();
                    ArrayList<Integer> irtopCornerValues = new ArrayList<>();
                    ArrayList<Integer> irrightCornerValues = new ArrayList<>();
                    ArrayList<Integer> irbottomCornerValues = new ArrayList<>();
                    ArrayList<String> nameList = new ArrayList<>();
                    ArrayList<Float> confidenceValList = new ArrayList<>();

                    for (int i = 0; i < facesArray.length; i++) {//Run for all the faces detected
                        double perReduce = 0.1;
                        int redWidth = (int) (facesArray[i][2] * perReduce);
                        int redHeight = (int) (facesArray[i][3] * perReduce);

                        int leftCornerValue = (facesArray[i][0] + redWidth);// - (facesArray[i][2] ));//XOriginal - rectWidth
                        int rightCornerValue = (facesArray[i][0] + (facesArray[i][2]) - redWidth);//XOriginal + rectWidth
                        int topCornerValue = (facesArray[i][1] + redHeight);//- (facesArray[i][3] ));//yOriginal- rectHeight
                        int bottomCornerValue = (facesArray[i][1] + (facesArray[i][3]) - redHeight);//yOriginal + rectHeight
                        //System.out.println("LeftCorner: " + leftCornerValue + " i: " + i);
                        clrleftCornerValues.add(i, leftCornerValue);
                        clrtopCornerValues.add(i, topCornerValue);
                        clrrightCornerValues.add(i, rightCornerValue);
                        clrbottomCornerValues.add(i, bottomCornerValue);

                        irleftCornerValues.add(i, leftCornerValue);
                        irtopCornerValues.add(i, topCornerValue);
                        irrightCornerValues.add(i, rightCornerValue);
                        irbottomCornerValues.add(i, bottomCornerValue);
                        if (nameValuesCamera.length!=0){
                            nameList.add(i, nameValuesCamera[i]);
                        }
                        if (confidenceValuesCamera.length!=0){
                            confidenceValList.add(i, confidenceValuesCamera[i]);
                        }
                    }

                    long timeLeft = wffrdualcamapp.getTimeLeft();

                    backDetecView.setTimeLeft(timeLeft);
                    frontDetecView.setTimeLeft(timeLeft);
                    //System.out.println("Time of YUV Image After and before rendereing rect");
                    backDetecView.setVisibility(View.VISIBLE);
                    frontDetecView.setVisibility(View.VISIBLE);
                    backDetecView.setRectValuesArray(clrleftCornerValues, clrtopCornerValues, clrrightCornerValues, clrbottomCornerValues);
                    frontDetecView.setRectValuesArray(irleftCornerValues, irtopCornerValues, irrightCornerValues, irbottomCornerValues);
                    backDetecView.setValuesArray(nameList, confidenceValList);
                    frontDetecView.setValuesArray(nameList, confidenceValList);

                    //当前屏幕尺寸与送去识别的image尺寸比
                    float scaleY = (float) backDetecView.getHeight() / (float) imageHeight;
                    float scaleX = (float) backDetecView.getWidth() / (float) imageWidth;
//                    Log.d(TAG, "scaleX:"+scaleX+" ,scaleY"+scaleY);
                    backDetecView.setScaleValues(scaleX,scaleY);

                    scaleY = (float) frontDetecView.getHeight() / (float) imageHeight;
                    scaleX = (float) frontDetecView.getWidth() / (float) imageWidth;

                    frontDetecView.setScaleValues(scaleX,scaleY);

                    if (confidenceValList.size()>0){
                        Log.d(TAG, "confidenceValList:"+confidenceValList.size());
                        for (Float confidence:confidenceValList){
                            Log.d(TAG, "confidence[i]"+confidence+"\n");
                        }
                    }
                    if (nameList.size()>0) {
                        Log.d(TAG, "nameList:" + nameList.size());
                        for (String name : nameList) {
                            Log.d(TAG, "name[i]"+name + "\n");
                        }
                    }
                }
                else {
                    backDetecView.setRectValuesArray(null, null, null, null);
                    frontDetecView.setRectValuesArray(null, null, null, null);
                }
                backDetecView.invalidate();
                frontDetecView.invalidate();
            }
        });
    }


}
