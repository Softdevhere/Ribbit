package com.example.sony.ribbit.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sony.ribbit.R;
import com.example.sony.ribbit.UI.LoginActivity;
import com.example.sony.ribbit.helper.PARSE_CONSTANTS;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static final int REQUEST_CODE_TAKE_PHOTO = 0;
    public static final int REQUEST_CODE_TAKE_VIDEO = 1;
    public static final int REQUEST_CODE_CHOOSE_PHOTO = 2;
    public static final int REQUEST_CODE_CHOOSE_VIDEO = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;
    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10; //10MB

    protected Uri mMediaUri;

    protected AlertDialog.OnClickListener mCameraOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0:
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                    if (mMediaUri.equals(null)){
                        Toast.makeText(MainActivity.this, R.string.external_memory_access_error_message, Toast.LENGTH_LONG).show();
                    }else {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePhotoIntent, REQUEST_CODE_TAKE_PHOTO);
                    }
                    break;
                case 1:
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFile(MEDIA_TYPE_VIDEO);
                    if (mMediaUri.equals(null)){
                        Toast.makeText(MainActivity.this, R.string.external_memory_access_error_message, Toast.LENGTH_LONG).show();
                    }else {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
                        startActivityForResult(videoIntent, REQUEST_CODE_TAKE_VIDEO);
                    }

                    break;
                case 2:
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent,REQUEST_CODE_CHOOSE_PHOTO);
                    break;
                case 3:
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, R.string.video_file_restriction_alert,Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, REQUEST_CODE_CHOOSE_VIDEO);
                    break;
            }

        }

        private Uri getOutputMediaFile(int mediaTypeImage) {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            File mediaFile=null;
            if(isExternalStorageMounted()){
                String appName = MainActivity.this.getString(R.string.app_name);
                //1.Get external storage directory
                File mediaStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                                        appName);

                //2.Create subdirectory
                if(!mediaStorageDirectory.exists()){
                    if(!mediaStorageDirectory.mkdir()){
                        Log.i("mediaStorageDirectory", "There was an error creating directory");
                        return null;
                    }
                }
                //3.Create file name

                Date now = new Date();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(now);
                String path = mediaStorageDirectory.getPath() + File.separator;
                if (mediaTypeImage==MEDIA_TYPE_IMAGE){
                    mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");
                }else if (mediaTypeImage==MEDIA_TYPE_VIDEO){
                    mediaFile = new File(path + "VID_" + timeStamp + ".mp4");
                }else{
                    return null;
                }
                Log.i("Filepath","File: " + Uri.fromFile(mediaFile));

            }
            return Uri.fromFile(mediaFile);
        }

        private boolean isExternalStorageMounted(){
            String state = Environment.getExternalStorageState();
            if(state.equals(Environment.MEDIA_MOUNTED)){
                return true;
            }else {
                return false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ParseUser currentUser = ParseUser.getCurrentUser();


        if (currentUser==null) {
            navigate_to_login();
        }else{
            Log.e("Logged in", currentUser.getUsername());
        }



        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            //Place to the gallery
            if(requestCode==REQUEST_CODE_CHOOSE_PHOTO || requestCode==REQUEST_CODE_CHOOSE_VIDEO) {
                if (data==null){
                    Toast.makeText(MainActivity.this,"no Photo selected",Toast.LENGTH_LONG);
                }else {
                    mMediaUri = data.getData();
                    if(requestCode==REQUEST_CODE_CHOOSE_VIDEO){
                        //Check the file size
                        int fileSize=0;
                        Log.i("mMediaUri", "MediaUri: " + mMediaUri);
                        InputStream inputStream=null;
                        try {
                            inputStream = getContentResolver().openInputStream(mMediaUri);
                            fileSize = inputStream.available();
                        }catch (FileNotFoundException fnf){
                            Toast.makeText(this,R.string.file_not_found_error,Toast.LENGTH_LONG).show();
                            return;

                        }catch (IOException ioe){
                            Toast.makeText(this,R.string.file_not_found_error,Toast.LENGTH_LONG).show();
                            return;
                        }
                        finally {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        if(fileSize>=FILE_SIZE_LIMIT){
                            Toast.makeText(this, R.string.file_too_large,Toast.LENGTH_LONG).show();
                            Log.i("File size", "File size is: " + fileSize + ", must be: " + FILE_SIZE_LIMIT);
                            return;
                        }

                    }
                }
            }else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

            Intent recipientsIntent = new Intent(this, ReceipientsActivity.class);
            recipientsIntent.setData(mMediaUri);
            String fileType;
            if(requestCode==REQUEST_CODE_TAKE_PHOTO || requestCode==REQUEST_CODE_CHOOSE_PHOTO){
                fileType=PARSE_CONSTANTS.TYPE_IMAGE;
            }else {
                fileType=PARSE_CONSTANTS.TYPE_VIDEO;
            }
            recipientsIntent.putExtra(PARSE_CONSTANTS.KEY_FILE_TYPE,fileType);
            startActivity(recipientsIntent);

        }else if(resultCode!=RESULT_CANCELED){
            Toast.makeText(this, R.string.taking_photo_error, Toast.LENGTH_LONG);
        }
    }

    private void navigate_to_login() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
        switch (id) {
            case (R.id.action_logout):
                ParseUser.logOut();
                navigate_to_login();

                return true;
            case (R.id.action_edit_friends):
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
            case (R.id.action_camera):
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mCameraOnClickListener);
                AlertDialog dialog = builder.create();
                dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
}
