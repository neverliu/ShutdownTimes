package com.megafone.ShutdownTimes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver{
    private static final String TAG = "BootReceiver";
    private static final String PACKAGENAME = "com.megafone.ShutdownTimes";

	@Override  
    public void onReceive(Context context, Intent intent) {  
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent intent3 = context.getPackageManager().getLaunchIntentForPackage(PACKAGENAME);
            intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(intent!=null)
            {
            	context.startActivity(intent3);
            }
//            Intent intent2 = new Intent(context, MainActivity.class);  
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//            context.startActivity(intent2);  
        }  
    }  
}
