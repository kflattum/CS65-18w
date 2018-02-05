package com.varunmishra.myruns3.data;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ExerciseEntry {
	private Long mId;

	private int mInputType;
	private int mActivityType;
	private Calendar mDateTime;
	private int mDuration;
	private double mDistance;
	private double mAvgPace;
	private double mAvgSpeed;
	private double mCurrentSpeed;
	private int mCalorie;
	private double mClimb;
	private int mHeartRate;
	private String mComment;

	private int mCurrentInferredActivityType;

	public ExerciseEntry() {
		this.mInputType = -1;
		this.mActivityType = -1;
		this.mDateTime = Calendar.getInstance();
		this.mDuration = 0;
		this.mDistance = 0;
		this.mAvgPace = 0;
		this.mAvgSpeed = 0;
		this.mCalorie = 0;
		this.mClimb = 0;
		this.mHeartRate = 0;
		this.mComment = "";
		mCurrentSpeed = -1;
        mCurrentInferredActivityType = -1;
	}

	public Long getId() {
		return mId;
	}

	public void setId(Long id) {
		this.mId = id;
	}

	public int getInputType() {
		return mInputType;
	}

	public void setInputType(int inputType) {
		this.mInputType = inputType;
	}

	public int getActivityType() {
		return mActivityType;
	}

	public void setActivityType(int activityType) {
		this.mActivityType = activityType;
	}

	public Calendar getDateTime() {
		return mDateTime;
	}

	public long getDateTimeInMillis() {
		return mDateTime.getTimeInMillis();
	}

	public void setDateTime(Calendar dateTime) {
		this.mDateTime = dateTime;

	}

	public void setDateTime(long timestamp) {
		this.mDateTime.setTimeInMillis(timestamp);

	}

	public void setDate(int year, int monthOfYear, int dayOfMonth) {
		mDateTime.set(year, monthOfYear, dayOfMonth);
	}

	public void setTime(int hourOfDay, int minute) {
		mDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
		mDateTime.set(Calendar.MINUTE, minute);
		mDateTime.set(Calendar.SECOND, 0);
	}

	public void updateDuration() {
		mDuration = (int) ((System.currentTimeMillis() - mDateTime
				.getTimeInMillis()) / 1000);

		if (mDuration != 0) {
			mAvgSpeed = mDistance / mDuration;
		}
	}

	public int getDuration() {
		return mDuration;
	}

	public void setDuration(int duration) {
		this.mDuration = duration;
	}

	public double getDistance() {
		return mDistance;
	}

	public void setDistance(double distance) {
		this.mDistance = distance;
	}

	public double getAvgPace() {
		return mAvgPace;
	}

	public void setAvgPace(double avgPace) {
		this.mAvgPace = avgPace;
	}

	public double getAvgSpeed() {
		return mAvgSpeed;
	}

	public void setAvgSpeed(double avgSpeed) {
		this.mAvgSpeed = avgSpeed;
	}

	public int getCalorie() {
		return mCalorie;
	}

	public void setCalorie(int calorie) {
		this.mCalorie = calorie;
	}

	public double getClimb() {
		return mClimb;
	}

	public void setClimb(double climb) {
		this.mClimb = climb;
	}

	public int getHeartrate() {
		return mHeartRate;
	}

	public void setHeartrate(int heartrate) {
		this.mHeartRate = heartrate;
	}

	public String getComment() {
		return mComment;
	}

	public void setComment(String comment) {
		this.mComment = comment;
	}

	public synchronized void insertLocation(Location location) {

	}

	public double getCurSpeed() {
		return mCurrentSpeed;
	}

	public int getCurrentActivity() {
		return mCurrentInferredActivityType;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();

		try {
			obj.put("id", mId);
			obj.put("inputType", getInputType());
			obj.put("activityType", getActivityType());
			obj.put("dateTime", getDateTimeInMillis());
			obj.put("duration", getDuration());
			obj.put("distance", getDistance());
			obj.put("avgSpeed", getAvgSpeed());
			obj.put("calorie", getCalorie());
			obj.put("climb", getClimb());
			obj.put("heartrate", getHeartrate());
			obj.put("comment", getComment());
		} catch (JSONException e) {
			return null;
		}

		return obj;
	}
}
