package com.bestom.eiface.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.bestom.ei_library.commons.utils.DataTurn;
import com.bestom.eiface.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

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

    int i=0;

    private String hexbody;
    private byte[] image;
    private Faceapi mFaceapi;
    private com.bestom.ei_library.commons.utils.DataTurn DataTurn=new DataTurn();

    private Bitmap bitmap;
    private String picturePath="";
    private InputStream Imginputstream;

    private boolean flag_hexbody=false;

    public static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_img);

        init();
        initview();
    }

    private void init(){
        mContext=this;
        mActivity=this;

        mFaceapi=new Faceapi();
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
                        int len = 0;
                        bitmap = BitmapFactory.decodeStream(inputStream);

                        //region 保存网络图片到本地
                         picturePath= FileUtil.SavaImage(bitmap, Environment.getExternalStorageDirectory().getPath()+"/Pictures/testUface");
                        //endregion
                        inputStream.close();
                        flag_hexbody=false;
                        //region  第二步预热操作
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!picturePath.equals(""))
                                        Imginputstream = new FileInputStream(picturePath);
                                    image = DataTurn.inputtoByteArray(Imginputstream);
                                    hexbody=mFaceapi.yuresecond(image);
                                    flag_hexbody=true;
                                }catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (Imginputstream!=null){
                                        try {
                                            Imginputstream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }).start();
                        //endregion

                        //region UI界面更新
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bitmap!=null)
                                    mImageView.setImageBitmap(bitmap);//ImageView显示图片

                                sub.setVisibility(View.VISIBLE);
                                sub.setFocusable(true);

                                userinfo.setVisibility(View.VISIBLE);
                                mNameEdit.setText("");
                                mNumberEdit.setText("");

                                log_tv.setText("imagePath:"+picturePath);
                            }
                        });
                        //endregion
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
                String jobNum = mNumberEdit.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(mContext,"请填写姓名！",Toast.LENGTH_SHORT).show();
                    break;
                }
                if (TextUtils.isEmpty(jobNum)) {
                    Toast.makeText(mContext,"请填写工号！",Toast.LENGTH_SHORT).show();
                    break;
                }

                String nameAndJobHex = DataTurn.StrToHex(name + "," + jobNum+",");
                userinfo.setVisibility(View.GONE);
                //endregion
                select_bt.setVisibility(View.GONE);
                selectNET_bt.setVisibility(View.GONE);
                sub.setVisibility(View.GONE);
                log_tv.setText("registerinfo:"+nameAndJobHex);
                register(nameAndJobHex);
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
        //图片路径picturePath
        bitmap = FileUtil.getBitmapForImgPath (picturePath);//从路径加载出图片bitmap  //20190614修改通过uri set 图片

//        bitmap = rotateBimap(this, -90, bitmap);//旋转图片-90°
        cursor.close();

        flag_hexbody=false;
        //region  第二步预热操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!picturePath.equals(""))
                        Imginputstream = new FileInputStream(picturePath);
                    image = DataTurn.inputtoByteArray(Imginputstream);
                    hexbody=mFaceapi.yuresecond(image);
                    flag_hexbody=true;
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (Imginputstream!=null){
                        try {
                            Imginputstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
        //endregion

        //region UI界面更新
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //update 修改通过 uri Set image
                if (bitmap!=null)
                    mImageView.setImageBitmap(bitmap);//ImageView显示图片

                sub.setVisibility(View.VISIBLE);
                sub.setFocusable(true);

                userinfo.setVisibility(View.VISIBLE);
                mNameEdit.setText("");
                mNumberEdit.setText("");

                log_tv.setText("imagePath:"+picturePath);
            }
        });
        //endregion

    }

    //region 图片上传注册
    private void register(String nameAndJobHex){
        //四步提交环环相扣
        final  String nameAndJob=nameAndJobHex;
        String path="picture.jpeg";
        //String path=picturePath;
        byte[] bytes=DataTurn.HexToIByteArr(DataTurn.StrToHex(path),64);
        //第一步
        mFaceapi.fileOpen(bytes, new RespListener() {
            @Override
            public void onSuccess(int code, String msg) {
                Log.i(TAG, "111111");

                //第二步
                i = 0;
                final int j = (image.length % (150 * 1024 + 4) != 0) ? image.length / (150 * 1024 + 4) + 1 : image.length / (150 * 1024 + 4);//分包发送次数
                //待指令完成拼接 提交第二步
                while (flag_hexbody){
                    mFaceapi.fileData(hexbody, new RespListener() {
                        @Override
                        public void onSuccess(int code, String msg) {
                            Log.i(TAG, "222222");
                            i++;
                            //第二步上传照片做了分包处理，所以会有多次返回，这里取 最后一次数据返回为标志完成，进行第三步提交
                            if (i != j)
                                return;

                            //第三步
                            try {
                                InputStream in = new FileInputStream(new File(picturePath));
                                byte[] md5 = new byte[0];
                                md5 = MD5.getMD5(in);
                                Log.i("md5", md5.length + "--" + md5.toString());
                                mFaceapi.fileDone(md5, new RespListener() {
                                    @Override
                                    public void onSuccess(int code, String msg) {
                                        Log.i(TAG, "3333333");

                                        //第四步
                                        mFaceapi.facePicture(nameAndJob, new RespSampleListener<FaceResult>() {
                                            @Override
                                            public void onSuccess(int code, FaceResult faceResult) {
                                                Log.i(TAG, "4444444");
                                                Toast.makeText(mContext, "成功", Toast.LENGTH_SHORT).show();
                                                log_tv.setText("第四步成功" + code + faceResult.toString());
                                                select_bt.setVisibility(View.VISIBLE);
                                                selectNET_bt.setVisibility(View.VISIBLE);
                                                //置空
                                                bitmap = null;
                                                picturePath = "";
                                            }

                                            @Override
                                            public void onFailure(int code, String msg) {
                                                Toast.makeText(mContext, "注册失败" + code + msg, Toast.LENGTH_SHORT).show();
                                                log_tv.setText("第四步失败" + code + msg);
                                                //置空
                                                bitmap = null;
                                                picturePath = "";
                                                select_bt.setVisibility(View.VISIBLE);
                                                selectNET_bt.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(int code, String msg) {
                                        Log.i(TAG, code + msg);
                                        log_tv.setText("第三步失败" + code + msg);
                                        select_bt.setVisibility(View.VISIBLE);
                                        selectNET_bt.setVisibility(View.VISIBLE);
                                        picturePath = "";
                                    }
                                });
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                                log_tv.setText("第三步失败" + e.getMessage());
                                select_bt.setVisibility(View.VISIBLE);
                                selectNET_bt.setVisibility(View.VISIBLE);
                                picturePath = "";
                            } catch (IOException e) {
                                e.printStackTrace();
                                log_tv.setText("第三步失败" + e.getMessage());
                                select_bt.setVisibility(View.VISIBLE);
                                selectNET_bt.setVisibility(View.VISIBLE);
                                picturePath = "";
                            }
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            Log.i(TAG, code + msg);
                            log_tv.setText("第二步失败" + code + msg);
                            picturePath = "";
                            select_bt.setVisibility(View.VISIBLE);
                            selectNET_bt.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i(TAG,code+msg);
                log_tv.setText("第一步失败"+code+msg);
                picturePath="";
                select_bt.setVisibility(View.VISIBLE);
                selectNET_bt.setVisibility(View.VISIBLE);

            }
        });
    }
    //endregions

}
