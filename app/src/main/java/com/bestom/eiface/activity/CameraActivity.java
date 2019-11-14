package com.bestom.eiface.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestom.ei_library.EIFace;
import com.bestom.ei_library.commons.constant.EICode;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private AlertDialog alertDialog;
    private Unbinder mUnbinder;

    public final int DIALOG_DISMISS=11;

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
    private TextView submit , cancel;
    private EditText nameEdit,noEdit;
    private CircleImageView headImage;


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DIALOG_DISMISS) {
                registerView.setEnabled(true);
                alertDialog.cancel();
            }
        }
    };

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
        initdialog();
    }

    private void  initview(){
        frontLayout.setVisibility(View.GONE);
        backLayout.setVisibility(View.VISIBLE);
        systemView.setOnClickListener(this);
        upregisterView.setOnClickListener(this);
        registerView.setOnClickListener(this);
        settingView.setOnClickListener(this);
    }

    private void initdialog(){

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
                EIFace.stopExecution();
                Toast.makeText(mContext,"设置待开通...",Toast.LENGTH_SHORT).show();
                //初始化状态
//                wffrdualcamapp.setState(0);
                EIFace.setState(0);
                Intent DBintent = new Intent(this, RegisterDBActivity.class);
                startActivity(DBintent);
                break;
            case R.id.upregister_tv:
                Toast.makeText(mContext,"上传注册待开通...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.register_tv:
//                Intent intent = new Intent(this, RegisterActivity.class);
//                startActivity(intent);
//                //Activity切换动画
//                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
                EIFace.stopExecution();
                registerView.setEnabled(false);
                enterNameDialogBox();
                alertDialog.show();
                break;
            case R.id.register_tv_submit:
                String name = nameEdit.getText().toString().trim();
                String ID = noEdit.getText().toString().trim();
                //region checkEdit
                //also check when firstname is not empty firstname should always be there!!
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(mActivity, "Name Can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(ID)) {
                    Toast.makeText(mActivity, "ID Can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                //endregion
                //region 初始化注册
                String registInfo = name + "," + ID;
                initenroll(registInfo);
                //endregion
                break;
            case R.id.register_tv_cancel:
                EIFace.setState(1);
                backCameraView.setEnrolled("",false);
                backDetecView.isEnrolling(false);
                mHandler.sendEmptyMessageDelayed(DIALOG_DISMISS,2000);

                enrollFinished();

                Log.d(TAG, "onClick: cancel");
                break;
            default:
                break;
        }
    }

    private void enterNameDialogBox() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_register, null);
        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        view.setMinimumWidth((int) (displayRectangle.width() * 0.5f));
        view.setMinimumHeight((int) (displayRectangle.height() * 0.3f));

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);
        cancel = view.findViewById(R.id.register_tv_cancel);
        submit = view.findViewById(R.id.register_tv_submit);
        headImage = view.findViewById(R.id.register_headimg);
        nameEdit = view.findViewById(R.id.register_edit_name);
        noEdit = view.findViewById(R.id.register_edit_no);
        alertDialog.setView(view);

        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    private void initenroll(String registInfo){
        wffrdualcamapp.setState(2);
        registerView.setEnabled(false);
        backDetecView.isEnrolling(true);
        backCameraView.setEnrolled(registInfo,true);
        Toast.makeText(mActivity, "Hello " + registInfo, Toast.LENGTH_SHORT).show();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                wffrdualcamapp.setState(1);
//                name = "";
//                enroll_button.setChecked(false);
//                enroll_button.setEnabled(true);
//                backDetecView.isEnrolling(false);
//                textureView.setEnrolledName("",false);
//                textureview2.setEnrolledName("",false);
//            }
//        },12000);
    }


    private void enrollFinished(){
        EIFace.setState(1);
        backCameraView.setEnrolled("",false);
        backDetecView.isEnrolling(false);

        mHandler.sendEmptyMessageDelayed(DIALOG_DISMISS,2000);

    }

    public void updateUI(int what, byte[] clrFrame){
        if (alertDialog.isShowing()){
            //region 更新ui头像
            runOnUiThread(() -> {
                Bitmap bitmap = null;
                YuvImage image = new YuvImage(clrFrame, ImageFormat.NV21, MyApp.CAMERA_WIDTH, MyApp.CAMERA_HEIGHT, null);
                if (image != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compressToJpeg(new Rect(0, 0, MyApp.CAMERA_WIDTH,  MyApp.CAMERA_HEIGHT), 80, stream);

                    bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //ScaleX
                if (MyApp.MirrorX){
                    Matrix matrix=new Matrix();
                    //沿x镜像
                    matrix.postScale(-1,1);
                    bitmap=Bitmap.createBitmap(bitmap,0,0,MyApp.CAMERA_WIDTH,MyApp.CAMERA_HEIGHT,matrix,true);
                }
                headImage.setImageBitmap(bitmap);
            });
            //endregion

            //region 判断是否注册成功
            if (what== EICode.DB_SUCCESS.getCode()){
                Log.d(TAG, "updateUI: 注册成功");
//            showMsg(EICode.DB_SUCCESS.getMsg());
//            mRegisterHandler.sendEmptyMessageDelayed(110,2000);
                enrollFinished();
            }else if (what== EICode.DB_ERROR_ID.getCode()){
                Log.d(TAG, "updateUI: ID已注册");
//            showMsg(EICode.DB_ERROR_ID.getMsg());
//            mRegisterHandler.sendEmptyMessageDelayed(110,3000);
                enrollFinished();
            }else if (what==EICode.DB_ERROR_RECORDID.getCode()){
                Log.d(TAG, "updateUI: recordid 异常");
//            showMsg(EICode.DB_ERROR_RECORDID.getMsg());
//            mRegisterHandler.sendEmptyMessageDelayed(110,2000);
                enrollFinished();
            }
            //endregion
        }
    }


    public void drawOutput(final int facesArray[][], final int imageWidth, final int imageHeight, final boolean enroll) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if (wffrdualcamapp.finish_state==-1){
                if (EIFace.getFinishstate()==-1){
                    finish();
                }

                if(facesArray!=null){
//                    float confidenceValuesCamera[] = wffrdualcamapp.getConfidence();//get Confidence Array from NDK
//                    String nameValuesCamera[] = wffrdualcamapp.getNames();//get name Array from NDK
                    float confidenceValuesCamera[] =EIFace.getConfidence();//get Confidence Array from NDK
                    String nameValuesCamera[] = EIFace.getNames();//get name Array from NDK

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

//                    long timeLeft = wffrdualcamapp.getTimeLeft();
                    long timeLeft = EIFace.getTimeLeft();

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

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
