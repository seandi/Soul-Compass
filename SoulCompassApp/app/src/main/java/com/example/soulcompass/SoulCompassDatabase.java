package com.example.soulcompass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import androidx.core.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SoulCompassDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SoulCompass";


    // --------- TEST TABLE ------------------------------ //
    public static final String TEST_TABLE_NAME = "stress_test_results";
    public static final String TEST_KEY_ID = "id";
    public static final String TEST_KEY_RESULT = "result";
    public static final String TEST_KEY_DAY = "day";
    public static final String TEST_KEY_SCALE = "scale";

    public static final String CREATE_TEST_TABLE_SQL = "CREATE TABLE " + TEST_TABLE_NAME + " (" +
            TEST_KEY_ID + " INTEGER PRIMARY KEY, " + TEST_KEY_RESULT + " INTEGER, " + TEST_KEY_DAY + " TEXT, " +
            TEST_KEY_SCALE + " INTEGER);";

    // --------- UNLOCK TABLE ------------------------------ //
    public static final String UNLOCK_TABLE_NAME = "unlocks_number";
    public static final String UNLOCK_KEY_ID = "id_unlock";
    public static final String UNLOCK_KEY_DATE = "date_unlock";

    public static final String CREATE_UNLOCK_TABLE_SQL = "CREATE TABLE " + UNLOCK_TABLE_NAME + " (" +
            UNLOCK_KEY_ID + " INTEGER PRIMARY KEY, " + UNLOCK_KEY_DATE + " TEXT);";


    private SimpleDateFormat dateFormat;

    public SoulCompassDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dateFormat= new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));

        //context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TEST_TABLE_SQL);
        db.execSQL(CREATE_UNLOCK_TABLE_SQL);
        Log.d("DATABASE", "Databases created!");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ;
    }

    public void insertTestResult(Integer result, Integer scale){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put(TEST_KEY_RESULT, result);
        String date = dateFormat.format(System.currentTimeMillis());
        row.put(TEST_KEY_DAY, date);
        row.put(TEST_KEY_SCALE, scale);

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
        String[] query_columns = {TEST_KEY_RESULT, TEST_KEY_DAY, TEST_KEY_SCALE};
        String where_clause = TEST_KEY_SCALE+"=?";
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
            Integer result = cursor.getInt(cursor.getColumnIndex(TEST_KEY_RESULT));
            String day = cursor.getString(cursor.getColumnIndex(TEST_KEY_DAY));
            String scale_str = cursor.getString(cursor.getColumnIndex(TEST_KEY_SCALE));
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


    public static void insertUnlockEvent(Context context){
        SoulCompassDatabase database_helper = new SoulCompassDatabase(context);
        SQLiteDatabase database = database_helper.getWritableDatabase();

        ContentValues row = new ContentValues();
        String date = database_helper.dateFormat.format(System.currentTimeMillis());
        row.put(UNLOCK_KEY_DATE, date);

        database.insert(UNLOCK_TABLE_NAME, null, row);
        Log.d("DATABASE", "Insert in UNLOCK_TABLE new unlock event in date: " + date);
        database.close();
        database_helper.close();

    }
}
