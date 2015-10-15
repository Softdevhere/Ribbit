package com.example.sony.ribbit.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by SONY on 12.10.2015.
 */
public class ParseQueries {
    protected List<ParseUser> mFriends;
    protected String[] mFriendsNames;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mParseCurrentUser;
    protected Context mContext;

    public ParseQueries(Context context){
        mContext=context;
        Log.i("Parse", "Parse started");
    }

    public String[] getFriends(ParseUser current){
            Log.i("Parse", "getFriends started");
            mParseCurrentUser =current;
            mFriendsRelation= mParseCurrentUser.getRelation(PARSE_CONSTANTS.Key_FRIENDS_RELATION);
            ParseQuery<ParseUser> queryParse= mFriendsRelation.getQuery();
            queryParse.addAscendingOrder(PARSE_CONSTANTS.KEY_USERNAME);
            Log.i("Parse", mParseCurrentUser.getUsername());
        try {
            mFriends=queryParse.find();
            Log.i("Parse", "request completed " + mFriends.toString());
                        int i = 0;
                        mFriendsNames = new String[mFriends.size()];
                        for (ParseUser user : mFriends) {
                            Log.i("Parse",user.getUsername() + " i = " + i);
                            mFriendsNames[i] = user.getUsername();
                            i++;

                        }
                        Log.i("Parse", mFriendsNames[0].toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

//            queryParse.findInBackground(new FindCallback<ParseUser>() {
//                int started = 1;
//
//                @Override
//                public void done(List<ParseUser> list, ParseException e) {
//                    Log.i("Parse", "request completed " + list.toString());
//                    if (e == null) {
//                        mFriends = list;
//
//                        Log.i("Parse", "request completed " + list.toString());
//                        int i = 0;
//                        mFriendsNames = new String[list.size()];
//                        for (ParseUser user : mFriends) {
//                            Log.i("Parse",user.getUsername() + " i = " + i);
//                            mFriendsNames[i] = user.getUsername();
//                            i++;
//
//                        }
//                        Log.i("Parse", mFriendsNames[0].toString());
//
//
//                    } else {
//                        Log.i("Friends.onResume", e.getMessage());
//                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                        builder.setTitle("Friends list error");
//                        builder.setMessage("An error: " + e.getMessage() + " has occured");
//                        builder.setPositiveButton("Ok", null);
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
//                    }
//
//                }
//
//            });
//        //mFriendsNames = new String[]{"John", "Bob", "Tesla"};
////        int timer =0;
////        while(mFriendsNames==null)timer++;
        Log.i("Parse","Final mFriend " + mFriendsNames[0]);
        return mFriendsNames;
    }
}
