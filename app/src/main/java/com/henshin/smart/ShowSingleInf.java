package com.henshin.smart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;

public class ShowSingleInf extends AppCompatActivity {
    private String id;
    private TextView title;
    private TextView name;
    private TextView contect;
    private ImageView pic;
    private Button returnAct;
    private Button delete;
    private SqlHelper sqlHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singleinf);
        Intent intent = getIntent();
        id =  intent.getStringExtra("id");
        sqlHelper = new SqlHelper(this);
        init();
        PutInf();
        delete();
        retuanAct();
    }
    private void init()
    {
        title = findViewById(R.id.ShowTitle);
        name = findViewById(R.id.ShowName);
        contect = findViewById(R.id.ShowContect);
        pic = findViewById(R.id.Showpic);
        returnAct = findViewById(R.id.returnAct);
        delete = findViewById(R.id.delete);
    }
    private void delete()
    {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowChoise();
            }
        });
    }
    private void retuanAct()
    {
        returnAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ShowSingleInf.this, ShowInf.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
    private void PutInf()
    {
        InfModel infModel = sqlHelper.getById(id);
        title.setText(infModel.getTitle());
        name.setText(infModel.getName());
        contect.setText(infModel.getContect());
        if(!infModel.getPicPath().equals(""))
        {
              pic.setImageBitmap(infModel.getPicreal());//ImageView显示图片
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent myIntent = new Intent(ShowSingleInf.this, ShowInf.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void ShowChoise()
    {

        ColorDialog dialog = new ColorDialog(this);
        dialog.setTitle("确定删除？");
        dialog.setContentText("删除数据将不可恢复");
        dialog.setPositiveListener("确定", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                if(sqlHelper.delete(id))
                {
                    new PromptDialog(ShowSingleInf.this)
                            .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                            .setAnimationEnable(true)
                            .setTitleText("SUCCESS")
                            .setContentText("删除成功")
                            .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
                else
                {
                    new PromptDialog(ShowSingleInf.this)
                            .setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                            .setAnimationEnable(true)
                            .setTitleText("错误")
                            .setContentText("删除失败")
                            .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
                dialog.dismiss();
            }
        })
                .setNegativeListener("取消", new ColorDialog.OnNegativeListener() {
                    @Override
                    public void onClick(ColorDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
