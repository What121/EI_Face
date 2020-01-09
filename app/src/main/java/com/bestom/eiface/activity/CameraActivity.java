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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestom.ei_library.EIFace;
import com.bestom.ei_library.commons.constant.EICode;
import com.bestom.ei_library.commons.utils.MyUtil;
import com.bestom.ei_library.commons.utils.PermissionsUtils;
import com.bestom.ei_library.commons.utils.SPUtil;
import com.bestom.eiface.Control.CameraController;
import com.bestom.eiface.Control.CameraDataQueueController;
import com.bestom.eiface.Control.CameraViewController;
import com.bestom.eiface.MyApp;
import com.bestom.eiface.R;
import com.bestom.ei_library.commons.constant.Settings;
import com.bestom.eiface.view.CameraDetectView;
import com.bestom.eiface.view.CameraView;

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
    public final int UPDATE_PASSINFO=10;
    private boolean upFlag = false;
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
    @BindView(R.id.tx_score) TextView scoreText;
    @BindView(R.id.userinfo_headimg) CircleImageView headImageView;
    @BindView(R.id.userinfo_name) TextView nameText;
    @BindView(R.id.userinfo_no) TextView noText;
    private TextView submit , cancel;
    private EditText nameEdit,noEdit;
    private CircleImageView headImage;

    ArrayList<String> nameList ;
    ArrayList<String> IDList ;
    ArrayList<Float> confidenceValList;

    private String passID="";
    private int passtimes=0;


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DIALOG_DISMISS) {
                registerView.setEnabled(true);
                headImage.setImageResource(R.drawable.default_face);
                nameEdit.setText("");
                noEdit.setText("");
                alertDialog.cancel();
            }else if (msg.what == UPDATE_PASSINFO){
                mHandler.removeMessages(UPDATE_PASSINFO);
                if (!upFlag&&checkPassTimes()){
                    updatePassUI();
                }
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
        backLayout.setVisibility(View.VISIBLE);
        systemView.setOnClickListener(this);
        upregisterView.setOnClickListener(this);
        registerView.setOnClickListener(this);
        settingView.setOnClickListener(this);
    }

    private void initdialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_register, null);
        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        view.setMinimumWidth((int) (displayRectangle.width() * 0.5f));
        view.setMinimumHeight((int) (displayRectangle.height() * 0.3f));

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setView(view);

        alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        alertDialog.setCancelable(false);
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

    private void initCameraView(){
        if(SPUtil.getValue(mContext,Settings.FACE_IR,false)){
            frontLayout.setVisibility(View.VISIBLE);
        }else {
            frontLayout.setVisibility(View.GONE);
        }
        frontCameraView=null;
        frontCameraView=findViewById(R.id.cameraView_front);
        frontDetecView=findViewById(R.id.cameraDetect_front);
        backCameraView=findViewById(R.id.cameraView_back);
        backDetecView=findViewById(R.id.cameraDetect_back);

        frontCameraView.setDrawActivity(this);
        if(!SPUtil.getValue(mContext, Settings.FACE_IR,false)){
            frontCameraView.IRCamerainit(true);
        }else {
            frontCameraView.IRCamerainit(false);
        }

        backCameraView.setDrawActivity(this);
        CameraViewController.getInstant().putFCameraView(frontCameraView);
        CameraViewController.getInstant().putBCameraView(backCameraView);
    }

    @Override
    protected void onResume() {
//        initCameraView();
        if (MyApp.face_state){
            EIFace.setState(1);
        }else {
            EIFace.setState(0);
        }
        PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);

        Log.d(TAG, "onResume: ");
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
                EIFace.stopExecution();
                //初始化状态
                EIFace.setState(0);
                Intent DBintent = new Intent(this, RegisterDBActivity.class);
                startActivity(DBintent);
                break;
            case R.id.setting_tv:
                EIFace.stopExecution();
                //初始化状态
                EIFace.setState(0);
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.upregister_tv:
                EIFace.stopExecution();
                //初始化状态
                EIFace.setState(0);
                Intent registerImg=new Intent(mContext,RegisterIMGActivity.class);
                startActivity(registerImg);
                break;
            case R.id.register_tv:
                EIFace.stopExecution();
                registerView.setEnabled(false);
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

                // use EL judge idCard NO.
                if(!MyUtil.IDCardEL(ID)){
                    Toast.makeText(mActivity, "IDCard NO. format error", Toast.LENGTH_SHORT).show();
                    return;
                }

                //region simple judge idcard NO.
