package com.mujoko.goldinfo;

import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class GoldApplication extends Application implements
    OnSharedPreferenceChangeListener { // <1>
  private static final String TAG = GoldApplication.class.getSimpleName();
  private SharedPreferences prefs;
  private boolean serviceRunning;
  

  private StatusData statusData; // <1>
  
  @Override
  public void onCreate() { // <3>
    super.onCreate();
    this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    this.prefs.registerOnSharedPreferenceChangeListener(this);
    statusData=new StatusData(this);
    Log.i(TAG, "onCreated");
  }

  @Override
  public void onTerminate() { // <4>
    super.onTerminate();
    Log.i(TAG, "onTerminated");
  }

  public synchronized void onSharedPreferenceChanged(
      SharedPreferences sharedPreferences, String key) { // <6>
  }


  public StatusData getStatusData() { // <2>
    return statusData;
  }

  public boolean isServiceRunning() {
    return serviceRunning;
  }

  public void setServiceRunning(boolean serviceRunning) {
    this.serviceRunning = serviceRunning;
  }

  public long getDelay(){
	  String frequent = this.prefs.getString("frequent", "60000");
	  if (!TextUtils.isEmpty(frequent) ) {
		  long value =Long.parseLong(frequent);
		  
		  return (value);
		  //    this.twitter = new Twitter(username, password);
		  //    this.twitter.setAPIRootUrl(url);
	  }
	  return 60000;//a minutes by default
  }
  int count = 0;
  // Connects to the online service and puts the latest statuses into DB.
  // Returns the count of new statuses
  public synchronized int fetchStatusUpdates() {
    Log.d(TAG, "Fetching status updates");
    try {
      
      int id= (int) (Math.random()*1000);
      ContentValues values = new ContentValues();
//      for (Status status : statusUpdates) {

      count++;
        values.put(StatusData.C_ID, id);
        long createdAt = System.currentTimeMillis();
        values.put(StatusData.C_CREATED_AT, createdAt);
        values.put(StatusData.C_PROVIDER, "CIMB");
        values.put(StatusData.C_BUY_RATE, count+"");
        values.put(StatusData.C_SELL_RATE, count+"");

        long latestStatusCreatedAtTime = this.getStatusData()
        		.getLatestStatusCreatedAtTime();
        id= (int) (Math.random()*1000);
        count++;
        this.getStatusData().insertOrIgnore(values);
        values.put(StatusData.C_ID, id);
        values.put(StatusData.C_CREATED_AT, createdAt);
        values.put(StatusData.C_PROVIDER, "Maybank");
        values.put(StatusData.C_BUY_RATE, count+"");
        values.put(StatusData.C_SELL_RATE, count+"");
        this.getStatusData().insertOrIgnore(values);

                Log.d(TAG, "Got update with id " + count + ". Saving");
        if (latestStatusCreatedAtTime < createdAt) {
          count++;
        }
      Log.d(TAG, count > 0 ? "Got " + count + " status updates"
          : "No new status updates");
      return count;
    } catch (RuntimeException e) {
      Log.e(TAG, "Failed to fetch status updates", e);
      return 0;
    }
  }

  

}
