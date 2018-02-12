package com.varunmishra.myruns4;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.varunmishra.myruns4.data.ExerciseEntryDbHelper;


// Display the details of a "manual" entry.
// All data are passed from the launching activity. Another way
// of doing it is only passing the entry id, and query the database in this activity.
// More work, but making the activity more self-contained.
public class DisplayEntryActivity extends Activity {
	
	private static final int MENU_ID_DELETE = 0;
	@SuppressWarnings("unused")
	private static final int MENU_ID_UPDATE = 1;  
	// !! Extra credit if you can modify and update the database
	
	private long mEntryID;
	// private long mId = 0;
	// private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_entry);
		//mContext = this;
		
		Bundle extras = getIntent().getExtras();
		
		if ( extras != null){
			mEntryID = extras.getLong(Globals.KEY_ROWID);
			((EditText) findViewById(R.id.editDispActivityType)).setText(extras.getString(Globals.KEY_ACTIVITY_TYPE));
			((EditText) findViewById(R.id.editDispDateTime)).setText(extras.getString(Globals.KEY_DATE_TIME));
			((EditText) findViewById(R.id.editDispDuration)).setText(extras.getString(Globals.KEY_DURATION));
			((EditText) findViewById(R.id.editDispDistance)).setText(extras.getString(Globals.KEY_DISTANCE));
			((EditText) findViewById(R.id.editDispCalories)).setText(extras.getString(Globals.KEY_CALORIES));
			((EditText) findViewById(R.id.editDispHearrate)).setText(extras.getString(Globals.KEY_HEARTRATE));
			((EditText) findViewById(R.id.editDispInputType)).setText(extras.getString(Globals.KEY_INPUT_TYPE));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		MenuItem menuitem;
		menuitem = menu.add(Menu.NONE, MENU_ID_DELETE, MENU_ID_DELETE, "Delete");
		menuitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		// For extra credit
		// menu.add(Menu.NONE, MENU_ID_UPDATE, MENU_ID_UPDATE, "Update"); 
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ID_DELETE:
			ExerciseEntryDbHelper exerciseEntryDbHelper = new ExerciseEntryDbHelper(this);
			exerciseEntryDbHelper.removeEntry(mEntryID);
			
			finish();
			return true;

		default:
			finish();
			return false;
		}
	}
}