//                if (TextUtils.isEmpty(ID)) {
//                    Toast.makeText(mActivity, "ID Can't be empty", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                //endregion

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
                mHandler.sendEmptyMessageDelayed(DIALOG_DISMISS,1500);

                enrollFinished();
                break;
            default:
                break;
        }
    }

    private void initenroll(String registInfo){
        EIFace.setState(2);
        registerView.setEnabled(false);
        backDetecView.isEnrolling(true);
        backCameraView.setEnrolled(registInfo,true);
        Toast.makeText(mActivity, "Hello " + registInfo, Toast.LENGTH_SHORT).show();
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
            }else if (what == EICode.DB_ERROR.getCode()){
                Log.d(TAG, "updateUI: 注册失败");
//            showMsg(EICode.DB_ERROR_ID.getMsg());
//            mRegisterHandler.sendEmptyMessageDelayed(110,3000);
                enrollFinished();
            } else if (what== EICode.DB_ERROR_ID.getCode()){
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

    private Boolean checkPassTimes(){
        if (IDList.size()>=1){
            String nowID=IDList.get(0).trim();
            if (nowID.equals("")){
                return false;
            }
            if (nowID.equals(passID)){
                passtimes++;
                Log.d(TAG, "checkPassTimes ID : "+nowID+" pass times up "+passtimes);
            }else {
                passtimes=1;
                passID=nowID;
            }

            if (passtimes>=MyApp.face_times){
                //init default value
                passtimes=0;
                passID="";
                return true;
            }
        }
        return false;
    }

    public void updatePassUI(){
        if (confidenceValList.size()>0){
            if (confidenceValList.get(0)>0){
                upFlag=true;
                scoreText.setText(confidenceValList.get(0).toString().subSequence(0,4));
                nameText.append(nameList.get(0));
                Log.i(TAG, "IDList.get(0): "+IDList.get(0));
                noText.setText(IDList.get(0).substring(14,18));
                byte[] clrFrame = CameraDataQueueController.getInstance().getF();
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
                headImageView.setImageBitmap(bitmap);

                mHandler.postDelayed(() -> {
                    scoreText.setText("0.0");
                    nameText.setText("姓名：");
                    noText.setText("身份证号:");
                    headImageView.setImageResource(R.drawable.default_face);
                    upFlag=false;
                },1500);
            }
        }
    }

    public void drawOutput(final int facesArray[][], final int imageWidth, final int imageHeight, final boolean enroll) {
        runOnUiThread(() -> {
//                if (wffrdualcamapp.finish_state==-1){
            if (EIFace.getFinishState()==-1){
                finish();
            }
            long startdraw = System.currentTimeMillis();

            if(facesArray!=null){
//                    float confidenceValuesCamera[] = wffrdualcamapp.getConfidence();//get Confidence Array from NDK
//                    String nameValuesCamera[] = wffrdualcamapp.getNames();//get name Array from NDK
                float confidenceValues[] =EIFace.getConfidence();//get Confidence Array from NDK

                ArrayList<Integer> clrleftCornerValues = new ArrayList<>();
                ArrayList<Integer> clrtopCornerValues = new ArrayList<>();
                ArrayList<Integer> clrrightCornerValues = new ArrayList<>();
                ArrayList<Integer> clrbottomCornerValues = new ArrayList<>();

                ArrayList<Integer> irleftCornerValues = new ArrayList<>();
                ArrayList<Integer> irtopCornerValues = new ArrayList<>();
                ArrayList<Integer> irrightCornerValues = new ArrayList<>();
                ArrayList<Integer> irbottomCornerValues = new ArrayList<>();
                nameList = new ArrayList<>();
                IDList = new ArrayList<>();
                confidenceValList = new ArrayList<>();

                for (int i = 0; i < facesArray.length; i++) {//Run for all the faces detected
                    String name = EIFace.getNames();//get name Array from NDK
                    String ID = EIFace.getIDs();//get ID Array from NDK
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

                    nameList.add(i, name);
                    IDList.add(i,ID);
                    if (confidenceValues.length!=0){
                        confidenceValList.add(i, confidenceValues[i]);
                    }
                }
//                    long timeLeft = wffrdualcamapp.getTimeLeft();
                long timeLeft = EIFace.getTimeLeft();

                backDetecView.setTimeLeft(timeLeft);
                frontDetecView.setTimeLeft(timeLeft);
                //System.out.println("Time of YUV Image After and before rendereing rect");
                backDetecView.setVisibility(View.VISIBLE);
                //IR camera 不显示实时信息
//                frontDetecView.setVisibility(View.VISIBLE);
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
                if (IDList.size()>0) {
                    Log.d(TAG, "IDList:" + IDList.size());
                    for (String id : IDList) {
                        Log.d(TAG, "id[i]"+id + "\n");
                    }
                }
                if (confidenceValList.size()>0){
                    mHandler.sendEmptyMessage(UPDATE_PASSINFO);
                }

            }
            else {
                backDetecView.setRectValuesArray(null, null, null, null);
                frontDetecView.setRectValuesArray(null, null, null, null);
            }

            backDetecView.invalidate();
            frontDetecView.invalidate();

            long finishdraw = System.currentTimeMillis();
//            Log.e("**Time**", "\nstartdraw:"+startdraw+"\nfinishdraw:"+finishdraw+"\nDrawTime"+(finishdraw-startdraw) );

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
