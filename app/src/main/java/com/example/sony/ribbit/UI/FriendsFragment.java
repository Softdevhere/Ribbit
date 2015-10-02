package com.example.sony.ribbit.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sony.ribbit.R;
import com.example.sony.ribbit.helper.PARSE_CONSTANTS;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by SONY on 20.09.2015.
 */
public class FriendsFragment extends ListFragment {
    private ArrayAdapter<String> mFriendsAdapter;
    private List<ParseUser> mFriends;
    private String[] mFriendsNames;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private String mFriendFullName;
    private String mFriendCity;
    private String mFriendWWW;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser=ParseUser.getCurrentUser();
        mFriendsRelation=mCurrentUser.getRelation(PARSE_CONSTANTS.Key_FRIENDS_RELATION);
        ParseQuery<ParseUser> query= mFriendsRelation.getQuery();
        query.addAscendingOrder(PARSE_CONSTANTS.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    mFriends = list;
                    int i = 0;
                    mFriendsNames = new String[list.size()];
                    for (ParseUser user : list) {
                        mFriendsNames[i] = user.getUsername();
                        i++;
                    }
                    mFriendsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mFriendsNames);
                    setListAdapter(mFriendsAdapter);

                } else {
                    Log.i("Friends.onResume", e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setTitle("Friends list error");
                    builder.setMessage("An error: " + e.getMessage() + " has occured");
                    builder.setPositiveButton("Ok", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try{
            mFriendFullName = mFriends.get(position).getString("FName") + " " + mFriends.get(position).getString("LName");
            mFriendCity = mFriends.get(position).getString("City");
            mFriendWWW = mFriends.get(position).getString("WWW");
        }catch (NullPointerException npe){
            Log.i("OnClick", npe.getMessage());
        }


        Intent intent = new Intent(getContext(),FriendProfile.class);
        intent.putExtra("fullName",mFriendFullName);
        intent.putExtra("city", mFriendCity);
        intent.putExtra("WWW", mFriendWWW);

        startActivity(intent);

    }
}
