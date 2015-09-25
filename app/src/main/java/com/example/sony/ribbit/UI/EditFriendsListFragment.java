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
import android.widget.Toast;

import com.example.sony.ribbit.R;
import com.example.sony.ribbit.helper.PARSE_CONSTANTS;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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


    private OnFragmentInteractionListener mListener;




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

        mProgressBar = (EditFriendsListFragment.actionBarInterface) getActivity();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(PARSE_CONSTANTS.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(this);

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_edit_friends_list,null);
    }


    //    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
