package com.example.soulcompass;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    private ScreenUnlockSensor screenUnlockSensor;
    private Sensor mSensorACC;
    private SensorManager mSensorManager;
    private SensorEventListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar header_bar = findViewById(R.id.toolbar);
        setSupportActionBar(header_bar);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        screenUnlockSensor = new ScreenUnlockSensor(this, SoulCompassDatabase::insertUnlockEvent);
        //this.deleteDatabase(SoulCompassDatabase.DATABASE_NAME);
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensorACC = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        listener = new StepCounterListener(this, null);
        if (mSensorACC != null) {

            // Register the ACC listener
            mSensorManager.registerListener(listener, mSensorACC, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("","Acc registered!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        Drawable drawable = menu.findItem(R.id.info).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.white));
        menu.findItem(R.id.info).setIcon(drawable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.info) {
            Intent settings_intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settings_intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}


// Sensor event listener
class StepCounterListener<stepsCompleted> implements SensorEventListener {

    Context context;
    private long lastUpdate = 0;

    // ACC Step counter
    // public int mACCStepCounter = 0;

    public int mACCStepCounter = 0;


    ArrayList<Integer> mACCSeries = new ArrayList<Integer>();
    ArrayList<String> mTimeSeries = new ArrayList<String>();

    private double accMag = 0d;
    private int lastXPoint = 1;
    int stepThreshold = 10;

    // Android step detector
    public int mAndroidStepCounter = 0;



    //
    public String timestamp;
    public String day;
    public String hour;

    // Get the database, TextView and ProgressBar as args
    public StepCounterListener(Context context, SQLiteDatabase db){
        this.context = context;
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        mACCStepCounter = SoulCompassDatabase.loadSingleRecord(context, fDate);
        Log.d("STEPS","Step counter init to " + mACCStepCounter);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {

            // Case of the ACC
            case Sensor.TYPE_LINEAR_ACCELERATION:

                // Get x,y,z
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                //////////////////////////// -- PRINT ACC VALUES -- ////////////////////////////////////
                // Timestamp
                long timeInMillis = System.currentTimeMillis() + (event.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000;

                // Convert the timestamp to date
                SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                jdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String date = jdf.format(timeInMillis);

                /*
                // print a value every 1000 ms
                long curTime = System.currentTimeMillis();
                if ((curTime - lastUpdate) > 1000) {
                    lastUpdate = curTime;

                    Log.d("ACC", "X: " + String.valueOf(x) + " Y: " + String.valueOf(y) + " Z: "
                            + String.valueOf(z) + " t: " + String.valueOf(date));

                }
                */

                // Get the date, the day and the hour
                timestamp = date;
                day = date.substring(0,10);
                hour = date.substring(11,13);

                ////////////////////////////////////////////////////////////////////////////////////////

                /// STEP COUNTER ACC ////
                accMag = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

                //Update the Magnitude series
                mACCSeries.add((int) accMag);

                //Update the time series
                mTimeSeries.add(timestamp);


                // Calculate ACC peaks and steps
                peakDetection();

                break;

            // case Step detector
            case Sensor.TYPE_STEP_DETECTOR:

                // Calculate the number of steps
                countSteps(event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }


    public void peakDetection() {
        int windowSize = 20;

        /* Peak detection algorithm derived from: A Step Counter Service for Java-Enabled Devices Using a Built-In Accelerometer, Mladenov et al.
         */
        int highestValX = mACCSeries.size(); // get the length of the series
        if (highestValX - lastXPoint < windowSize) { // if the segment is smaller than the processing window skip it
            return;
        }

        List<Integer> valuesInWindow = mACCSeries.subList(lastXPoint,highestValX);
        List<String> timesInWindow = mTimeSeries.subList(lastXPoint,highestValX);

        lastXPoint = highestValX;

        int forwardSlope = 0;
        int downwardSlope = 0;

        List<Integer> dataPointList = new ArrayList<Integer>();
        List<String> timePointList = new ArrayList<String>();


        for (int p =0; p < valuesInWindow.size(); p++){
            dataPointList.add(valuesInWindow.get(p)); // ACC Magnitude data points
            timePointList.add(timesInWindow.get(p)); // Timestamps
        }

        for (int i = 0; i < dataPointList.size(); i++) {
            if (i == 0) {
            }
            else if (i < dataPointList.size() - 1) {
                forwardSlope = dataPointList.get(i + 1) - dataPointList.get(i);
                downwardSlope = dataPointList.get(i)- dataPointList.get(i - 1);

                if (forwardSlope < 0 && downwardSlope > 0 && dataPointList.get(i) > stepThreshold ) {

                    // Update the number of steps
                    mACCStepCounter += 1;
                    Log.d("ACC STEPS: ", String.valueOf(mACCStepCounter));


                    //Insert the data in the database
                    SoulCompassDatabase databaseOpenHelper =   new SoulCompassDatabase(context);;
                    SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(SoulCompassDatabase.KEY_TIMESTAMP, timePointList.get(i));
                    values.put(SoulCompassDatabase.KEY_DAY, day);
                    values.put(SoulCompassDatabase.KEY_HOUR, hour);
                    database.insert(SoulCompassDatabase.TABLE_NAME, null, values);
                    database.close();
                }

            }
        }
    }


    // Calculate the number of steps from the step detector
    private void countSteps(float step) {

        //Step count
        mAndroidStepCounter += (int) step;
        Log.d("NUM STEPS ANDROID", "Num.steps: " + String.valueOf(mAndroidStepCounter));
    }

}

