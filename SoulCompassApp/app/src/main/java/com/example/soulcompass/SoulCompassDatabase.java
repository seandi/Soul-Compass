package com.example.soulcompass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


import androidx.core.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class SoulCompassDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SoulCompass";


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


    public static final String TABLE_NAME = "num_steps";
    public static final String KEY_ID = "id";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_HOUR = "hour";
    public static final String KEY_DAY = "day";

    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY, " + KEY_DAY + " TEXT, " + KEY_HOUR + " TEXT, "
            + KEY_TIMESTAMP + " TEXT);";



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
        db.execSQL(CREATE_TABLE_SQL);
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
    public Map<String, Integer> loadTestResults(Integer scale){

        // 1. Init
        HashMap<String, Integer> test_results = new HashMap<>();
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

            test_results.put(day, result);

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

    public static Map<String, Integer> loadUnlocksByDay(Context context){
        // 1. Define the map to store the date and number of unlocks
        Map<String, Integer>  map = new TreeMap<>();

        // 2. Get the readable database
        SoulCompassDatabase database_helper = new SoulCompassDatabase(context);
        SQLiteDatabase database = database_helper.getReadableDatabase();

        // 3. Define the query to get the data
        Cursor cursor = database.rawQuery("SELECT " + UNLOCK_KEY_DATE + ", COUNT(*)  FROM " + UNLOCK_TABLE_NAME  +
                " GROUP BY " + UNLOCK_KEY_DATE + " ORDER BY " + UNLOCK_KEY_DATE + " ASC ", new String [] {});

        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            String tmpKey = cursor.getString(0);
            Integer tmpValue = Integer.parseInt(cursor.getString(1));

            // Put the data from the database into the map
            map.put(tmpKey, tmpValue);
            cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }




    /**
     * Utility function to load all records in the database
     *
     * @param context: application context
     */
    public static void loadRecords(Context context){
        List<String> dates = new LinkedList<String>();
        SoulCompassDatabase databaseHelper = new SoulCompassDatabase(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String [] columns = new String [] {KEY_TIMESTAMP};
        Cursor cursor = database.query(TABLE_NAME, columns, null, null, KEY_TIMESTAMP,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            dates.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Log.d("STORED TIMESTAMPS: ", String.valueOf(dates));
    }

    /**
     * Utility function to delete all records from the data base
     *
     * @param context: application context
     */
    public static void deleteRecords(Context context){
        SoulCompassDatabase databaseHelper = new SoulCompassDatabase(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int numberDeletedRecords =0;

        numberDeletedRecords = database.delete(TABLE_NAME, null, null);
        database.close();

        // display the number of deleted records with a Toast message
        Toast.makeText(context,"Deleted " + String.valueOf(numberDeletedRecords) + " steps",Toast.LENGTH_LONG).show();
    }

    /**
     * Utility function to load records from a single day
     *
     * @param context: application context
     * @param date: today's date
     * @return numSteps: an integer value with the number of records in the database
     */
    //
    public static Integer loadSingleRecord(Context context, String date){
        List<String> steps = new LinkedList<String>();
        // Get the readable database
        SoulCompassDatabase databaseHelper = new SoulCompassDatabase(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String where = KEY_DAY + " = ?";
        String [] whereArgs = { date };

        Cursor cursor = database.query(TABLE_NAME, null, where, whereArgs, null,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            steps.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Integer numSteps = steps.size();
        Log.d("STORED STEPS TODAY: ", String.valueOf(numSteps));
        return numSteps;
    }

    /**
     * Utility function to get the number of steps by hour for current date
     *
     * @param context: application context
     * @param date: today's date
     * @return map: map with key-value pairs hour->number of steps
     */
    //
    public static Map<Integer, Integer> loadStepsByHour(Context context, String date){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<Integer, Integer>  map = new HashMap<> ();

        // 2. Get the readable database
        SoulCompassDatabase databaseHelper = new SoulCompassDatabase(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // 3. Define the query to get the data
        Cursor cursor = database.rawQuery("SELECT hour, COUNT(*)  FROM num_steps " +
                "WHERE day = ? GROUP BY hour ORDER BY  hour ASC ", new String [] {date});

        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            Integer tmpKey = Integer.parseInt(cursor.getString(0));
            Integer tmpValue = Integer.parseInt(cursor.getString(1));

            //2. Put the data from the database into the map
            map.put(tmpKey, tmpValue);


            cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }

    /**
     * Utility function to get the number of steps by day
     *
     * @param context: application context
     * @return map: map with key-value pairs hour->number of steps
     */
    //
    public static Map<String, Integer> loadStepsByDay(Context context){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<String, Integer>  map = new TreeMap<>();

        // 2. Get the readable database
        SoulCompassDatabase databaseHelper = new SoulCompassDatabase(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // 3. Define the query to get the data
        Cursor cursor = database.rawQuery("SELECT day, COUNT(*)  FROM num_steps " +
                "GROUP BY day ORDER BY day ASC ", new String [] {});

        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            String tmpKey = cursor.getString(0);
            Integer tmpValue = Integer.parseInt(cursor.getString(1));

            // Put the data from the database into the map
            map.put(tmpKey, tmpValue);
            cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }





}
