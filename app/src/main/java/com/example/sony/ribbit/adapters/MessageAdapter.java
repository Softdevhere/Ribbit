package com.example.sony.ribbit.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sony.ribbit.R;
import com.example.sony.ribbit.helper.PARSE_CONSTANTS;
import com.google.android.gms.plus.model.moments.Moment;
import com.parse.ParseObject;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

/**
 * Created by SONY on 25.10.2015.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {
    private Context mContext;
    private List<ParseObject> mMessages;

    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_item, messages);
        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            viewHolder.messageItemTextView = (TextView) convertView.findViewById(R.id.senderNameLabel);
            viewHolder.timeSent = (TextView) convertView.findViewById(R.id.timeSentLabel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ParseObject message = mMessages.get(position);


        if (message.getString(PARSE_CONSTANTS.KEY_FILE_TYPE).equals(PARSE_CONSTANTS.TYPE_IMAGE)) {
            viewHolder.iconImageView.setImageResource(R.drawable.ic_picture);
        } else if (message.getString(PARSE_CONSTANTS.KEY_FILE_TYPE).equals(PARSE_CONSTANTS.TYPE_VIDEO)) {
            viewHolder.iconImageView.setImageResource(R.drawable.ic_video);
        } else {
            viewHolder.iconImageView.setImageResource(android.R.drawable.ic_menu_agenda);
        }
        viewHolder.messageItemTextView.setText(message.getString(PARSE_CONSTANTS.KEY_SENDER_NAME));
        Date timeSent = message.getCreatedAt();
        long timeSentInMiliseconds = timeSent.getTime();
        String timeSentRelative = DateUtils.getRelativeTimeSpanString(
                timeSentInMiliseconds,
                new Date().getTime(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        viewHolder.timeSent.setText(timeSentRelative);


        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView messageItemTextView;
        TextView timeSent;
    }

    public void refill(List<ParseObject> messages){
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }
}
