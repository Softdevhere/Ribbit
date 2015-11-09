package com.example.sony.ribbit;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by SONY on 10.09.2015.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i("NEW Application class", "New app is used");
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "uz8EYZUhYgzyvd7WX0OzzGZTGvg8QWspdq4kDdXJ", "zTvcv2FDlFO23geWWgICpuKubjgTsDd2Veu1CbJx");


    }
}
