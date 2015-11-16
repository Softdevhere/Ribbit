package com.example.sony.ribbit.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.sony.ribbit.R;
import com.example.sony.ribbit.adapters.UserAdapter;
import com.example.sony.ribbit.helper.ParseQueries;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by SONY on 20.09.2015.
 */
public class FriendsFragment extends Fragment {
    private UserAdapter mFriendsAdapter;
    private List<ParseUser> mFriends;
    private String[] mFriendsNames;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private String mFriendFullName;
    private String mFriendCity;
    private String mFriendWWW;
    private ParseQueries mParseQueries;
    private GridView mFriendGrid;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);
        mFriendGrid = (GridView) rootView.findViewById(R.id.friendGrid);
        mFriendGrid.setEmptyView(rootView.findViewById(android.R.id.empty));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser=ParseUser.getCurrentUser();
        mParseQueries = new ParseQueries(getActivity());
        mFriends=mParseQueries.getFriendsObjects(mCurrentUser);
        mFriendsNames=mParseQueries.getFriends(mCurrentUser);
        if(mFriendsNames!=null) {
            if (mFriendsAdapter==null) {
                mFriendsAdapter = new UserAdapter(getActivity(), mFriends);
                mFriendGrid.setAdapter(mFriendsAdapter);
            }else
            {
                ((UserAdapter)mFriendGrid.getAdapter()).refill(mFriends);
            }
            mFriendGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        mFriendFullName = mFriends.get(position).getString("FName") + " " + mFriends.get(position).getString("LName");
                        mFriendCity = mFriends.get(position).getString("City");
                        mFriendWWW = mFriends.get(position).getString("WWW");
                    } catch (NullPointerException npe) {
                        Log.i("OnClick", npe.getMessage());
                    }


                    Intent intent = new Intent(getContext(), FriendProfile.class);
                    intent.putExtra("fullName", mFriendFullName);
                    intent.putExtra("city", mFriendCity);
                    intent.putExtra("WWW", mFriendWWW);

                    startActivity(intent);
                }
            });

        }

    }


//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        try{
//            mFriendFullName = mFriends.get(position).getString("FName") + " " + mFriends.get(position).getString("LName");
//            mFriendCity = mFriends.get(position).getString("City");
//            mFriendWWW = mFriends.get(position).getString("WWW");
//        }catch (NullPointerException npe){
//            Log.i("OnClick", npe.getMessage());
//        }
//
//
//        Intent intent = new Intent(getContext(),FriendProfile.class);
//        intent.putExtra("fullName",mFriendFullName);
//        intent.putExtra("city", mFriendCity);
//        intent.putExtra("WWW", mFriendWWW);
//
//        startActivity(intent);
//
//    }
}
