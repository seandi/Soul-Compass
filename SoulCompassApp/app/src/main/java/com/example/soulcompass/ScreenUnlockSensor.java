package com.example.soulcompass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScreenUnlockSensor{

    private ScreenUnlockReceiver screenUnlockReceiver;

    public ScreenUnlockSensor(Context context){
       screenUnlockReceiver = new ScreenUnlockReceiver();

       IntentFilter screenLockUnlockFilter = new IntentFilter();
       screenLockUnlockFilter.addAction(Intent.ACTION_SCREEN_OFF);
       screenLockUnlockFilter.addAction(Intent.ACTION_SCREEN_ON);
       context.registerReceiver(screenUnlockReceiver, screenLockUnlockFilter);
   }

   public ScreenUnlockSensor(Context context, Runnable unlock_callback){
       screenUnlockReceiver = new ScreenUnlockReceiver(unlock_callback);

       IntentFilter screenLockUnlockFilter = new IntentFilter();
       screenLockUnlockFilter.addAction(Intent.ACTION_SCREEN_OFF);
       screenLockUnlockFilter.addAction(Intent.ACTION_SCREEN_ON);
       context.registerReceiver(screenUnlockReceiver, screenLockUnlockFilter);
   }






}

class ScreenUnlockReceiver extends BroadcastReceiver{

    private static boolean screen_on = true;
    private static Integer screen_unlocks = 0;
    private Runnable unlock_callback = null;

    ScreenUnlockReceiver(){
        super();
    }

    ScreenUnlockReceiver(Runnable unlock_callback){
        super();
        this.unlock_callback = unlock_callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            screen_on = false;
            Log.d("UNLOCK SENSOR", "Screen locked");
        }

        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            if(!screen_on){
                screen_on = true;
                screen_unlocks+=1;
                Log.d("UNLOCK SENSOR", "Screen unlocked. Total unlocks: " + screen_unlocks);
                if(unlock_callback != null) unlock_callback.run();
            }
        }

    }

    public static Integer getNumberOfUnlocks(){
        return screen_unlocks;
    }
}
