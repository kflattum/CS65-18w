package com.varunmishra.myruns1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.soundcloud.android.crop.Crop;

public class MainActivity extends Activity{

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_CROP_PHOTO = 2;

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final String URI_INSTANCE_STATE_KEY_TEMP = "saved_uri_temp";
    private static final String CAMERA_CLICKED_KEY = "clicked";


    private Button mBtnChangeImage, mBtnSave, mBtnCancel;
    private ImageView mProfileImage;
    private Uri mImageCaptureUri, mTempUri;
    public static Boolean stateChnaged = false, cameraClicked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
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
                changeImage();
            }
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

    private void checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //start audio recording or whatever you planned to do
        }else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)||shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                            }

                        }
                    });
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                }else{
                    //Never ask again and handle your app without permission.
                }
            }
        }
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
                } else {
                    fis = openFileInput(getString(R.string.profile_photo_file_name));
                    Bitmap bmap = BitmapFactory.decodeStream(fis);
                    mProfileImage.setImageBitmap(bmap);

                    fis.close();
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
        cameraClicked = false;
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
                break;

            case Crop.REQUEST_CROP:
                // Update image view after image crop
                // Set the picture image in UI
                handleCrop(resultCode, data);

                Log.d("TAG", mImageCaptureUri.getPath());
                // Delete temporary image taken by camera after crop.
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists())
                        f.delete();

                break;
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //if (cameraClicked==true) {
            // Save the image capture uri before the activity goes into background
            outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
            outState.putParcelable(URI_INSTANCE_STATE_KEY_TEMP, mTempUri);
            outState.putBoolean(CAMERA_CLICKED_KEY, cameraClicked);
        //}
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


}
