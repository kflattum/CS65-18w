package com.varunmishra.myruns4;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.varunmishra.myruns4.data.ExerciseEntry;
import com.varunmishra.myruns4.data.ExerciseEntryDbHelper;


public class ManualInputActivity extends FragmentActivity {

    // Exercise entry
    private ExerciseEntryDbHelper mEntryDbHelper;
    private ExerciseEntry mEntry;

    ListView listview;
    public static final int LIST_ITEM_ID_DATE = 0;
    public static final int LIST_ITEM_ID_TIME = 1;
    public static final int LIST_ITEM_ID_DURATION = 2;
    public static final int LIST_ITEM_ID_DISTANCE = 3;
    public static final int LIST_ITEM_ID_CALORIES = 4;
    public static final int LIST_ITEM_ID_HEARTRATE = 5;
    public static final int LIST_ITEM_ID_COMMENT = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Setting the UI layout
        setContentView(R.layout.activity_manual_input);

        listview = (ListView)findViewById(R.id.listview);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                int dialogId;

                // Figuring out what dialog to show based on the position clicked
                // (more readable, also could use dialogId = position + 2)
                switch (position) {
                    case LIST_ITEM_ID_DATE:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_DATE;
                        break;
                    case LIST_ITEM_ID_TIME:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_TIME;
                        break;
                    case LIST_ITEM_ID_DURATION:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_DURATION;
                        break;
                    case LIST_ITEM_ID_DISTANCE:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_DISTANCE;
                        break;
                    case LIST_ITEM_ID_CALORIES:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_CALORIES;
                        break;
                    case LIST_ITEM_ID_HEARTRATE:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_HEARTRATE;
                        break;
                    case LIST_ITEM_ID_COMMENT:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_COMMENT;
                        break;
                    default:
                        dialogId = DialogFragment.DIALOG_ID_ERROR;
                }

                displayDialog(dialogId);
            }
        });

        // Initialize
        mEntry = new ExerciseEntry();
        mEntryDbHelper = new ExerciseEntryDbHelper(this);

        // Get the extra information passed from the launching activity
        Bundle extras = getIntent().getExtras();

        mEntry.setInputType(extras.getInt(Globals.KEY_INPUT_TYPE, -1));
        mEntry.setActivityType(extras.getInt(Globals.KEY_ACTIVITY_TYPE, -1));
    }

    // "Save" button is clicked
    public void onSaveClicked(View v) {

        // Insert the exercise entry into database
        //long id = mEntryDbHelper.insertEntry(mEntry);

        // Pop up a toast
//        Toast.makeText(this, "Entry #" + id + " saved.", Toast.LENGTH_SHORT)
//                .show();

        // Close the activity
        new InsertDbTask().execute(mEntry);
        finish();
    }

    // "Cancel" button is clicked
    public void onCancelClicked(View v) {
        // Discard the input and close the activity directly
        Toast.makeText(getApplicationContext(), "Entry discarded.",
                Toast.LENGTH_SHORT).show();
        finish();
    }



    // Display dialog based on id. See DialogFragment for details
    public void displayDialog(int id) {
        android.app.DialogFragment fragment = DialogFragment.newInstance(id);
        fragment.show(getFragmentManager(),
                getString(R.string.dialog_fragment_tag_general));
    }

    // ********************************
    // The following are functions called after dialog is clicked.
    // Called from DialogFragment side. mEntry is handled here in a
    // cleaner and more separated way.
    // value are parsed and set in mEntry for later database insersion.

    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
        mEntry.setDate(year, monthOfYear, dayOfMonth);
    }

    public void onTimeSet(int hourOfDay, int minute) {
        mEntry.setTime(hourOfDay, minute);
    }

    public void onDurationSet(String strDurationInMinutes) {

        int durationInSeconds;
        try {
            durationInSeconds = (int) (Double.parseDouble(strDurationInMinutes) * 60);
        } catch (NumberFormatException e) {
            durationInSeconds = 0;
        }

        mEntry.setDuration(durationInSeconds);
    }

    public void onDistanceSet(String strDistance) {

        int distanceInMeters;
        try {
            distanceInMeters = (int) (Double.parseDouble(strDistance) * 1000 * Globals.KM2MILE_RATIO);
        } catch (NumberFormatException e) {
            distanceInMeters = 0;
        }
        mEntry.setDistance(distanceInMeters);

    }

    public void onCaloriesSet(String strCalories) {

        int calories;
        try {
            calories = (Integer.parseInt(strCalories));
        } catch (NumberFormatException e) {
            calories = 0;
        }
        mEntry.setCalorie(calories);

    }

    public void onHeartrateSet(String strHeartrate) {
        int heartrate;
        try {
            heartrate = (Integer.parseInt(strHeartrate));
        } catch (NumberFormatException e) {
            heartrate = 0;
        }
        mEntry.setHeartrate(heartrate);
    }

    public void onCommentSet(String comment) {
        mEntry.setComment(comment);
    }

    public class InsertDbTask extends AsyncTask<ExerciseEntry, Void, String> {
        @Override
        protected String doInBackground(ExerciseEntry... exerciseEntries) {
            long id = mEntryDbHelper.insertEntry(exerciseEntries[0]);

            return ""+id;
            // Pop up a toast

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Entry #" + result + " saved.", Toast.LENGTH_SHORT)
                    .show();
        }

    }
}