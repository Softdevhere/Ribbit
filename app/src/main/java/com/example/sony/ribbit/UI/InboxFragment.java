package com.example.sony.ribbit.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sony.ribbit.R;
import com.example.sony.ribbit.helper.MessageAdapter;
import com.example.sony.ribbit.helper.PARSE_CONSTANTS;
import com.example.sony.ribbit.helper.ParseQueries;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SONY on 20.09.2015.
 */
public class InboxFragment extends ListFragment {
    List<ParseObject> mMessages;
    ParseQueries mParseQueries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParseQueries = new ParseQueries(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setProgressBarIndeterminateVisibility(true);

        //mMessages = mParseQueries.getParseObjects(PARSE_CONSTANTS.CLASS_MESSAGES);
        ParseQuery<ParseObject> messageQuery = new ParseQuery<>(PARSE_CONSTANTS.CLASS_MESSAGES);
        messageQuery.whereEqualTo(PARSE_CONSTANTS.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        messageQuery.addDescendingOrder("createdAt");
        messageQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    mMessages = list;
                    getActivity().setProgressBarIndeterminateVisibility(false);
                    int i = 0;
                    if (mMessages.size() > 0) {
                        String[] messageSender = new String[mMessages.size()];
                        for (ParseObject message : mMessages) {
                            Log.i("Parse", message.getString(PARSE_CONSTANTS.KEY_SENDER_NAME) + " i = " + i);
                            messageSender[i] = message.getString(PARSE_CONSTANTS.KEY_SENDER_NAME);
                            i++;
                        }
                        /*ArrayAdapter<String> inboxAdapter = new ArrayAdapter<>(getListView().getContext(),
                                android.R.layout.simple_list_item_1, messageSender);
                        setListAdapter(inboxAdapter);*/

                        if(getListView().getAdapter()==null) {
                            MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                            setListAdapter(adapter);
                        }else{
                            ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                        }


                    }
                } else {
                    Log.i("Objects request", e.getMessage());

                }
            }
        });

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String fileType = message.getString(PARSE_CONSTANTS.KEY_FILE_TYPE);

        if (fileType.equals(PARSE_CONSTANTS.TYPE_IMAGE) || fileType.equals(PARSE_CONSTANTS.TYPE_VIDEO)) {
            ParseFile file = message.getParseFile(PARSE_CONSTANTS.KEY_FILE);
            Uri fileUri = Uri.parse(file.getUrl());
            if (fileType.equals(PARSE_CONSTANTS.TYPE_IMAGE)) {
                Intent intent = new Intent(getActivity(), ImageViewActivity.class);
                intent.setData(fileUri);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
                intent.setDataAndType(fileUri, "video/*");
                startActivity(intent);
            }
        } else {
            ParseFile messageFile = (ParseFile) message.get(PARSE_CONSTANTS.KEY_TEXT_MESSAGE);
            messageFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        String messageBody = new String((bytes));
                        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                        builder.setMessage(messageBody);
                        builder.setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Log.i("Extracting text message", e.getMessage());
                    }
                }
            });

        }
        List<String> recipientsID = message.getList(PARSE_CONSTANTS.KEY_RECIPIENT_IDS);

        if (recipientsID.size()==1){
            message.deleteInBackground();
        }else {
            recipientsID.remove(ParseUser.getCurrentUser().getObjectId());

            List<String> idsToRemove = new ArrayList<>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(PARSE_CONSTANTS.KEY_RECIPIENT_IDS,idsToRemove);

            message.saveInBackground();

        }

    }
}
