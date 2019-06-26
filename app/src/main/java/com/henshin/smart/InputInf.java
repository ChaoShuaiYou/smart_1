package com.henshin.smart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;

public class InputInf extends AppCompatActivity {

    private EditText inputTitle = null;
    private EditText inputName = null;
    private EditText inpoutConect = null;

    private Button inputPic = null;
    private Button input = null;
    private Button returnActivity = null;

    private ImageView pic= null;

    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;

    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");

    private Uri imageUri;
    private Uri cropImageUri;

    private SqlHelper sqlHelper ;
    private String picfilepath = "";
    private Bitmap bmp;

    private final int MIME = 5; //权限回调参数
    private Uri uriForFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadinf);

        init();//添加按键
        Input();//提交按钮
        InputPic();//提交图片
        ReturnMainActivity();//返回按钮
    }
    private void init()
    {
        inputTitle = findViewById(R.id.EditTitle);
        inputName = findViewById(R.id.EditName);
        inpoutConect = findViewById(R.id.EditContect);
        inputPic = findViewById(R.id.EditPic);
        input = findViewById(R.id.input);
        returnActivity = findViewById(R.id.returnActivity);
        pic = findViewById(R.id.pic);
        sqlHelper = new SqlHelper(this);//实例化数据库
    }

    private void InputPic()
    {
        inputPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowChoise();
            }
        });
    }

    private void Input()
    {
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean IsRight =  CheckInputInf();//提交检测输入是否合法
                if(IsRight)
                {
                    new PromptDialog(InputInf.this)
                            .setDialogType(PromptDialog.DIALOG_TYPE_WARNING)
                            .setAnimationEnable(true)
                            .setTitleText("警告")
                            .setContentText("上传信息不准为空")
                            .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
                else
                {
                    String title = inputTitle.getText().toString();//标题
                    String name = inputName.getText().toString();//提交姓名
                    String conect = inpoutConect.getText().toString();//提交内容

                    //boolean IsOk = sqlHelper.insert(title,name,conect,picfilepath,bmp);//存入数据库
                    boolean IsOk = sqlHelper.insert(title,name,conect,imageUri.getPath(),bmp);//存入数据库


                    if(IsOk)
                    {
                        new PromptDialog(InputInf.this)
                                .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                                .setAnimationEnable(true)
                                .setTitleText("SUCCESS")
                                .setContentText("提交成功，请在历史纪录查看")
                                .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                    @Override
                                    public void onClick(PromptDialog dialog) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                    else
                    {
                        new PromptDialog(InputInf.this)
                                .setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                                .setAnimationEnable(true)
                                .setTitleText("错误")
                                .setContentText("网络状态不稳定")
                                .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                    @Override
                                    public void onClick(PromptDialog dialog) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }

                }
            }
        });
    }

    private boolean CheckInputInf()
    {
        String title = inputTitle.getText().toString();//标题
        String name = inputName.getText().toString();//提交姓名
        String conect = inpoutConect.getText().toString();//提交内容
        if(title.equals(""))
            return true;
        if(name.equals(""))
            return true;
        if(conect.equals(""))
            return true;
        return false;
    }

    private void ReturnMainActivity()
    {
        returnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputInf.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /*
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相机获取
     */
    public void camera() {

        File outImage = new File(getExternalCacheDir(), "outputimage.jpg");

        if (outImage.exists()) {
            outImage.delete();
        }

        try {
            outImage.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            uriForFile = FileProvider.getUriForFile(InputInf.this, "com.example.jing.myapplication,fileprovider", outImage);
        } else {
            uriForFile = Uri.fromFile(outImage);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

//    /*
//     * 判断sdcard是否被挂载
//     */
//    private boolean hasSdcard() {
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            return true;
//        } else {
//            return false;
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PHOTO_REQUEST_GALLERY) {
//            // 从相册返回的数据
//            if (data != null) {
//                // 得到图片的全路径
//                Uri uri = data.getData();
//                String realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
//                picfilepath = realPathFromUri;
//                crop(uri);
//            }
//
//        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
//            // 从相机返回的数据
//            if (hasSdcard()) {
//                picfilepath = uriForFile.getPath();
//                crop(uriForFile);
//            } else {
//                Toast.makeText(InputInf.this, "未找到存储卡，无法存储照片！", 0).show();
//            }
//
//        } else if (requestCode == PHOTO_REQUEST_CUT) {
//            // 从剪切图片返回的数据
//            if (data != null) {
//                //picfilepath = data.getData().getPath();
//                Bitmap bitmap = data.getParcelableExtra("data");
//                bmp = bitmap;
//                this.pic.setImageBitmap(bitmap);
//            }
//            try {
//                // 将临时文件删除
//                //tempFile.delete();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    private void ShowChoise()
    {

        ColorDialog dialog = new ColorDialog(this);
        dialog.setTitle("选择图片");
        dialog.setContentText("选择图片来源");
        dialog.setPositiveListener("相册", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {

//                gallery();  //从相册选取

                autoObtainStoragePermission();

                dialog.dismiss();

            }
        })
                .setNegativeListener("拍照", new ColorDialog.OnNegativeListener() {
                    @Override
                    public void onClick(ColorDialog dialog) {

                        //申请相机权限，并开始拍照
                        autoObtainCameraPermission();
//                        if (ContextCompat.checkSelfPermission(InputInf.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                            ActivityCompat.requestPermissions(InputInf.this, new String[]{Manifest.permission.CAMERA}, MIME);
//                        } else {
//                            camera();
//                        }
                        dialog.dismiss();
                    }
                }).show();



    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent myIntent = new Intent(InputInf.this, MainActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case MIME:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    camera();
//                } else {
//                    Toast.makeText(this, "您尚未打开摄像头的权限", Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//        }
//    }


    /**
     * 自动获取相机权限
     */
    private void autoObtainCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ToastUtils.showShort(this, "您已经拒绝过一次");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {
                imageUri = Uri.fromFile(fileUri);
                //通过FileProvider创建一个content类型的Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(this, "com.zz.fileprovider", fileUri);
                }
                PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
            } else {
                ToastUtils.showShort(this, "设备没有SD卡！");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            //调用系统相机申请拍照权限回调
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        imageUri = Uri.fromFile(fileUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            imageUri = FileProvider.getUriForFile(this, "com.zz.fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
                        PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        ToastUtils.showShort(this, "设备没有SD卡！");
                    }
                } else {

                    ToastUtils.showShort(this, "请允许打开相机！！");
                }
                break;


            }
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
                } else {

                    ToastUtils.showShort(this, "请允许打操作SDCard！！");
                }
                break;
            default:
        }
    }

    private static final int OUTPUT_X = 480;
    private static final int OUTPUT_Y = 480;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //拍照完成回调
                case CODE_CAMERA_REQUEST:
                    cropImageUri = Uri.fromFile(fileCropUri);
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                    break;
                //访问相册完成回调
                case CODE_GALLERY_REQUEST:
                    if (hasSdcard()) {
                        cropImageUri = Uri.fromFile(fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            newUri = FileProvider.getUriForFile(this, "com.zz.fileprovider", new File(newUri.getPath()));
                        }
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                    } else {
                        ToastUtils.showShort(this, "设备没有SD卡！");
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                    if (bitmap != null) {
                        showImages(bitmap);
                        bmp=bitmap;
                    }
                    break;
                default:
            }
        }
    }

    /**
     * 自动获取sdk权限
     */

    private void autoObtainStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
        }

    }

    private void showImages(Bitmap bitmap) {
        pic.setImageBitmap(bitmap);
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }




}
