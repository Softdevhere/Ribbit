package com.example.sony.ribbit.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;

import com.example.sony.ribbit.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendProfile extends AppCompatActivity {
    private String mFullName;
    private String mCity;
    private String mWWW;
    @Bind(R.id.fullNameLabel) TextView mFullNameLabel;
    @Bind(R.id.cityLabel) TextView mCityLabel;
    @Bind(R.id.wwwLabel) TextView mWWWLabel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        Intent intent = getIntent();
        mFullName = intent.getStringExtra("fullName");
        mCity = intent.getStringExtra("city");
        mWWW = intent.getStringExtra("WWW");
        ButterKnife.bind(this);

        mFullNameLabel.setText(mFullName);
        mCityLabel.setText(mCity);
        mWWWLabel.setText(mWWW);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_friend_profile, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
