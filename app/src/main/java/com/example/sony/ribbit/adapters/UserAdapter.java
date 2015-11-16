package com.example.sony.ribbit.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sony.ribbit.R;
import com.example.sony.ribbit.helper.MD5Util;
import com.example.sony.ribbit.helper.PARSE_CONSTANTS;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by SONY on 25.10.2015.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {
    private Context mContext;
    private List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.message_item, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        GridView gridView = (GridView)parent;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            viewHolder = new ViewHolder();
            viewHolder.userImageView = (ImageView) convertView.findViewById(R.id.friendImageView);
            viewHolder.checkImageView = (ImageView) convertView.findViewById(R.id.checkMarkImageView);
            viewHolder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ParseUser user = mUsers.get(position);

        String friendEmail = user.getEmail().toLowerCase();

        if(friendEmail.equals("")){
            viewHolder.userImageView.setImageResource(R.drawable.avatar_empty);
        } else {
            String emailHash = MD5Util.md5Hex(friendEmail);
            String gavatarUrl = "http://www.gravatar.com/avatar/" + emailHash + "s204%d=404";
            Log.d("Test",gavatarUrl);
            Picasso.with(mContext)
                    .load(gavatarUrl)
                    .placeholder(R.drawable.avatar_empty)
                    .into(viewHolder.userImageView);
        }

        viewHolder.nameLabel.setText(user.getString("FName") + " " + user.getString("LName"));

        if(gridView.isItemChecked(position)){
            viewHolder.checkImageView.setVisibility(View.VISIBLE);
        }else {
            viewHolder.checkImageView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView checkImageView;
        ImageView userImageView;
        //TextView messageItemTextView;
        TextView nameLabel;
    }

    public void refill(List<ParseUser> users){
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}
