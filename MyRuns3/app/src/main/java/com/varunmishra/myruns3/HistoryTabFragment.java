package com.varunmishra.myruns3;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.varunmishra.myruns3.data.ExerciseEntry;
import com.varunmishra.myruns3.data.ExerciseEntryDbHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class HistoryTabFragment extends ListFragment implements LoaderManager.LoaderCallbacks<ArrayList<ExerciseEntry>>  {

    private static ExerciseEntryDbHelper mExerciseEntryDbHelper;
    public static ArrayList<ExerciseEntry>  entryList = new ArrayList<ExerciseEntry>();
    public static Context mContext; // context pointed to parent activity
    public static ActivityEntriesAdapter mAdapter; // customized adapter for displaying
    // exercise entry
    public static LoaderManager loaderManager;
    public static int onCreateCheck=0;

    // retrieve records from the database and display them in the list view

    public void updateHistoryEntries(Context context) {
        if (this.mExerciseEntryDbHelper == null) {
            setUpDB(context);
        }
        if(onCreateCheck==1){
            onCreateCheck=0;
        } else {
            loaderManager.initLoader(1, null, this).forceLoad();
            Log.d("TAG", "Called");
        }
    }

    private void setUpDB(Context context) {
        this.mContext = context;
        ExerciseEntryDbHelper exerciseEntryDbHelper = new ExerciseEntryDbHelper(context);
        this.mExerciseEntryDbHelper = exerciseEntryDbHelper;
        ActivityEntriesAdapter activityEntriesAdapter = new ActivityEntriesAdapter(this,context);
        this.mAdapter = activityEntriesAdapter;
        loaderManager = getActivity().getLoaderManager();

        setListAdapter(this.mAdapter);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

        // Open data base for operations.
        mExerciseEntryDbHelper = new ExerciseEntryDbHelper(mContext);
        loaderManager = getActivity().getLoaderManager();
        // Instantiate our customized array adapter
        mAdapter = new ActivityEntriesAdapter(this,mContext);
        // Set the adapter to the listview
        setListAdapter(mAdapter);
        loaderManager.initLoader(1, null, this).forceLoad();

        onCreateCheck=1;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
        public void onResume() {
        super.onResume();
        // Re-query in case the data base has changed.
        updateHistoryEntries(mContext);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Click event
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(); // The intent to launch the activity after
        // click.
        Bundle extras = new Bundle(); // The extra information needed pass
        // through to next activity.

        // get the ExerciseEntry corresponding to user's selection
        ExerciseEntry entry = mAdapter.getItem(position);
        // Task type is display history, versus create new as in
        // StartTabFragment.java
        extras.putInt(Globals.KEY_TASK_TYPE, Globals.TASK_TYPE_HISTORY);

        // Write row id into extras.
        extras.putLong(Globals.KEY_ROWID, entry.getId());

        // Read the input type: Manual, GPS, or automatic
        int inputType = entry.getInputType();
        // Write the input type

        // Based on different input type, launching different activities
        switch (inputType) {

            case Globals.INPUT_TYPE_GPS:
            case Globals.INPUT_TYPE_AUTOMATIC:
                // GPS and Automatic require MapDisplayAcvitity
                intent.setClass(mContext, MapDisplayActivity.class);
                break;

            case Globals.INPUT_TYPE_MANUAL: // Manual mode

                // Passing information for display in the DisaplayEntryActivity.
                extras.putString(Globals.KEY_ACTIVITY_TYPE,
                        Utils.parseActivityType(entry.getActivityType(), mContext));
                extras.putString(Globals.KEY_DATE_TIME,
                        Utils.parseTime(entry.getDateTimeInMillis(), mContext));
                extras.putString(Globals.KEY_DURATION,
                        Utils.parseDuration(entry.getDuration(), mContext));
                extras.putString(Globals.KEY_DISTANCE,
                        Utils.parseDistance(entry.getDistance(), mContext));
                extras.putString(Globals.KEY_INPUT_TYPE,
                        Utils.parseInputType(entry.getInputType(), mContext));
                extras.putString(Globals.KEY_CALORIES,
                        Utils.parseCalories(entry.getCalorie(), mContext));
                extras.putString(Globals.KEY_HEARTRATE,
                        Utils.parseHeartRate(entry.getHeartrate(), mContext));


                // Manual mode requires DisplayEntryActivity
                intent.setClass(mContext, DisplayEntryActivity.class);
                break;
            default:
                return;
        }

        // start the activity
        intent.putExtras(extras);
        startActivity(intent);
    }


        @Override
    public Loader<ArrayList<ExerciseEntry>> onCreateLoader(int i, Bundle bundle) {
        Log.d("TAGG", "Came here");

        return new DataLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ExerciseEntry>> loader, ArrayList<ExerciseEntry> exerciseEntries) {
        entryList = exerciseEntries;
        mAdapter.clear();
        Log.d("TAGG", "Load Finished");


        mAdapter.addAll(entryList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ExerciseEntry>> loader) {
        mAdapter.clear();
        mAdapter.addAll(entryList);
        mAdapter.notifyDataSetChanged();
    }


    // Subclass of ArrayAdapter to display interpreted database row values in
    // customized list view.
    private class ActivityEntriesAdapter extends ArrayAdapter<ExerciseEntry> {
        final /* synthetic */ HistoryTabFragment this_0;

        public ActivityEntriesAdapter(HistoryTabFragment historyTabFragment, Context context) {
            // set layout to show two lines for each item

            super(context, android.R.layout.two_line_list_item);
            this.this_0 = historyTabFragment;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View listItemView = convertView;
            if (null == convertView) {
                // we need to check if the convertView is null. If it's null,
                // then inflate it.
                listItemView = inflater.inflate(
                        android.R.layout.two_line_list_item, parent, false);
            }

            // Setting up view's text1 is main title, text2 is sub-title.
            TextView titleView = (TextView) listItemView
                    .findViewById(android.R.id.text1);
            TextView summaryView = (TextView) listItemView
                    .findViewById(android.R.id.text2);

            // get the corresponding ExerciseEntry
            ExerciseEntry entry = getItem(position);

            //parse data to readable format
            String inputType = Utils.parseInputType(entry.getInputType(),mContext);
            String activityTypeString = Utils.parseActivityType(
                    entry.getActivityType(), mContext);

            String dateString = Utils.parseTime(entry.getDateTimeInMillis(),
                    mContext);
            String distanceString = Utils.parseDistance(entry.getDistance(),
                    mContext);
            String durationString = Utils.parseDuration(entry.getDuration(),
                    mContext);

            // Set text on the view.
            titleView.setText(inputType + ": " + activityTypeString + ", " + dateString);
            summaryView.setText(distanceString + ", " + durationString);

            return listItemView;
        }

    }

}