package com.xxun.pointsystem.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by zhangweinan on 2018/12/25.
 */

public class ExpProvider extends ContentProvider {
    private static final String TAG = "ExpProvider";

    private static final String DATABASE_NAME = "XunExp.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "expexchange";

    private SQLiteDatabase mDatabase;
    private DatabaseHelper mDatabaseHelper;
    
    public static final String AUTHORITY = "com.xiaoxun.exp.provider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/expexchange");

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private Context mContext;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("Create table " + TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "exchange_type INTEGER,"
                    + "exchange_value TEXT,"
                    + "blank TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
            onCreate(db);
        }

    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri url, String selection, String[] selectionArgs) {
        mDatabase = mDatabaseHelper.getWritableDatabase();
        //getContext().getContentResolver().notifyChange(url, null, false);
        return mDatabase.delete(TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public String getType(Uri url) {
        return null;
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
        mDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues(initialValues);
        long rowId = mDatabase.insert(TABLE_NAME, null, values);
        if (rowId > 0) {
            Uri rowUri = ContentUris.appendId(CONTENT_URI.buildUpon(), rowId).build();
            getContext().getContentResolver().notifyChange(url, null, false);
            return rowUri;
        }
        throw new SQLException("zhangweinan: Failed to insert row into " + url);
    }

    @Override
    public Cursor query(Uri url, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        qb.setTables(TABLE_NAME);

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (c != null) {
            c.setNotificationUri(getContext().getContentResolver(), url);
        }
        return c;
    }

    @Override
    public int update(Uri url, ContentValues initialValues, String where, String[] whereArgs) {
        mDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues(initialValues);
        return mDatabase.update(TABLE_NAME,values,where,whereArgs);
    }
}
