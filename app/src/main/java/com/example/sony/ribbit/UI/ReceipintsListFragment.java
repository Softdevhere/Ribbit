package com.example.sony.ribbit.UI;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.sony.ribbit.helper.FileHelper;
import com.example.sony.ribbit.helper.PARSE_CONSTANTS;
import com.example.sony.ribbit.helper.ParseQueries;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
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
    private ParseQueries mParseQueries;
    private MenuItem mSendItem;
    private Uri mMediaUri;
    private String mFileType;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mMediaUri = getActivity().getIntent().getData();
        mFileType = getActivity().getIntent().getStringExtra(PARSE_CONSTANTS.KEY_FILE_TYPE);
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
        mFriends=mParseQueries.getFriendsObjects(mCurrentUser);
        mFriendsNames=mParseQueries.getFriends(mCurrentUser);
        if(mFriends!=null && mFriendsNames != null) {
            mFriendsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_checked, mFriendsNames);
            setListAdapter(mFriendsAdapter);
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Add friends first");
            builder.setPositiveButton("Ok", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_recipients, menu);
        mSendItem=menu.findItem(R.id.action_send);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_send:
                ParseObject message = createMessage();
                if(message==null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.sending_message_file_error)
                            .setTitle(R.string.error_title)
                            .setPositiveButton("Ok",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    sendMessage(message);
                    getActivity().finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Log.i("Message", "Message was sent");

                }else{

                    Log.i("Send Error",e.getMessage());
                }

            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(l.getCheckedItemCount()>0){
            mSendItem.setVisible(true);
        }else {
            mSendItem.setVisible(false);
        }


    }

    protected ParseObject createMessage(){
        ParseObject message = new ParseObject(PARSE_CONSTANTS.CLASS_MESSAGES);
        message.put(PARSE_CONSTANTS.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
        message.put(PARSE_CONSTANTS.KEY_SENDER_NAME,ParseUser.getCurrentUser().getString(PARSE_CONSTANTS.KEY_FIRST_NAME) + " " +
                                                    ParseUser.getCurrentUser().getString(PARSE_CONSTANTS.KEY_LAST_NAME));
        message.put(PARSE_CONSTANTS.KEY_RECIPIENT_IDS, getRecipientIds());

        byte[] fileBytes = FileHelper.getByteArrayFromFile(getActivity(), mMediaUri);
        if(fileBytes==null){
            return null;
        }else{
            if(mFileType.equals(PARSE_CONSTANTS.TYPE_IMAGE)){
                FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(getActivity(),mMediaUri,mFileType);
            ParseFile file = new ParseFile(fileName,fileBytes);
            message.put(PARSE_CONSTANTS.KEY_FILE,file);
            return message;
        }
    }

    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for(int i=0; i<getListView().getCount(); i++){
            if(getListView().isItemChecked(i)) recipientIds.add(mFriends.get(i).getObjectId());
        }
        return recipientIds;
    }


}
