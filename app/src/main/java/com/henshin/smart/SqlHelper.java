package com.henshin.smart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SqlHelper extends SQLiteOpenHelper {
    private static final String name = "smart.db"; //数据库名称

    private static final int version = 1; //数据库版本
    public SqlHelper(Context context) {
        //第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
        super(context, name, null, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS inputinf (_id integer primary key autoincrement, title varchar(20), name varchar(20),contect text,pic text,picreal BLOB)");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      //  db.execSQL("ALTER TABLE person ADD phone VARCHAR(12)"); //往表中增加一列
    }
    public List<InfModel> getAll(String where, String orderBy) {//返回表中的数据,where是调用时候传进来的搜索内容,orderby是设置中传进来的列表排序类型
        StringBuilder buf=new StringBuilder("SELECT * FROM inputinf");
        List<InfModel> infModelList = new ArrayList<>();
        if (!where.equals("")) {
            buf.append(" WHERE ");
            buf.append(where);
        }

        if (!orderBy.equals("")) {
            buf.append(" ORDER BY ");
            buf.append(orderBy);
        }
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM inputinf", null);
        if(cursor!=null)
        {
            if(cursor.getCount()>0)
            {
                cursor.moveToFirst();
                for(int i = 0; i < cursor.getCount(); i++)
                {
                    int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String contect = cursor.getString(cursor.getColumnIndex("contect"));
                    String pic = cursor.getString(cursor.getColumnIndex("pic"));
                    InfModel temp = new InfModel();
                    temp.setId(id);
                    temp.setTitle(title);
                    temp.setName(name);
                    temp.setContect(contect);
                    temp.setPicPath(pic);
                    infModelList.add(temp);
                    //移动到下一位
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        getReadableDatabase().close();
        return infModelList;
    }
    public InfModel getById(String id) {//根据点击事件获取id,查询数据库
       Cursor cursor =  getReadableDatabase().rawQuery("SELECT * FROM inputinf  WHERE _id = "+id,null);
        InfModel temp = new InfModel();
       if(cursor.getCount()>0)
       {
           cursor.moveToFirst();
           int _id = cursor.getInt(cursor.getColumnIndex("_id"));
           String title = cursor.getString(cursor.getColumnIndex("title"));
           String name = cursor.getString(cursor.getColumnIndex("name"));
           String contect = cursor.getString(cursor.getColumnIndex("contect"));
           String pic = cursor.getString(cursor.getColumnIndex("pic"));
           temp.setId(Integer.parseInt(id));
           temp.setTitle(title);
           temp.setName(name);
           temp.setContect(contect);
           temp.setPicPath(pic);
           byte[] in=cursor.getBlob(cursor.getColumnIndex("picreal"));
           Bitmap bmpout= BitmapFactory.decodeByteArray(in,0,in.length);
           temp.setPicreal(bmpout);
       }
        cursor.close();
        getReadableDatabase().close();
        return temp;
    }
    public boolean insert(String title, String name, String contect, String pic,Bitmap bmp) {
        try
        {
            ContentValues cv=new ContentValues();
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            if(bmp!=null)
            {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
            }
            cv.put("title", title);
            cv.put("name", name);
            cv.put("contect", contect);
            cv.put("pic", pic);
            cv.put("picreal",os.toByteArray());
            getWritableDatabase().insert("inputinf", "name", cv);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            getWritableDatabase().close();
        }
    }
    public void update(String id,String title, String name, String contect, String pic) {
        ContentValues cv=new ContentValues();
        String[] args={id};

        cv.put("title", title);
        cv.put("name", name);
        cv.put("contect", contect);
        cv.put("pic", pic);

        getWritableDatabase().update("inputinf", cv, "_id=?",
                args);
    }
    public boolean delete(String id)
    {
        int resule = 0;
        try
        {
            resule =  getWritableDatabase().delete("inputinf","_id"+" = ?",new String[]{id});
        }
      catch (Exception ex)
      {
          getWritableDatabase().close();
          return false;
      }
        getWritableDatabase().close();
       if(resule>0) return true;
       else  return false;
    }

}
