package com.mujoko.goldinfo;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class PriceAdapter extends SimpleCursorAdapter { // <1>
  static final String[] from = { StatusData.C_CREATED_AT, StatusData.C_PROVIDER,
	  StatusData.C_BUY_RATE }; // <2>
  static final int[] to = { R.id.textCreatedAt, R.id.textProvider, R.id.textRate}; // <3>

  // Constructor
  public PriceAdapter(Context context, Cursor c) { // <4>
	  super(context, R.layout.row, c, from, to);
//	  this.
//    super(context, R.layout.row, c, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
  }

  // This is where the actual binding of a cursor to view happens
  @Override
  public void bindView(View row, Context context, Cursor cursor) { // <5>
    super.bindView(row, context, cursor);
    // Manually bind created at timestamp to its view
    long timestamp = cursor.getLong(cursor
        .getColumnIndex(StatusData.C_CREATED_AT)); // <6>
    TextView textCreatedAt = (TextView) row.findViewById(R.id.textCreatedAt); // <7>
    textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp)); // <8>
  }

}
