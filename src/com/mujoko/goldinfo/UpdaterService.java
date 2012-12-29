package com.mujoko.goldinfo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	static final String TAG = "UpdaterService"; // <1>
	public static final String NEW_STATUS_INTENT = "com.mujoko.goldinfo.NEW_STATUS";
	  public static final String NEW_STATUS_EXTRA_COUNT = "NEW_STATUS_EXTRA_COUNT";
	  
//	static final int DELAY = 60000; // wait a minute
	private boolean runFlag = false;
	private Updater updater;
	GoldApplication application;
	@Override
	public IBinder onBind(Intent intent) { // <2>
		return null;
	}

	@Override
	public void onCreate() { // <3>
		super.onCreate();
		this.application = (GoldApplication) getApplication(); // <2>
		this.updater = new Updater();
		Log.d(TAG, "onCreated");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { // <4>
		super.onStartCommand(intent, flags, startId);

		this.runFlag = true;
		try {
			this.updater.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.application.setServiceRunning(true); // <3>

		Log.d(TAG, "onStarted");
		return START_STICKY;
	}

	@Override
	public void onDestroy() { // <5>
		super.onDestroy();
		this.runFlag = false;
		try {
		this.updater.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.updater = null;
		this.application.setServiceRunning(false); // <4>

		Log.d(TAG, "onDestroyed");
	}


	/**
	 * Thread that performs the actual update from the online service
	 */
	private class Updater extends Thread {
	    static final String RECEIVE_TIMELINE_NOTIFICATIONS = "com.mujoko.goldinfo.RECEIVE_TIMELINE_NOTIFICATIONS";
	    Intent intent;

		public Updater() {
			super("UpdaterService-Updater");
		}

		@Override
		public void run() {
			UpdaterService updaterService = UpdaterService.this;
			while (updaterService.runFlag) {
				Log.d(TAG, "Updater running");
				try {
					Log.d(TAG, "Running background thread");
					GoldApplication goldApp = (GoldApplication) updaterService
							.getApplication();
					int newUpdates = goldApp.fetchStatusUpdates();
					if (newUpdates > 0) {
				              Log.d(TAG, "We have a new status");
				              intent = new Intent(NEW_STATUS_INTENT);
				              intent.putExtra(NEW_STATUS_EXTRA_COUNT, newUpdates);
				              updaterService.sendBroadcast(intent, RECEIVE_TIMELINE_NOTIFICATIONS);
						
						Log.d(TAG, "We have a Data ");
					}
					Log.d(TAG, "Updater ran");
					Thread.sleep(goldApp.getDelay());
				} catch (InterruptedException e) {
					updaterService.runFlag = false;
				}
			}
		}
	} // Updater
}
