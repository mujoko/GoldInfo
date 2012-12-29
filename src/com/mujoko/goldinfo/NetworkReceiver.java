package com.mujoko.goldinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver { // <1>
  public static final String TAG = "NetworkReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {

    boolean isNetworkDown = intent.getBooleanExtra(
        ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);  // <2>
    
    if (isNetworkDown) {
      Log.i(TAG, "onReceive: NOT connected, stopping UpdaterService");
      context.stopService(new Intent(context, UpdaterService.class)); // <3>
    } else {
      Log.i(TAG, "onReceive: connected, starting UpdaterService");
      context.startService(new Intent(context, UpdaterService.class)); // <4>
    }
  }

}
