package com.example.sony.ribbit.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sony.ribbit.R;
import com.example.sony.ribbit.helper.PARSE_CONSTANTS;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditFriendsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditFriendsListFragment} factory method to
 * create an instance of this fragment.
 */
public class EditFriendsListFragment extends ListFragment implements FindCallback<ParseUser> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private  List<ParseUser> mUsers;
    private EditFriendsListFragment.actionBarInterface mProgressBar;
    private ParseRelation<ParseUser> mUserParseRelation;
    private ParseUser mCurrentUser;


    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("OnAttach", "passed");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("onCreateView", "passed");


        return inflater.inflate(R.layout.fragment_edit_friends_list,null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar = (EditFriendsListFragment.actionBarInterface) getActivity();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(PARSE_CONSTANTS.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(this);

        setHasOptionsMenu(true);

        mCurrentUser = ParseUser.getCurrentUser();

        mUserParseRelation = mCurrentUser.getRelation(PARSE_CONSTANTS.Key_FRIENDS_RELATION);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void done(List<ParseUser> list, ParseException e) {
        Log.i("done", "query_passed");
        mProgressBar.progressBar(false);
        if (e == null) {
            Toast toast = Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG);
            toast.show();
            mUsers = list;
            String[] usernames = new String[mUsers.size()];
            int i = 0;
            for (ParseUser user : mUsers) {
                usernames[i] = user.getUsername();
                i++;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_checked, usernames);
            setListAdapter(adapter);
            addFriendCheckmarks();


        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(super.getContext());
            builder.setTitle("Friends list error");
            builder.setMessage("An error: " + e.getMessage() + " has occured");
            builder.setPositiveButton("Ok", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    public interface actionBarInterface{
        void progressBar(Boolean toggle);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.i("OnListItemClick Log", "clicked");

        if(getListView().isItemChecked(position)){
            //add friend
            mUserParseRelation.add(mUsers.get(position));

        }else{
            //remove friend
            mUserParseRelation.remove(mUsers.get(position));
        }

        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.i("PARSE USER SAVING", e.getMessage());
                }
            }
        });
    }

    private void addFriendCheckmarks(){
        Log.i("addFriendsCheckMarks","started");
        mUserParseRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if(e==null){
                    //we have the list
                    for(ParseUser user:list) {
                        for (int i = 0; i < getListView().getCount(); i++) {
                            if (mUsers.get(i).getObjectId().equals(user.getObjectId())) {
                                Log.i("addFriendsCheckMarks", "found a friend:" + user.getUsername());
                                getListView().setItemChecked(i, true);
                                break;
                            }
                        }
                    }
                }else{
                    Log.i("addFriendsCheckMarks",e.getMessage());
                }
            }
        });

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



}
