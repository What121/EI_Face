package com.bestom.eiface.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bestom.eiface.Control.CameraViewController;
import com.bestom.eiface.Handler.RegisterHandler;
import com.bestom.eiface.MyApp;
import com.bestom.eiface.R;
import com.wf.wffrdualcamapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 用户注册
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private Activity mActivity;
    private Unbinder mUnbinder;

    private RegisterHandler mRegisterHandler;

    @BindView(R.id.register_tv_cancel) TextView cancel;
    @BindView(R.id.register_tv_submit) TextView submit;
    @BindView(R.id.register_headimg) CircleImageView headImage;
    @BindView(R.id.register_edit_name) EditText nameEdit;
    @BindView(R.id.register_edit_no) EditText noEdit;

    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext=this;
        mActivity=this;
        mUnbinder= ButterKnife.bind(mActivity);
        mRegisterHandler=new RegisterHandler(this);

        initview();

    }

    private void initview(){
        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        //region 结束注册初始化为识别模式
        initrecognize();
        mRegisterHandler=null;

        overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_tv_cancel:
                finish();
                break;
            case R.id.register_tv_submit:
                DoRegister();
                break;
        }
    }

    private void initrecognize(){
        //设置当前算法模式为1
        wffrdualcamapp.setState(1);
        CameraViewController.getInstant().frontCameraView.setEnrolled("",false);
        CameraViewController.getInstant().frontCameraView.setRegisterHandler(null);
        CameraViewController.getInstant().backCameraView.setEnrolled("",false);
        CameraViewController.getInstant().backCameraView.setRegisterHandler(null);
    }

    private void initenroll(String registerInfo){
        //设置当前算法模式为2
        wffrdualcamapp.setState(2);
        CameraViewController.getInstant().frontCameraView.setEnrolled(registerInfo,true);
        CameraViewController.getInstant().frontCameraView.setRegisterHandler(mRegisterHandler);
        CameraViewController.getInstant().backCameraView.setEnrolled(registerInfo,true);
        CameraViewController.getInstant().backCameraView.setRegisterHandler(mRegisterHandler);
    }

    /****************************************************
     * register
     */
    private void DoRegister() {
        //region checkEditView
        String name = nameEdit.getText().toString().trim();
        String NO = noEdit.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showMsg("姓名不能为空！");
            return ;
        }
        if (TextUtils.isEmpty(NO)) {
            showMsg("身份证号不能为空！");
            return ;
        }
        //endregion
        //region 初始化注册
        String registInfo=name+","+NO;
        initenroll(registInfo);
        //endregion

    }

    public void updateUI(int what, byte[] clrFrame){
        //region 更新ui头像
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
        //endregion

        //region 判断是否注册成功
        if (what==0){
            Log.d(TAG, "updateUI: 注册成功");
            this.finish();
        }
        //endregion

    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
