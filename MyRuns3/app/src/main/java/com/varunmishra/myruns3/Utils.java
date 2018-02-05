package com.varunmishra.myruns3;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

// Handy utilities you'll need here and there
// We can give these to students

public class Utils {

	// Different format to display the information
	public static final String DATE_FORMAT = "H:mm:ss MMM d yyyy";
	public static final String DISTANCE_FORMAT = "#.##";
	public static final String MINUTES_FORMAT = "%d mins";
	public static final String SECONDS_FORMAT = "%d secs";

	// Read from preference, the unit used for displaying distance meter/feet
	public static boolean getIsMetricFromPerf(Context context) {

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);

		String[] unit_display_options = context.getResources().getStringArray(
				R.array.unit_display_value);

		String option = settings.getString(
				context.getString(R.string.preference_key_unit_display),
				unit_display_options[0]);

		String option_metric = context.getString(R.string.kilometers);

		if (option.equals(option_metric))
			return true;
		else
			return false;
	}

	// *******************************//
	// Parser utilities to read value from database and interpret into
	// human-readable string

	// From activity type 0, 1, 2 ... to string "Running", "Walking", etc.
	public static String parseActivityType(int code, Context context) {	
		if(code >= 0 && code <  Globals.ACTIVITY_TYPES.length) {
			return Globals.ACTIVITY_TYPES[code];
		}
		
		return "Unknown";
	}

	public static String parseInputType(int code, Context context) {
		if(code >= 0 && code <  Globals.INPUT_TYPES.length) {
			return Globals.INPUT_TYPES[code];
		}

		return "Unknown";
	}

	// From 1970 epoch time in seconds to something like "10/24/2012"
	public static String parseTime(long timeInMs, Context context) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeInMs);
		SimpleDateFormat dateFormat;
		dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

		return dateFormat.format(calendar.getTime());
	}

	// Round the double type distance in meters shorter.
	// (Can also do some conversion mile-kilometers stuff)
	public static String parseDistance(double distInMeters, Context context) {
		double dist;
		String unit;
		
		if(getIsMetricFromPerf(context))
		{
			dist = distInMeters / 1000.0;
			unit = context.getString(R.string.kilometers);
		}else {
			dist = distInMeters / 1000.0 / Globals.KM2MILE_RATIO;
			unit = context.getString(R.string.miles);
		}

		DecimalFormat decimalFormat = new DecimalFormat(DISTANCE_FORMAT);
		return decimalFormat.format(dist) + " " + unit;
	}

	public static String parseSpeed(double speedInMeters, Context context) {
		if(speedInMeters < 0) {
			return "n/a";
		}
		
		double speed = speedInMeters * 3600 / 1000;
		String unit;
		
		if(getIsMetricFromPerf(context))
		{
			unit = context.getString(R.string.km_per_hr);
		}else {
			speed /= Globals.KM2MILE_RATIO;
			unit = context.getString(R.string.mile_per_hr);
		}

		DecimalFormat decimalFormat = new DecimalFormat(DISTANCE_FORMAT);
		return decimalFormat.format(speed) + " " + unit;
	}
	
	// Convert duration in seconds to minutes.
	public static String parseDuration(int durationInSeconds, Context context) {
		int min = durationInSeconds / 60;
		int sec = durationInSeconds % 60;
		
		String duration = "";
		if(min > 0) {
			duration = min + "mins ";
		}
		duration = duration + sec + "secs";
		
		return duration;

	}

	public static String parseCalories(int calories, Context context){
		String cals = calories + " cals";
		return cals;
	}

	public static String parseHeartRate(int hr, Context context){
		String heartrate = hr + " bpm";
		return heartrate;
	}

}
