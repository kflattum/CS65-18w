package edu.dartmouth.cs.stressmeter.psm;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.dartmouth.cs.stressmeter.R;

public class PSMMainFragment extends Fragment {

    private GridView mPictureGrid;
    private ImageAdapter mImageAdapter;

    private List<Integer> mGridSeq = new ArrayList<>();
    private int mGridSeqId = 0;
    private int mSelectedSlot = -1;
    private boolean mNeedAlert = true;
    private Button mButtonMore;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.psmmain_fragment, container, false);


        mPictureGrid = (GridView)view.findViewById(R.id.PSMgridView);
        mImageAdapter =  new ImageAdapter(getActivity());
        mPictureGrid.setAdapter(mImageAdapter);

        mPictureGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                EMAAlert.getAlertObject().cancel();

                mSelectedSlot = position;
                Log.e("SELECTED", mSelectedSlot + "");

                Intent intent = new Intent(getActivity(), PSMConfirmActivity.class);
                intent.putExtra(PSMConfirmActivity.MSG_SELECTED_IMG_ID,
                        PSM.getGridById(mGridSeq.get(mGridSeqId))[position]);

                startActivityForResult(intent, 0);
            }
        });

        mButtonMore = (Button)view.findViewById(R.id.button);
        mButtonMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onMoreImageClick(v);
            }
        });
        initGridSeq();
        view.post(new Runnable() {
                           @Override
                           public void run() {
                               loadImages(mGridSeq.get(mGridSeqId));
                               // code you want to run when view is visible for the first time
                           }
                       }
        );
        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PSMScheduler.setSchedule(getActivity());

        if(mNeedAlert) {
            EMAAlert.getAlertObject().startAlert(getActivity());
            mNeedAlert = false;
        }

    }

    @Override
    public void onResume (){
        super.onResume();

    }



    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(resultCode == getActivity().RESULT_OK && data.getBooleanExtra(PSMConfirmActivity.MSG_PSM_IS_CONFIRMED,false)) {
            savePSMResponse(mSelectedSlot);
            getActivity().finish();
        }

        mSelectedSlot = -1;
    }

    private void savePSMResponse(int psmSlotId) {
        if(psmSlotId == -1) {
            return;
        }

        String resp = System.currentTimeMillis() / 1000 + "," + psmSlotId + "\n";

        File psmFile = new File(Environment.getExternalStorageDirectory(), "stress_meter_resp.csv");;
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = new FileOutputStream(psmFile, true);
            fileOutputStream.write(resp.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGridSeq() {
        mGridSeq.add(1);
        mGridSeq.add(2);
        mGridSeq.add(3);

        Collections.shuffle(mGridSeq, new Random(System.currentTimeMillis()));
    }

    public void onMoreImageClick(View v) {
        EMAAlert.getAlertObject().cancel();

        mGridSeqId = (mGridSeqId + 1) % mGridSeq.size();

        loadImages(mGridSeq.get(mGridSeqId));
    }

    private void loadImages(int gridId) {
        mImageAdapter.clear();

        int[] grid = PSM.getGridById(gridId);

        mImageAdapter.setPSMGrid(grid);

        mPictureGrid.setAdapter(mImageAdapter);
    }



    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<Drawable> mImgList;
        private ArrayList<String> mImgPathList;

        public ImageAdapter(Context c) {
            mContext = c;
            mImgList = new ArrayList<Drawable>();
            mImgPathList = new ArrayList<String>();
        }

        public void clear() {
            mImgList.clear();
            mImgPathList.clear();
        }

        public void setPSMGrid(int[] grid){
            for(int i = 0; i< grid.length; i++) {
                Drawable d = getResources().getDrawable(grid[i]);
                mImgList.add(d);
                mImgPathList.add(String.valueOf(i));
            }
        }

        public int getCount() {
            return mImgList.size();
        }

        public Object getItem(int position) {
            return mImgPathList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            int imgDim = getImgDim();

            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(imgDim, imgDim));
                imageView.setMaxWidth(imgDim);
                imageView.setMaxHeight(imgDim);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageDrawable(mImgList.get(position));

            imgDim = getImgDim();
            return imageView;
        }
    }

    private int getImgDim() {
        int gridW = mPictureGrid.getWidth();
        int gridH = mPictureGrid.getHeight();

        int imgDim = Math.min(gridW / 4, gridH / 4);

        return imgDim;
    }
}
