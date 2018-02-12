package com.varunmishra.myruns4.data;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
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
	private ArrayList<LatLng> mLocationLatLngList; // Location list

	private Location mLastLocation;

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
		mLocationLatLngList = new ArrayList<LatLng>();

		mLastLocation = null;
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

	// update exercise duration
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

	public ArrayList<LatLng> getLocationLatLngList() {
		return this.mLocationLatLngList;
	}

	// insert a new location to the trace
	public synchronized void insertLocation(Location location) {
		// add location to location list
		mLocationLatLngList.add(new LatLng(location.getLatitude(), location
				.getLongitude()));

		// update status
		if (mLastLocation == null) {
			setAvgSpeed(0);
			setClimb(0);
			setAvgSpeed(0);
			setClimb(0);
			setDistance(0);
			setCalorie(0);
		} else {
			mDistance += Math.abs(location.distanceTo(mLastLocation));
			mClimb += location.getAltitude() - mLastLocation.getAltitude();
			mCalorie = (int) (mDistance / 15.0);
		}

		updateDuration();
		mCurrentSpeed = location.getSpeed();
		mLastLocation = location;
	}

	public double getCurSpeed() {
		return mCurrentSpeed;
	}

	// Convert Location ArrayList to byte array, to store in SQLite database
	public byte[] getLocationByteArray() {
		int[] intArray = new int[mLocationLatLngList.size() * 2];

		for (int i = 0; i < mLocationLatLngList.size(); i++) {
			intArray[i * 2] = (int) (mLocationLatLngList.get(i).latitude * 1E6);
			intArray[(i * 2) + 1] = (int) (mLocationLatLngList.get(i).longitude * 1E6);
		}

		ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length
				* Integer.SIZE);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(intArray);

		return byteBuffer.array();
	}

	// Convert byte array to Location ArrayList
	public void setLocationListFromByteArray(byte[] bytePointArray) {

		ByteBuffer byteBuffer = ByteBuffer.wrap(bytePointArray);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();

		int[] intArray = new int[bytePointArray.length / Integer.SIZE];
		intBuffer.get(intArray);

		int locationNum = intArray.length / 2;

		for (int i = 0; i < locationNum; i++) {
			LatLng latLng = new LatLng((double) intArray[i * 2] / 1E6F,
					(double) intArray[i * 2 + 1] / 1E6F);
			mLocationLatLngList.add(latLng);
		}
	}

}
