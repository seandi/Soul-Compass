package com.example.soulcompass;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class SoulCompassDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SoulCompass";

    public static final String TEST_TABLE_NAME = "stress_test_results";
    public static final String KEY_ID = "id";
    public static final String KEY_RESULT = "result";
    public static final String KEY_DAY = "day";
    public static final String KEY_SCALE = "scale";


    public static final String CREATE_TEST_TABLE_SQL = "CREATE TABLE " + TEST_TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY, " + KEY_RESULT + " INTEGER, " +  KEY_DAY + " TEXT, " +
            KEY_SCALE + " INTEGER);";


    private SimpleDateFormat dateFormat;

    public SoulCompassDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dateFormat= new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TEST_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ;
    }

    public void insertTestResult(Integer result, Integer scale){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put(KEY_RESULT, result);
        String date = dateFormat.format(System.currentTimeMillis());
        row.put(KEY_DAY, date);
        row.put(KEY_SCALE, scale);

        database.insert(TEST_TABLE_NAME, null, row);
        Log.d("DATABASE", "Insert in TEST_TABLE row: " + String.valueOf(row));
        database.close();

    }
}
