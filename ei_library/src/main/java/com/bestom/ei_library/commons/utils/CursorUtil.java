package com.bestom.ei_library.commons.utils;

import android.database.Cursor;

public class CursorUtil {
private static final String TAG=CursorUtil.class.getSimpleName();

    public static void cursorUtil(Cursor cursor) {
        StringBuilder stringBuilder=new StringBuilder();
        //判断游标是否为空
        if (cursor.moveToFirst()) {
            //遍历游标
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.move(i);
                //获得DBID
                stringBuilder.append("dbid"+cursor.getInt(cursor.getColumnIndex("dbid")));
                //获得DBID
                stringBuilder.append("num"+cursor.getInt(cursor.getColumnIndex("num")));
                //获得用户名
                stringBuilder.append("name"+cursor.getString(cursor.getColumnIndex("name")));
                //获得密码
                stringBuilder.append("time"+cursor.getString(cursor.getColumnIndex("time")));
                FLogs.i(TAG,stringBuilder.toString());
            }
        }
    }
}
