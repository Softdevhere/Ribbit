package com.example.sony.ribbit.UI;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.example.sony.ribbit.R;

public class EditFriendsActivity extends AppCompatActivity implements EditFriendsListFragment.actionBarInterface{


    private ListFragment mListFragment;
    private MenuItem mActionBarProgress;
    private Boolean fragmentIsFull;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_friends);
            //supportInvalidateOptionsMenu();
            fragmentIsFull=false;



            try {
                getActionBar().setDisplayHomeAsUpEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void fillFragment() {
        if(!fragmentIsFull){
            mListFragment= new EditFriendsListFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.listFragment, mListFragment);
            ft.commit();
            fragmentIsFull=true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
        Log.i("oncreateoptions", "passed");




        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case(R.id.home):
                    NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i("OnPrepare","Stage One");

        // Store instance of the menu item containing progress
        mActionBarProgress = menu.findItem(R.id.edit_user_progressbar);
        if(mActionBarProgress==null)Log.i("oncreateoption", "another mistake");
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(mActionBarProgress);
        if(mActionBarProgress!=null){
            fillFragment();
        }

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void progressBar(Boolean toggle) {
        mActionBarProgress.setVisible(toggle);
    }
}
