package com.example.sony.ribbit.UI;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sony.ribbit.R;
import com.example.sony.ribbit.helper.PARSE_CONSTANTS;
import com.example.sony.ribbit.helper.ParseQueries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by SONY on 12.10.2015.
 */
public class ReceipintsListFragment extends ListFragment{
    private  List<ParseUser> mFriends;
    private EditFriendsListFragment.actionBarInterface mProgressBar;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private String[] mFriendsNames ;
    private ArrayAdapter<String> mFriendsAdapter;
    ParseQueries mParseQueries;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.receipints_list_fragment,null);
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser=ParseUser.getCurrentUser();
        Log.i("onResume", "current user: " + mCurrentUser.getUsername());
        mParseQueries = new ParseQueries(getActivity());
        mFriendsNames=mParseQueries.getFriends(mCurrentUser);
        if(mFriendsNames==null)Log.i("onResume", "Helper method failed ");
        mFriendsAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, mFriendsNames);
        setListAdapter(mFriendsAdapter);
    }



}
