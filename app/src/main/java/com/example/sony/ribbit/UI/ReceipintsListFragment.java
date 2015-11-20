package com.example.sony.ribbit.UI;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.sony.ribbit.R;
import com.example.sony.ribbit.adapters.UserAdapter;
import com.example.sony.ribbit.helper.FileHelper;
import com.example.sony.ribbit.helper.PARSE_CONSTANTS;
import com.example.sony.ribbit.helper.ParseQueries;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SONY on 12.10.2015.
 */
public class ReceipintsListFragment extends Fragment {
    private List<ParseUser> mFriends;
    private EditFriendsListFragment.actionBarInterface mProgressBar;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private String[] mFriendsNames;
    private ParseQueries mParseQueries;
    private MenuItem mSendItem;
    private Uri mMediaUri;
    private String mFileType;
    private String mMessage;
    private ParseFile mMessageFile;
    private int mMessageType;
    private GridView mFriendGrid;
    private UserAdapter mFriendsAdapter;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext=getActivity();
        mMediaUri = getActivity().getIntent().getData();
        mFileType = getActivity().getIntent().getStringExtra(PARSE_CONSTANTS.KEY_FILE_TYPE);
        mMessage = getActivity().getIntent().getStringExtra("message");
        if (mMessage != null) {
            byte[] messageBytes = mMessage.getBytes();
            mMessageFile = new ParseFile("textMessage.txt", messageBytes);
            mMessageType = PARSE_CONSTANTS.MESSAGE_TYPE_TXT;
        } else {
            mMessageType = PARSE_CONSTANTS.MESSAGE_TYPE_MEDIA;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFriendGrid.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.user_grid, null);
        mFriendGrid = (GridView) layoutView.findViewById(R.id.friendGrid);
        mFriendGrid.setEmptyView(layoutView.findViewById(android.R.id.empty));
        return layoutView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        Log.i("onResume", "current user: " + mCurrentUser.getUsername());
        mParseQueries = new ParseQueries(getActivity());
        mFriends = mParseQueries.getFriendsObjects(mCurrentUser);
        mFriendsNames = mParseQueries.getFriends(mCurrentUser);

        if (mFriends != null && mFriendsNames != null) {
            if (mFriendsAdapter == null) {
                mFriendsAdapter = new UserAdapter(getActivity(), mFriends);
                mFriendGrid.setAdapter(mFriendsAdapter);
            } else {
                ((UserAdapter) mFriendGrid.getAdapter()).refill(mFriends);
            }
            mFriendGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.checkMarkImageView);
                    if (mFriendGrid.isItemChecked(position)) {
                        imageView.setVisibility(View.VISIBLE);
                    } else {
                        imageView.setVisibility(View.INVISIBLE);
                    }
                    if (mFriendGrid.getCheckedItemCount() > 0) {
                        mSendItem.setVisible(true);

                    } else {
                        mSendItem.setVisible(false);
                    }
                }
            });
        } else {
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
        mSendItem = menu.findItem(R.id.action_send);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                ParseObject message = createMessage();
                if (message == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.sending_message_file_error)
                            .setTitle(R.string.error_title)
                            .setPositiveButton("Ok", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
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
                if (e == null) {
                    Log.i("Message", "Message was sent");
                    sendPushNotification();
                } else {

                    Log.i("Send Error", e.getMessage());
                }

            }
        });
    }

    private void sendPushNotification() {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(PARSE_CONSTANTS.KEY_USER_ID, getRecipientIds());

        //send push
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(mContext.getString(R.string.push_new_message_recipients,
                ParseUser.getCurrentUser().getString(PARSE_CONSTANTS.KEY_FIRST_NAME) +
                        ParseUser.getCurrentUser().getString(PARSE_CONSTANTS.KEY_LAST_NAME)));

        push.sendInBackground();
    }


    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(PARSE_CONSTANTS.CLASS_MESSAGES);
        message.put(PARSE_CONSTANTS.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(PARSE_CONSTANTS.KEY_SENDER_NAME, ParseUser.getCurrentUser().getString(PARSE_CONSTANTS.KEY_FIRST_NAME) + " " +
                ParseUser.getCurrentUser().getString(PARSE_CONSTANTS.KEY_LAST_NAME));
        message.put(PARSE_CONSTANTS.KEY_RECIPIENT_IDS, getRecipientIds());
        switch (mMessageType) {
            case 1:
                message.put(PARSE_CONSTANTS.KEY_TEXT_MESSAGE, mMessageFile);
                message.put(PARSE_CONSTANTS.KEY_FILE_TYPE, PARSE_CONSTANTS.KEY_TEXT_MESSAGE);
                break;
            case 2:
                byte[] fileBytes = FileHelper.getByteArrayFromFile(getActivity(), mMediaUri);
                if (fileBytes == null) {
                    return null;
                } else {
                    if (mFileType.equals(PARSE_CONSTANTS.TYPE_IMAGE)) {
                        FileHelper.reduceImageForUpload(fileBytes);
                    }
                    String fileName = FileHelper.getFileName(getActivity(), mMediaUri, mFileType);
                    ParseFile file = new ParseFile(fileName, fileBytes);
                    if (mFileType.equals(PARSE_CONSTANTS.TYPE_IMAGE)) {
                        message.put(PARSE_CONSTANTS.KEY_FILE_TYPE, PARSE_CONSTANTS.TYPE_IMAGE);
                    } else {
                        message.put(PARSE_CONSTANTS.KEY_FILE_TYPE, PARSE_CONSTANTS.TYPE_VIDEO);
                    }
                    message.put(PARSE_CONSTANTS.KEY_FILE, file);
                }
        }
        return message;
    }

    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<>();
        for (int i = 0; i < mFriendGrid.getCount(); i++) {
            if (mFriendGrid.isItemChecked(i)) recipientIds.add(mFriends.get(i).getObjectId());
        }
        return recipientIds;
    }
}
