package com.bestom.ei_library.core.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.bestom.ei_library.commons.constant.DBConstant;
import com.bestom.ei_library.core.manager.DB.DataBaseOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * DATABASE
 * 初始化入口
 */
public class DBApi {
    private static final String TAG = "DBApi";
    private static List<String> tablesql=new ArrayList<>();

    private static DataBaseOpenHelper mDataBaseOpenHelper;

    /**********************************************************************
     * 初始化数据库
     */
    public static void init(Context mcontext) {

        //创建表结构
        String sql = "create table "+
                DBConstant.TABLE_NAME+"("+
                DBConstant.RECORDID+" integer primary key ,"+
                DBConstant.ID+" varchar(20),"+
                DBConstant.NAME+" varchar(20))";
        tablesql.add(sql);
        mDataBaseOpenHelper=DataBaseOpenHelper.getInstance(mcontext,DBConstant.DATABASE_NAME,DBConstant.DATABASE_VERSION,tablesql);
    }


    //通过recordid的增
    public static void checkPersonToinsert(int recordid,String num ,String name){
        if (queryPersonInfoByRecordID(recordid).getCount()<=0){
            insertPersonInfo(recordid,num,name);
        }
    }

    public static void insertPersonInfo(int recordid,String id, String name){
        //Log.i("------num-----------", num);
        ContentValues contentValues=new ContentValues();
        contentValues.put("recordid",recordid);
        contentValues.put("id",id);
        contentValues.put("name",name);
        mDataBaseOpenHelper.insert(DBConstant.TABLE_NAME,contentValues);
    }

    public static Cursor queryPersonInfoByRecordID(int recordid){
        String sql="where recordid="+recordid;
        return mDataBaseOpenHelper.query(DBConstant.TABLE_NAME,sql);
    }

    public static Cursor queryPersonInfoByID(String ID){
        String sql="where id="+ID;
        return mDataBaseOpenHelper.query(DBConstant.TABLE_NAME,sql);
    }

    public static void deletePersonInfoByRecordID(int recordid){
        String whereargs[]=new String[1];
        whereargs[0]=String.valueOf(recordid);
        mDataBaseOpenHelper.delete(DBConstant.TABLE_NAME,"recordid =?",whereargs);
    }

    public static Cursor queryAll(){
        String sql="";
        return mDataBaseOpenHelper.query(DBConstant.TABLE_NAME,sql);
    }

    public static void deleteAll(){
        String sql="delete from "+DBConstant.TABLE_NAME;
        mDataBaseOpenHelper.execSQL(sql);
    }


    //通过num的增删改查
    public static void updateItemBynum(long time,String num ){
        ContentValues contentValues=new ContentValues();
        contentValues.put("time",String.valueOf(time));
        String[] strings=new String[1];
        strings[0]=num;
        mDataBaseOpenHelper.update(DBConstant.TABLE_NAME,contentValues,"num=?",strings);
    }


}
