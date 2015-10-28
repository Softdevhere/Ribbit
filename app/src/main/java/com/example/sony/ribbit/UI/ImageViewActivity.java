package com.example.sony.ribbit.UI;

import android.app.ActionBar;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.sony.ribbit.R;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

public class ImageViewActivity extends AppCompatActivity {
    Uri mFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        mFileUri=getIntent().getData();


        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        Picasso.with(this).load(mFileUri.toString()).into(imageView);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        },10*1000);


    }
}
