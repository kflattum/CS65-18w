package com.varunmishra.myruns2;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InvalidObjectException;

import com.soundcloud.android.crop.Crop;


public class ProfileActivity extends Activity {

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_SELECT_FROM_GALLERY = 1;

    public static final int REQUEST_CODE_CROP_PHOTO = 2;

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final String URI_INSTANCE_STATE_KEY_TEMP = "saved_uri_temp";
    private static final String CAMERA_CLICKED_KEY = "clicked";


    private Button mBtnChangeImage, mBtnSave, mBtnCancel;
    private ImageView mProfileImage;
    private Uri mImageCaptureUri, mTempUri;
    private Boolean stateChnaged = false, cameraClicked = false,clickedFromCam=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState
                    .getParcelable(URI_INSTANCE_STATE_KEY);
            mTempUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY_TEMP);
            cameraClicked = savedInstanceState.getBoolean(CAMERA_CLICKED_KEY);
            stateChnaged=true;
        }
        mBtnChangeImage = (Button) findViewById(R.id.btnChangePhoto);
        mBtnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.DialogFragment fragment = DialogFragment.newInstance(DialogFragment.DIALOG_ID_PHOTO_PICKER);
                fragment.show(getFragmentManager(), "Photo Picker");            }
        });
        mBtnSave = (Button) findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.ui_profile_toast_save_text),
                        Toast.LENGTH_SHORT).show();
                // Close the activity
                finish();
            }
        });
        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick();
            }
        });
        mProfileImage = (ImageView)findViewById(R.id.imageProfile);
        loadProfile();

    }

    public void onCancelClick(){
        //Toast.makeText(getApplicationContext(), getString(R.id.cancel_message), Toast.LENGTH_SHORT).show();
        finish();
    }

    public void changeImage(){
        Intent intent;


        // Take photo from camera，
        // Construct an intent with action
        // MediaStore.ACTION_IMAGE_CAPTURE
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Construct temporary image path and name to save the taken
        // photo
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        mImageCaptureUri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

/*
    This was the previous code to generate a URI. This was throwing an exception -
    "android.os.StrictMode.onFileUriExposed" in Android N.
    This was because StrictMode prevents passing URIs with a file:// scheme. Once you
    set the target SDK to 24, then the file:// URI scheme is no longer supported because the
    security is exposed. You can change the  targetSDK version to be <24, to use the following code.
    The new code as written above works nevertheless.


        mImageCaptureUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), "tmp_"
                + String.valueOf(System.currentTimeMillis()) + ".jpg"));
*/
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                mImageCaptureUri);
        intent.putExtra("return-data", true);
        try {
            // Start a camera capturing activity
            // REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
            // defined to identify the activity in onActivityResult()
            // when it returns
            startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }
    public void loadProfile(){
        String key, str_val;
        int int_val;

        key = getString(R.string.preference_name);
        SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);

        key = getString(R.string.preference_key_profile_name);
        str_val = prefs.getString(key, "");
        ((EditText) findViewById(R.id.etName)).setText(str_val);

        key = getString(R.string.preference_key_profile_email);
        str_val = prefs.getString(key, "");
        ((EditText) findViewById(R.id.etEmail)).setText(str_val);

        key = getString(R.string.preference_key_profile_phone);
        str_val = prefs.getString(key, "");
        ((EditText) findViewById(R.id.etPhone)).setText(str_val);

        key = getString(R.string.preference_key_profile_gender);
        int_val = prefs.getInt(key, -1);

        if (int_val >= 0) {
            RadioButton radioBtn = (RadioButton) ((RadioGroup) findViewById(R.id.radioGender))
                    .getChildAt(int_val);
            radioBtn.setChecked(true);
        }

        key = getString(R.string.preference_key_profile_class);
        str_val = prefs.getString(key, "");
        ((TextView) findViewById(R.id.etClass)).setText(str_val);

        key = getString(R.string.preference_key_profile_major);
        str_val = prefs.getString(key, "");
        ((TextView) findViewById(R.id.etMajor)).setText(str_val);

        loadProfileImage();
    }

    public void saveProfile(){
        String key, str_val;
        int int_val;

        key = getString(R.string.preference_name);
        SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Write screen contents into corresponding editor fields.
        key = getString(R.string.preference_key_profile_name);
        str_val = ((EditText) findViewById(R.id.etName)).getText().toString();
        editor.putString(key, str_val);

        key = getString(R.string.preference_key_profile_email);
        str_val = ((EditText) findViewById(R.id.etEmail)).getText()
                .toString();
        editor.putString(key, str_val);

        key = getString(R.string.preference_key_profile_phone);
        str_val = ((EditText) findViewById(R.id.etPhone)).getText()
                .toString();
        editor.putString(key, str_val);

        key = getString(R.string.preference_key_profile_gender);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGender);
        int_val = radioGroup.indexOfChild(findViewById(radioGroup
                .getCheckedRadioButtonId()));
        editor.putInt(key, int_val);

        key = getString(R.string.preference_key_profile_class);
        str_val = ((EditText) findViewById(R.id.etClass)).getText()
                .toString();
        editor.putString(key, str_val);

        key = getString(R.string.preference_key_profile_major);
        str_val = ((EditText) findViewById(R.id.etMajor)).getText()
                .toString();
        editor.putString(key, str_val);

        editor.apply();

        saveProfileImage();


    }

    private void loadProfileImage() {


        // Load profile photo from internal storage


        try {
            FileInputStream fis;

            if(stateChnaged && cameraClicked){
                if(!Uri.EMPTY.equals(mTempUri)) {
                    mProfileImage.setImageURI(mTempUri);
                    stateChnaged = false;
                }
            } else {
                fis = openFileInput(getString(R.string.profile_photo_file_name));
                Bitmap bmap = BitmapFactory.decodeStream(fis);
                mProfileImage.setImageBitmap(bmap);

                fis.close();
            }

        } catch (IOException e) {
            // Default profile photo if no photo saved before.
            mProfileImage.setImageResource(R.drawable.default_profile);
        }
    }
    private void saveProfileImage() {

        // Commit all the changes into preference file
        // Save profile image into internal storage.
        mProfileImage.buildDrawingCache();
        Bitmap bmap = mProfileImage.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(
                    getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                beginCrop(mImageCaptureUri);
                clickedFromCam=true;
                break;

            case REQUEST_CODE_SELECT_FROM_GALLERY:
                Uri srcUri = data.getData();
                beginCrop(srcUri);
                break;

            case Crop.REQUEST_CROP:
                // Update image view after image crop
                // Set the picture image in UI
                handleCrop(resultCode, data);

                // Delete temporary image taken by camera after crop.
                if(clickedFromCam) {
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists())
                        f.delete();
                    clickedFromCam=false;
                }

                break;
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
            outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
            outState.putParcelable(URI_INSTANCE_STATE_KEY_TEMP,mTempUri);
            outState.putBoolean(CAMERA_CLICKED_KEY,cameraClicked);
    }
    /** Method to start Crop activity using the library
     *	Earlier the code used to start a new intent to crop the image,
     *	but here the library is handling the creation of an Intent, so you don't
     * have to.
     *  **/
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
             mTempUri = Crop.getOutput(result);
            mProfileImage.setImageResource(0);
            mProfileImage.setImageURI(mTempUri);
            Log.d("TAG", "came here");
            cameraClicked=true;

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void onPhotoPickerItemSelected(int item) {
        Intent intent;

        switch (item) {

            case 0:
                changeImage();
                break;

            case 1:
                intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, REQUEST_CODE_SELECT_FROM_GALLERY);
                break;

            default:
                return;
        }    }
}
