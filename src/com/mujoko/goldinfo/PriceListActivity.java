package com.mujoko.goldinfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class PriceListActivity extends Activity {
	static final String SEND_TIMELINE_NOTIFICATIONS = "com.mujoko.goldinfo.SEND_TIMELINE_NOTIFICATIONS";
	
	static final String TAG = "PriceListActivity"; // <1>
	static final String[] from = { StatusData.C_CREATED_AT,
			StatusData.C_PROVIDER, StatusData.C_BUY_RATE }; // <2>
	static final int[] to = { R.id.textCreatedAt, R.id.textProvider,
			R.id.textRate }; // <3>

	private PriceStatusReceiver receiver;
	private IntentFilter filter;

	private GoldApplication app; // <2>

	SQLiteDatabase db;
	Cursor cursor; // <1>
	ListView listTimeline; // <2>
	PriceAdapter adapter; // <3

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		this.app = (GoldApplication) getApplication(); // <2>
		// Find your views
		listTimeline = (ListView) findViewById(R.id.listTimeline); // <4>

		receiver = new PriceStatusReceiver();
		filter = new IntentFilter(UpdaterService.NEW_STATUS_INTENT);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_price_list, menu);
		return true;
	}
	

	  @Override
	  protected void onPause() {
	    super.onPause();

	    // UNregister the receiver
	    unregisterReceiver(receiver); 
	  }


	public void onDestroy() {
		super.onDestroy();
		// Close the database
		app.getStatusData().close(); // <4>
	}

	@Override
	protected void onResume() { // <5>
		super.onResume();
		this.setupList();
	    // Register the receiver
	    super.registerReceiver(receiver, filter,
	        SEND_TIMELINE_NOTIFICATIONS, null);
	}

	
	
	// Called when an options item is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		 case R.id.itemPrefs:
		      startActivity(new Intent(this, PrefsActivity.class)
		          .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		      break;
		case R.id.itemServiceStart:
			Log.d(TAG, "itemServiceStart");
			if (!this.app.isServiceRunning()){
				startService(new Intent(this, UpdaterService.class));
			} else {
				Toast.makeText(this, "Services is already running", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.itemServiceStop:
			Log.d(TAG, "itemServiceStop");
			stopService(new Intent(this, UpdaterService.class));
			break;
		}

		return true;
	}

	// Responsible for fetching data and setting up the list and the adapter
	private void setupList() { // <5>
		// Get the data
//		GoldApplication app = (GoldApplication) super.getApplication();
		cursor = app.getStatusData().getStatusUpdates();
		startManagingCursor(cursor);
		// Setup Adapter
		adapter = new PriceAdapter(this, cursor); // <2>
		listTimeline.setAdapter(adapter); // <3>
	}

	// Receiver to wake up when UpdaterService gets a new status
	// It refreshes the timeline list by requerying the cursor
	class PriceStatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			setupList();
			Log.d("PriceStatusReceiver", "onReceived");
		}
	}

}
