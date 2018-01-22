package com.varunmishra.myruns2;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;


public class ManualInputActivity extends Activity {

    public static final int LIST_ITEM_ID_DATE = 0;
    public static final int LIST_ITEM_ID_TIME = 1;
    public static final int LIST_ITEM_ID_DURATION = 2;
    public static final int LIST_ITEM_ID_DISTANCE = 3;
    public static final int LIST_ITEM_ID_CALORIES = 4;
    public static final int LIST_ITEM_ID_HEARTRATE = 5;
    public static final int LIST_ITEM_ID_COMMENT = 6;
    ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);

        listview = (ListView)findViewById(R.id.listview);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                int dialogId;


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

    }

    public void onSaveClicked(View v) {
        finish();
    }

    public void onCancelClicked(View v) {
        // Discard the input and close the activity directly
        Toast.makeText(getApplicationContext(), "Entry discarded.",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    public void displayDialog(int id) {
        android.app.DialogFragment fragment = DialogFragment.newInstance(id);
        fragment.show(getFragmentManager(),
                "Stating dialog");
    }

    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
    }

    public void onTimeSet(int hourOfDay, int minute) {
    }

    public void onDurationSet(String strDurationInMinutes) {
    }

    public void onDistanceSet(String strDistance) {
    }

    public void onCaloriesSet(String strCalories) {
    }

    public void onHeartrateSet(String strHeartrate) {
    }

    public void onCommentSet(String comment) {
    }
}