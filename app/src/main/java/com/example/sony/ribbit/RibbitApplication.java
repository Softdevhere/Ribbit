package com.example.sony.ribbit;

import android.app.Application;
import android.util.Log;

import com.example.sony.ribbit.helper.PARSE_CONSTANTS;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

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

        ParseInstallation.getCurrentInstallation().saveInBackground();


    }

    public static void updateParseInstallation(ParseUser parseUser) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(PARSE_CONSTANTS.KEY_USER_ID,parseUser.getObjectId());
        installation.saveInBackground();
    }
}
