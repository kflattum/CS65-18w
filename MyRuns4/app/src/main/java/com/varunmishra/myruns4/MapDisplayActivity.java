package com.varunmishra.myruns4;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.varunmishra.myruns4.data.ExerciseEntry;
import com.varunmishra.myruns4.data.ExerciseEntryDbHelper;

public class MapDisplayActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    // Menu ID for deletion
    public static final int MENU_ID_DELETE = 0;
    public static final int DEFAULT_MAP_ZOOM_LEVEL = 17;
    // For bookkeeping if the service bound already
    private boolean mIsBound;

    // Background service for GPS and Motion sensors
    private TrackingService mTrackingService;
    private Intent mServiceIntent;
    private int mTaskType;

    // Exercise entry
    private ExerciseEntry mExerciseEntry;
    private ExerciseEntryDbHelper mDbHelper;

    // Text views
    private TextView mTextType;
    private TextView mTextAvgSpeed;
    private TextView mTextCurSpeed;
    private TextView mTextClimb;
    private TextView mTextCalorie;
    private TextView mTextDistance;

    // map related
    private Marker mStartMarker;
    private Marker mFinishMarker;
    private Polyline mLocationTrace;

    // BroadcastReceiver for receiving ExerciseEntry updates
    private EntityUpdateReceiver mEventUpdateReceiver;

    public class EntityUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context ctx, Intent intent) {
            Log.e("onReceive","onReceive");
            // update trace
            drawTrace();
            // update exercise stats
            updateTraceInfo();
        }
    }
    @Override
    protected void onDestroy (){
        super.onDestroy();

        if (isFinishing()) {
            stopTrackingService();
            // do stuff
        } else {
            //It's an orientation change.
        }

    }
    // service connection that handles service binding
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        // when tracking service is connected, save the instance of service and
        // exercise entry
        public void onServiceConnected(ComponentName name, IBinder service) {
            // get the binder
            TrackingService.TrackingServiceBinder binder = (TrackingService.TrackingServiceBinder) service;

            // save the service object
            mTrackingService = binder.getService();
            // save the exercise entry
            mExerciseEntry = binder.getExerciseEntry();
        }

        public void onServiceDisconnected(ComponentName name) {
            // This ONLY gets called when crashed.
            Log.d(Globals.TAG, "Connection disconnected");
            // stopService(mServiceIntent);
            mTrackingService = null;
        }
    };

    @Override
    protected void onPause() {
        // unregister the receiver when the activity is about to go inactive
        // Reverse to what happened in onResume()
        if (mTaskType == Globals.TASK_TYPE_NEW) {
            unregisterReceiver(mEventUpdateReceiver);
        }
        // unbind the service to avoid bind leaking
        doUnbindService();

        Log.d(Globals.TAG, "Activity paused");
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_display);

        // init database
        mDbHelper = new ExerciseEntryDbHelper(this);
        // init broadcast receiver
        mEventUpdateReceiver = new EntityUpdateReceiver();
        // setup map
        setUpMapIfNeeded();
        // check for task type, new or history
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        // finish itself if extras is null
        if (extras == null) {
            // remove notifications
            // it's a little bit ugly in here because sometime android can't cleanup very
            // well after killing the app
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.cancelAll();

            finish();
            return;
        }

        // get task type
        mTaskType = extras.getInt(Globals.KEY_TASK_TYPE);

        mTextType = (TextView) findViewById(R.id.Type);
        mTextAvgSpeed = (TextView) findViewById(R.id.AvgSpeed);
        mTextCurSpeed = (TextView) findViewById(R.id.CurSpeed);
        mTextClimb = (TextView) findViewById(R.id.Climb);
        mTextCalorie = (TextView) findViewById(R.id.Calorie);
        mTextDistance = (TextView) findViewById(R.id.Distance);

        // Different initialization based on different task type and input mode
        // Combinations can be (new or history) and (gps or automatic). Manual
        // mode is handled
        // in ManualInputActivity
        // The difference between new and history is:
        // "new" task type needs a background service to read sensor data. "gps"
        // mode only pulls
        // the GPS locations, and "automatic" mode also pull motion sensor data
        // for Weka classifier
        // While the "history" task type reads from database, and display the
        // map route only, does not
        // need sensor service.
        switch (mTaskType) {

            case Globals.TASK_TYPE_NEW:
                // start tracking service if it is a new task
                int activityType = extras.getInt(Globals.KEY_ACTIVITY_TYPE);

                startTrackingService(activityType);
                break;

            case Globals.TASK_TYPE_HISTORY:
                // show the trace on the map. tracking service is not needed

                // No longer need "Save" and "Cancel" button in history mode
                ((Button) findViewById(R.id.buttonMapSave))
                        .setVisibility(View.GONE);
                ((Button) findViewById(R.id.buttonMapCancel))
                        .setVisibility(View.GONE);

                long rowid = extras.getLong(Globals.KEY_ROWID);
                // read track from database
                try {
                    mExerciseEntry = mDbHelper.fetchEntryByIndex(rowid);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            default:
                finish(); // Should never happen.
                return;
        }

        Log.d(Globals.TAG, "Activity created");
    }

    private void startTrackingService(int activityType) {
        mServiceIntent = new Intent(this, TrackingService.class);
        mServiceIntent.putExtra(Globals.KEY_ACTIVITY_TYPE, activityType);

        // start the service first
        startService(mServiceIntent);
        // Establish a connection with the service. We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(mServiceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void stopTrackingService() {
        // Stop the service and the notification.
        // ----------------------Skeleton--------------------------
        // Need to check whether the mSensorService is null or not
        // before unbind and stop the service.
        if (mTrackingService != null) {
            doUnbindService();
            stopService(mServiceIntent);
        }
    }

    private void doUnbindService() {
        if (mIsBound) {
            // Double unbind behaves like double free. So check first.
            // Detach our existing connection.
            unbindService(mServiceConnection);
            mIsBound = false;
        }
    }

    public void onSaveClicked(View v) {

        // Multiple click will give duplicate entries, disable the button
        // immediately after 1st click.
        v.setEnabled(false);

        if (mExerciseEntry != null) {
            // update duration
            mExerciseEntry.updateDuration();
            // insert the entry to the database
            new InsertDbTask().execute(mExerciseEntry);
        }

        // stop tracking service
        stopTrackingService();
        finish();
    }

    public void onCancelClicked(View v) {
        v.setEnabled(false);
        stopTrackingService();
        finish();
    }

    // we need to handle back button in here
    @Override
    public void onBackPressed() {
        stopTrackingService();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // If task type is displaying history, also give a menu button
        // To delete the entry
        MenuItem menuitem;
        if (mTaskType == Globals.TASK_TYPE_HISTORY) {
            menuitem = menu.add(Menu.NONE, MENU_ID_DELETE, MENU_ID_DELETE,
                    "Delete");
            menuitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_DELETE:
                if (mExerciseEntry != null) {
                    mDbHelper.removeEntry(mExerciseEntry.getId());
                }
                finish();
                return true;
            default:
                finish();
                return false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        // register the receiver for receiving the location update broadcast
        // from the service. Logic is the same as in onCreate()
        if (mTaskType == Globals.TASK_TYPE_NEW) {
            IntentFilter intentFilter = new IntentFilter(
                    EntityUpdateReceiver.class.getName());
            registerReceiver(mEventUpdateReceiver, intentFilter);
        }

        // draw trace and update exercise stats
        drawTrace();
        updateTraceInfo();

        Log.d(Globals.TAG, "Activity resumed");
        super.onResume();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mLocationTrace = mMap.addPolyline(new PolylineOptions());
    }
    // draw trace on the map by updating the initial trace
    private void drawTrace() {
        if (mExerciseEntry == null
                || mExerciseEntry.getLocationLatLngList().size() == 0) {
            return;
        }

        // get the trace from mExerciseEntry
        ArrayList<LatLng> trace = mExerciseEntry.getLocationLatLngList();

        // get the start and end location
        LatLng begin = trace.get(0);
        LatLng end = trace.get(trace.size() - 1);

        // add start marker
        if (mStartMarker == null) {
            mStartMarker = mMap.addMarker(new MarkerOptions().position(begin)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.green_dot)));
        }

        // update trace
        mLocationTrace.setPoints(trace);

        // add end marker
        if (mFinishMarker != null) {
            mFinishMarker.setPosition(end);
        } else {
            mFinishMarker = mMap.addMarker(new MarkerOptions().position(end)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.red_dot)));
        }

        // recenter the map if needed
        adjustMapCenter(end);
    }

    // recenter the map if needed
    private void adjustMapCenter(LatLng location) {

        boolean needRecenter = false;
        // the the boundry of visible region of the map
        VisibleRegion vr = mMap.getProjection().getVisibleRegion();

        if (!vr.latLngBounds.contains(location)) {
            // if the location is out of the visible region, recenter
            needRecenter = true;
        } else {
            // if the location is out of the center 70% if the visible region, recenter
            final int coeff = 1000000;
            double left = vr.latLngBounds.southwest.longitude;
            double top = vr.latLngBounds.northeast.latitude;
            double right = vr.latLngBounds.northeast.longitude;
            double bottom = vr.latLngBounds.southwest.latitude;

            int rectWidth = (int) Math.abs((right - left) * 0.7 * coeff);
            int rectHeight = (int) Math.abs((bottom - top) * 0.7 * coeff);

            LatLng mapCenter = mMap.getCameraPosition().target;
            int rectCenterX = (int) (mapCenter.longitude * coeff);
            int rectCenterY = (int) (mapCenter.latitude * coeff);

            // Constructs the rectangle Rect validScreenRect = new
            Rect validScreenRect = new Rect(rectCenterX - rectWidth / 2,
                    rectCenterY - rectHeight / 2, rectCenterX + rectWidth / 2,
                    rectCenterY + rectHeight / 2);

            needRecenter = !validScreenRect.contains(
                    (int) (location.longitude * coeff),
                    (int) (location.latitude * coeff));
        }

        if (needRecenter) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location,
                    Globals.DEFAULT_MAP_ZOOM_LEVEL);
            mMap.animateCamera(update);
        }

    }

    // update exercise stats
    private void updateTraceInfo() {
        if (mExerciseEntry == null) {
            return;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        String type = "Type: "
                + Utils.parseActivityType(mExerciseEntry.getActivityType(),
                this);
        String avg_speed = "Avg speed: "
                + Utils.parseSpeed(mExerciseEntry.getAvgSpeed(), this);
        String cur_speed = "Cur speed: "
                + Utils.parseSpeed(mExerciseEntry.getCurSpeed(), this);
        String climb = "Climb: "
                + Utils.parseDistance(mExerciseEntry.getClimb(), this);
        String calorie = "Calorie: "
                + decimalFormat.format(mExerciseEntry.getCalorie());
        String distance = "Distance: "
                + Utils.parseDistance(mExerciseEntry.getDistance(), this);

        mTextType.setText(type);
        mTextAvgSpeed.setText(avg_speed);
        mTextCurSpeed.setText(cur_speed);
        mTextClimb.setText(climb);
        mTextCalorie.setText(calorie);
        mTextDistance.setText(distance);
    }
    public class InsertDbTask extends AsyncTask<ExerciseEntry, Void, String> {
        @Override
        protected String doInBackground(ExerciseEntry... exerciseEntries) {
            long id = mDbHelper.insertEntry(mExerciseEntry);

            if (id > 0) {
                return "Entry #" + id + " saved.";
            }
            return "Entry Not Saved";

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
                    .show();
        }

    }
}
