package com.varunmishra.myruns4;


public abstract class Globals {


	public static final String TAG = "MyRuns";

	// Const for distance/time conversions
	public static final double KM2MILE_RATIO = 1.609344;
	public static final double KILO = 1000;
	public static final int SECONDS_PER_HOUR = 3600;
	public static final int DEFAULT_MAP_ZOOM_LEVEL = 17;

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

	// Int encoded activity types
	// All activity types in a string array.
	// "Standing" is not an exercise type... it's there for activity recognition
	// result
	// There was a debate about using "Standing" or "Still". The authority
	// decided using
	// "Standing"...
	public static final String[] ACTIVITY_TYPES = { "Running", "Walking",
			"Standing", "Cycling", "Hiking", "Downhill Skiing",
			"Cross-Country Skiing", "Snowboarding", "Skating", "Swimming",
			"Mountain Biking", "Wheelchair", "Elliptical", "Other" };

	public static final String[] INPUT_TYPES = { "Manual Entry", "GPS", "Automatic" };



	public static final int ACTIVITY_TYPE_ERROR = -1;

	// Strings for Tabs
	public static final String UI_TAB_START = "START";
	public static final String UI_TAB_HISTORY = "HISTORY";
	public static final String UI_TAB_SETTINGS = "SETTINGS";


	// Consts for input types.
	public static final int INPUT_TYPE_ERROR = -1;
	public static final int INPUT_TYPE_MANUAL = 0;
	public static final int INPUT_TYPE_GPS = 1;
	public static final int INPUT_TYPE_AUTOMATIC = 2;

	// Consts for task types
	public static final String KEY_TASK_TYPE = "TASK_TYPE";
	public static final int TASK_TYPE_ERROR = -1;
	public static final int TASK_TYPE_NEW = 1;
	public static final int TASK_TYPE_HISTORY = 2;

	// Consts for tab positions
	public static final int TAB_START = 0;
	public static final int TAB_HISTORY = 1;
	public static final int TAB_SETTINGS = 2;
}