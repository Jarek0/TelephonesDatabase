package com.example.dell.telephonesdatabase.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dell on 2017-04-23.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public final static int DB_VERSION = 1;
    public final static String DB_NAME = "phones_Db";
    public final static String TABLE_NAME = "phones";
    public final static String ID = "_id";
    public final static String PRODUCER_COLUMN_NAME  = "producer";
    public final static String MODEL_COLUMN_NAME = "model";
    public final static String ANDROID_VERSION_COLUMN_NAME = "android_version";
    public final static String URL_COLUMN_NAME = "url";

    public final static String TABLE_CREATING_SCRIPT = "CREATE TABLE " + TABLE_NAME +
            "("+ ID +" integer primary key autoincrement, " +
            PRODUCER_COLUMN_NAME+" text not null,"+
            MODEL_COLUMN_NAME+" text not null,"+
            ANDROID_VERSION_COLUMN_NAME+" text not null,"+
            URL_COLUMN_NAME+" text not null);";
    private static final String TABLE_DELETING_SCRIPT = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public DatabaseHelper(Context context)  {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATING_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
