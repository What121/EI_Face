package com.bestom.ei_library.core.manager.DB;

import android.database.sqlite.SQLiteDatabase;

public interface OnSqliteUpdateListener {
    public void onSqliteUpdateListener(SQLiteDatabase db, int oldVersion, int newVersion);
}
