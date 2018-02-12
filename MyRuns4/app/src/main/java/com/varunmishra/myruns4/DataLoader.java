package com.varunmishra.myruns4;

/**
 * Created by Varun on 1/26/16.
 */
import java.util.ArrayList;
import android.content.Context;
import android.content.AsyncTaskLoader;
import android.util.Log;

import com.varunmishra.myruns4.data.ExerciseEntry;
import com.varunmishra.myruns4.data.ExerciseEntryDbHelper;

public class DataLoader extends AsyncTaskLoader<ArrayList<ExerciseEntry>> {
    public Context mContext;
    public DataLoader(Context context) {
        super(context);
        mContext = context;
    }
    @Override
    protected void onStartLoading() {
       forceLoad();
    }

    @Override
    public ArrayList<ExerciseEntry> loadInBackground() {
        Log.d("TAGG","Started");
        ExerciseEntryDbHelper mExerciseEntryDbHelper = new ExerciseEntryDbHelper(mContext);
        ArrayList<ExerciseEntry> entryList = mExerciseEntryDbHelper
                .fetchEntries();
        Log.d("TAGG","Finished");

        return entryList;
    }
}