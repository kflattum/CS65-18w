package com.varunmishra.myruns2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;


public class MapDisplayActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);
    }

    public void onSaveClicked(View v) {
        finish();
    }

    public void onCancelClicked(View v) {
        v.setEnabled(false);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

