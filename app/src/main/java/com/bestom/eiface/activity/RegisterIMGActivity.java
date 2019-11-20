package com.bestom.eiface.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestom.ei_library.EIFace;
import com.bestom.ei_library.commons.utils.DataTurn;
import com.bestom.ei_library.commons.utils.FileUtil;
import com.bestom.ei_library.commons.utils.HttpUtil;
import com.bestom.eiface.MyApp;
import com.bestom.eiface.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterIMGActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterIMGActivity";
    private Context mContext;
    private Activity mActivity;

    ImageView mImageView;
    LinearLayout userinfo;
    EditText mNameEdit,mNumberEdit;
    Button select_bt,selectNET_bt,sub;
    TextView log_tv;

    private com.bestom.ei_library.commons.utils.DataTurn DataTurn=new DataTurn();
    private Bitmap bitmap;
    private String picturePath="";
    public static final int CHOOSE_PHOTO = 2;
    private final int USER_VIEW_SHOW = 1;

    private final String imgPath=MyApp.Cachepath+"register"+ File.separator;

    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == USER_VIEW_SHOW) {
                showImgAndUserinfo();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_img);
        mContext=this;
        mActivity=this;

        initview();
    }

    private void init(){


    }

    private void initview(){
        userinfo= (LinearLayout) findViewById(R.id.user_info_layout);
        mNameEdit= (EditText) findViewById(R.id.name_tv);
        mNumberEdit= (EditText) findViewById(R.id.number_tv);
        log_tv= (TextView) findViewById(R.id.log_tv);
        log_tv.setTextColor(getResources().getColor(R.color.colorRed));
        select_bt= (Button) findViewById(R.id.select_picture);
        select_bt.setOnClickListener(this);
        selectNET_bt= (Button) findViewById(R.id.select_Netpicture);
        selectNET_bt.setOnClickListener(this);
        mImageView= (ImageView) findViewById(R.id.img_view);
        mImageView.setOnClickListener(this);
        sub= (Button) findViewById(R.id.sub);
        sub.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_picture:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,CHOOSE_PHOTO);//打开相册
                break;
            case R.id.select_Netpicture:
                String imgurl="http://static.dalitek.tech:10000/file/cmp/e1f84d50-722a-11e9-95bb-c56b00cd6e97.jpg";//150kb
                HttpUtil.doGet(imgurl, new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        InputStream inputStream = response.body().byteStream();//得到图片的流
                        bitmap = BitmapFactory.decodeStream(inputStream);

                        //region 保存网络图片到本地
                         picturePath= FileUtil.SavaImage(bitmap,imgPath );
                        //endregion
                        inputStream.close();

                        mHandler.sendEmptyMessage(USER_VIEW_SHOW);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            //图片提交注册
            case R.id.sub:
                //region get registerinfo
                String name = mNameEdit.getText().toString().trim();
                String ID = mNumberEdit.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(mContext,"请填写姓名！",Toast.LENGTH_SHORT).show();
                    break;
                }
                if (TextUtils.isEmpty(ID)) {
                    Toast.makeText(mContext,"请填写身份证号！",Toast.LENGTH_SHORT).show();
                    break;
                }

                String registInfo = name + "," + ID;
                userinfo.setVisibility(View.GONE);
                //endregion
                select_bt.setVisibility(View.GONE);
                selectNET_bt.setVisibility(View.GONE);
                sub.setVisibility(View.GONE);
                log_tv.setText("registerinfo:"+registInfo+"\n");
                register(registInfo);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri =data.getData();
        switch (requestCode){
            case CHOOSE_PHOTO:
                picturePath="";
                //选择图片
                choosephoto(uri);
                break;
            default:
                break;
        }
    }

    //图片选择器
    private void choosephoto(Uri uri){
        final Uri selectedImage = uri;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        picturePath = cursor.getString(columnIndex);
        cursor.close();
        //图片路径picturePath
        bitmap = FileUtil.getBitmapForImgPath (picturePath);//从路径加载出图片bitmap  //20190614修改通过uri set 图片
        picturePath = FileUtil.SavaImage(bitmap,imgPath);   //保存注册图片

//        bitmap = rotateBimap(this, -90, bitmap);//旋转图片-90°

        mHandler.sendEmptyMessage(USER_VIEW_SHOW);
    }

    private void showImgAndUserinfo(){
        if (bitmap!=null)
            mImageView.setImageBitmap(bitmap);//ImageView显示图片

        sub.setVisibility(View.VISIBLE);
        sub.setFocusable(true);

        userinfo.setVisibility(View.VISIBLE);
        mNameEdit.setText("");
        mNumberEdit.setText("");

        log_tv.setText("imagePath:"+picturePath);
    }

    //region 图片注册
    private void register(String registerInfo){
        int recordID = EIFace.EnrollFromJpegFile(picturePath,registerInfo);
        //更新注册结果
        updateImgResult(recordID);
    }
    //endregions


    private void updateImgResult(int recordID){
        Log.d(TAG, "updateImgResult recordID: "+recordID);
        if (recordID>=0){
            log_tv.append("success:"+recordID);
        }else {
            log_tv.append("fail:"+recordID);
        }
    }

    @Override
    public void onBackPressed() {
        EIFace.initwff();
        super.onBackPressed();
    }
}
