package com.example.soulcompass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;


import androidx.core.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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


    /**
     * Return a list of (date,result) pairs for each test in the database in the given scale
     * @param scale
     */
    public List<Pair<String, Integer>> loadTestResults(Integer scale){

        // 1. Init
        List<Pair<String, Integer>> test_results = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        // 2. Query the database
        String[] query_columns = {KEY_RESULT, KEY_DAY, KEY_SCALE};
        String where_clause = KEY_SCALE+"=?";
        String[] where_args = new String[] {String.valueOf(scale)};
        Cursor cursor = database.query(
                TEST_TABLE_NAME,
                query_columns,
                where_clause,
                where_args,
                null,
                null,
                null);

        // 3. Iterate over the query results
        cursor.moveToFirst();
        Log.d("", String.valueOf(cursor));
        for (int index=0; index < cursor.getCount(); index++){
            Integer result = cursor.getInt(cursor.getColumnIndex(KEY_RESULT));
            String day = cursor.getString(cursor.getColumnIndex(KEY_DAY));
            String scale_str = cursor.getString(cursor.getColumnIndex(KEY_SCALE));
            Log.d("", result + " " + day + " " + scale_str);

            Pair<String, Integer> test = new Pair<>(day, result);
            test_results.add(test);

            cursor.moveToNext();
        }

        // 4. Close database helper and return loaded results
        cursor.close();
        database.close();

        return test_results;
    }

    /**
     * Utility function to delete all records in the test table
     */
    public void deleteRecords(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TEST_TABLE_NAME, null, null);
        database.close();
    }
}
