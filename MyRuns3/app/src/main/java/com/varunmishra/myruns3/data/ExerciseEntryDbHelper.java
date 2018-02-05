package com.varunmishra.myruns3.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class ExerciseEntryDbHelper extends SQLiteOpenHelper {
	// Database name string
	public static final String DATABASE_NAME = "MyRunsDB";
	// Table name string. (Only one table)
	private static final String TABLE_NAME_ENTRIES = "ENTRIES";

	// Version code
	private static final int DATABASE_VERSION = 1;

	// Table schema, column names
	public static final String KEY_ROWID = "_id";
	public static final String KEY_INPUT_TYPE = "input_type";
	public static final String KEY_ACTIVITY_TYPE = "activity_type";
	public static final String KEY_DATE_TIME = "date_time";
	public static final String KEY_DURATION = "duration";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_AVG_PACE = "avg_pace";
	public static final String KEY_AVG_SPEED = "avg_speed";
	public static final String KEY_CALORIES = "calories";
	public static final String KEY_CLIMB = "climb";
	public static final String KEY_HEARTRATE = "heartrate";
	public static final String KEY_COMMENT = "comment";
	public static final String KEY_PRIVACY = "privacy";
	public static final String KEY_GPS_DATA = "gps_data";

	// SQL query to create the table for the first time
	// Data types are defined below
	private static final String CREATE_TABLE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME_ENTRIES
			+ " ("
			+ KEY_ROWID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_INPUT_TYPE
			+ " INTEGER NOT NULL, "
			+ KEY_ACTIVITY_TYPE
			+ " INTEGER NOT NULL, "
			+ KEY_DATE_TIME
			+ " DATETIME NOT NULL, "
			+ KEY_DURATION
			+ " INTEGER NOT NULL, "
			+ KEY_DISTANCE
			+ " FLOAT, "
			+ KEY_AVG_PACE
			+ " FLOAT, "
			+ KEY_AVG_SPEED
			+ " FLOAT,"
			+ KEY_CALORIES
			+ " INTEGER, "
			+ KEY_CLIMB
			+ " FLOAT, "
			+ KEY_HEARTRATE
			+ " INTEGER, "
			+ KEY_COMMENT
			+ " TEXT, "
			+ KEY_PRIVACY
			+ " INTEGER, " + KEY_GPS_DATA + " BLOB " + ");";

	private static final String[] mColumnList = new String[] { KEY_ROWID,
			KEY_INPUT_TYPE, KEY_ACTIVITY_TYPE, KEY_DATE_TIME, KEY_DURATION,
			KEY_DISTANCE, KEY_AVG_PACE, KEY_AVG_SPEED, KEY_CALORIES, KEY_CLIMB,
			KEY_HEARTRATE, KEY_COMMENT, KEY_PRIVACY, KEY_GPS_DATA };

	public ExerciseEntryDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create the table schema.
		db.execSQL(CREATE_TABLE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// we don't need to do anything in here right now.
	}

	// Insert a item given each column value
	public long insertEntry(ExerciseEntry entry) {

		ContentValues value = new ContentValues();
		// put values in a ContentValues object
		value.put(KEY_INPUT_TYPE, entry.getInputType());
		value.put(KEY_ACTIVITY_TYPE, entry.getActivityType());
		value.put(KEY_DATE_TIME, entry.getDateTimeInMillis());
		value.put(KEY_DURATION, entry.getDuration());
		value.put(KEY_DISTANCE, entry.getDistance());
		value.put(KEY_CALORIES, entry.getCalorie());
		value.put(KEY_HEARTRATE, entry.getHeartrate());
		value.put(KEY_AVG_SPEED, entry.getAvgSpeed());
		value.put(KEY_COMMENT, entry.getComment());

		// get a database object
		SQLiteDatabase dbObj = getWritableDatabase();
		// insert the record
		long id = dbObj.insert(TABLE_NAME_ENTRIES, null, value);
		// close the database
		dbObj.close();

		// return the primary key for the new record
		return id;
	}

	// Remove a entry by giving its index
	public void removeEntry(long rowIndex) {
		SQLiteDatabase dbObj = getWritableDatabase();
		dbObj.delete(TABLE_NAME_ENTRIES, KEY_ROWID + "=" + rowIndex, null);
		dbObj.close();
	}

	// Query a specific entry by its index. Return a cursor having each column
	// value
	public ExerciseEntry fetchEntryByIndex(long rowId) throws SQLException {
		SQLiteDatabase dbObj = getReadableDatabase();
		ExerciseEntry entry = null;
		// do the query with the condition KEY_ROWID = rowId
		Cursor cursor = dbObj.query(true, TABLE_NAME_ENTRIES, mColumnList,
				KEY_ROWID + "=" + rowId, null, null, null, null, null);

		// move the cursor to the first record
		if (cursor.moveToFirst()) {
			// convert the cursor to an ExerciseEntry object
			entry = cursorToEntry(cursor, true);
		}

		// close the cursor
		cursor.close();
		dbObj.close();

		return entry;
	}

	// Query the entire table, return all rows
	public ArrayList<ExerciseEntry> fetchEntries() {
		SQLiteDatabase dbObj = getReadableDatabase();
		// store all the entries to an ArrayList
		ArrayList<ExerciseEntry> entryList = new ArrayList<ExerciseEntry>();
		// do the query without any condition. it retrieves every record from
		// the database
		Cursor cursor = dbObj.query(TABLE_NAME_ENTRIES, mColumnList, null,
				null, null, null, null);

		// the cursor initially points the record PRIOR to the first record
		// use the while loop to read every record from the cursor
		while (cursor.moveToNext()) {
			ExerciseEntry entry = cursorToEntry(cursor, false);
			entryList.add(entry);
			Log.d("TAGG", "Got data");
		}

		cursor.close();
		dbObj.close();

		return entryList;
	}

	// convert the a row in the cursor to an ExerciseEntry object
	private ExerciseEntry cursorToEntry(Cursor cursor, boolean needGps) {
		ExerciseEntry entry = new ExerciseEntry();
		entry.setId(cursor.getLong(cursor.getColumnIndex(KEY_ROWID)));
		entry.setInputType(cursor.getInt(cursor.getColumnIndex(KEY_INPUT_TYPE)));
		entry.setActivityType(cursor.getInt(cursor
				.getColumnIndex(KEY_ACTIVITY_TYPE)));
		entry.setDateTime(cursor.getLong(cursor.getColumnIndex(KEY_DATE_TIME)));
		entry.setDuration(cursor.getInt(cursor.getColumnIndex(KEY_DURATION)));
		entry.setDistance(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE)));
		entry.setAvgPace(cursor.getDouble(cursor.getColumnIndex(KEY_AVG_PACE)));
		entry.setAvgSpeed(cursor.getDouble(cursor.getColumnIndex(KEY_AVG_SPEED)));
		entry.setCalorie(cursor.getInt(cursor.getColumnIndex(KEY_CALORIES)));
		entry.setClimb(cursor.getDouble(cursor.getColumnIndex(KEY_CLIMB)));
		entry.setHeartrate(cursor.getInt(cursor.getColumnIndex(KEY_HEARTRATE)));
		entry.setComment(cursor.getString(cursor.getColumnIndex(KEY_COMMENT)));

		return entry;
	}
}
