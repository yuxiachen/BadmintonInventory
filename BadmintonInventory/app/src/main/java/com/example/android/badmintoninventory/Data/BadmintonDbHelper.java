package com.example.android.badmintoninventory.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.badmintoninventory.Data.BadmintonContract.BadmintonEntry;


/**
 * Created by yuxia on 10/27/16.
 */

public class BadmintonDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "badminton.db";
    public BadmintonDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BADMINTON_TABLE = "CREATE TABLE " + BadmintonEntry.TABLE_NAME + "(" +
                BadmintonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BadmintonEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                BadmintonEntry.COLUMN_PRODUCT_DESCRIPTION + " TEXT, " +
                BadmintonEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL, " +
                BadmintonEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, " +
                BadmintonEntry.COLUMN_PRODUCT_MANUFACTURER + " STRING, " +
                BadmintonEntry.COLUMN_PRODUCT_IMAGE_URI + " TEXT);";
        db.execSQL(SQL_CREATE_BADMINTON_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
