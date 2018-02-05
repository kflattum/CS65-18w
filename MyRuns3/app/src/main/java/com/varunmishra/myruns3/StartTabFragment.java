package com.varunmishra.myruns3;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class StartTabFragment extends Fragment {

    // Context stands for current running activity.
    private Context mContext;

    // View widgets on the screen needs to be programmatically configured
    private Spinner mSpinnerInputType;
    private Spinner mSpinnerActivityType;
    private Button mButtonStart;
    private Button mButtonSync;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        mContext = getActivity();

        mSpinnerInputType = (Spinner) view.findViewById(R.id.spinnerInputType);
        mSpinnerActivityType = (Spinner) view
                .findViewById(R.id.spinnerActivityType);
        mButtonStart = (Button) view.findViewById(R.id.btnStart);
        mButtonSync = (Button) view.findViewById(R.id.btnSync);

        ArrayAdapter<String> arrayAdapterInput = new ArrayAdapter<String>(
                mContext,
                android.R.layout.simple_list_item_1,
                Globals.INPUT_TYPES );
        mSpinnerInputType.setAdapter(arrayAdapterInput);

        ArrayAdapter<String> arrayAdapterActivity = new ArrayAdapter<String>(
                mContext,
                android.R.layout.simple_list_item_1,
                Globals.ACTIVITY_TYPES );
        mSpinnerActivityType.setAdapter(arrayAdapterActivity);

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onStartButtonClick(v);
            }
        });

        mButtonSync.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSyncClicked(v);
            }
        });

        return view;
    }

    public void onStartButtonClick(View v) {

        int inputType = mSpinnerInputType.getSelectedItemPosition();

        // Create a extras bundle to store the index of item selected
        // and pass to the next activity through an intent.
        Bundle extras = new Bundle();

        // New task (versus reading form history database)
        extras.putInt(Globals.KEY_TASK_TYPE, Globals.TASK_TYPE_NEW);
        // Input type as explained above.
        extras.putInt(Globals.KEY_INPUT_TYPE, inputType);

        Intent i;

        // Based on different selection, start different activity with

        switch (inputType) {

            case Globals.INPUT_TYPE_GPS:

                // GPS automatic tracking
                extras.putInt(Globals.KEY_ACTIVITY_TYPE,
                        mSpinnerActivityType.getSelectedItemPosition());
                // Intent to launch MapDisplayActivity
                i = new Intent(mContext, MapDisplayActivity.class);
                break;

            case Globals.INPUT_TYPE_MANUAL:

                // Manual input
                extras.putInt(Globals.KEY_ACTIVITY_TYPE,
                        mSpinnerActivityType.getSelectedItemPosition());
                // Intent to launch ManualInputActivity
                i = new Intent(mContext, ManualInputActivity.class);
                break;

            case Globals.INPUT_TYPE_AUTOMATIC:
                // Inference, the activity does not matter
                extras.putInt(Globals.KEY_ACTIVITY_TYPE, -1);
                // Intent to launch MapDisplayActivity
                i = new Intent(mContext, MapDisplayActivity.class);
                break;

            default:
                return;
        }

        // Put extras into the intent
        i.putExtras(extras);
        // Launch activity
        startActivity(i);
    }

    public void onSyncClicked(View view) {

    }

}