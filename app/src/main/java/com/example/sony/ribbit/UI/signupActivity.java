package com.example.sony.ribbit.UI;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sony.ribbit.R;
import com.example.sony.ribbit.RibbitApplication;
import com.example.sony.ribbit.helper.Credentials;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

public class signupActivity extends AppCompatActivity {

    @Bind(R.id.usernameSignUpLabel) EditText mUserSignUp;
    @Bind(R.id.passwordSignupLabel) EditText mPasswordSignUp;
    @Bind(R.id.emailSignupLabel) EditText mEmailSignUp;
    @Bind(R.id.firstNameLabel) EditText mFirstNameLabel;
    @Bind(R.id.signupUserLNameLabel) EditText mLastNameLabel;
    @Bind(R.id.signupUserCityLabel) EditText mCityLabel;
    @Bind(R.id.signupUserWWWLabel) EditText mWWWLabel;
    @Bind(R.id.signUpButton)     Button mSignUpButton;
    @Bind(R.id.signUpCancellButton) Button mCancelButton;

    private String mUserName;
    private String mPassword;
    private String mEmail;
    private String mFName;
    private String mLName;
    private String mCity;
    private String mWWW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ButterKnife.bind(this);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserName = mUserSignUp.getText().toString();
                mPassword = mPasswordSignUp.getText().toString();
                mEmail = mEmailSignUp.getText().toString();
                mFName = mFirstNameLabel.getText().toString();
                mLName = mLastNameLabel.getText().toString();
                mCity = mCityLabel.getText().toString();
                mWWW = mWWWLabel.getText().toString();


                Credentials credentials = new Credentials(mUserName, mPassword, mEmail);
                if(!credentials.checkCredentials()){
                    AlertDialog.Builder wrongCredentialsDialogBuilder = new AlertDialog.Builder(signupActivity.this);
                    wrongCredentialsDialogBuilder.setMessage(R.string.wrongCredentialsAlertMessage);
                    wrongCredentialsDialogBuilder.setTitle(R.string.wrongCredentialsAlertTitle);
                    wrongCredentialsDialogBuilder.setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = wrongCredentialsDialogBuilder.create();
                    dialog.show();
                }else{
                    ParseUser user = new ParseUser();
                    user.setUsername(mUserName);
                    user.setPassword(mPassword);
                    user.setEmail(mEmail);
                    user.put("FName", mFName);
                    user.put("LName", mLName);
                    user.put("City", mCity);
                    user.put("WWW",mWWW);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Log.i("New User", "New user created");
                                RibbitApplication.updateParseInstallation(ParseUser.getCurrentUser());

                                Intent intent = new Intent(signupActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else{
                                AlertDialog.Builder wrongCredentialsDialogBuilder = new AlertDialog.Builder(signupActivity.this);
                                wrongCredentialsDialogBuilder.setMessage(e.getMessage());
                                wrongCredentialsDialogBuilder.setTitle(R.string.wrongCredentialsAlertTitle);
                                wrongCredentialsDialogBuilder.setPositiveButton(android.R.string.ok, null);

                                AlertDialog dialog = wrongCredentialsDialogBuilder.create();
                                dialog.show();
                            }
                        }
                    });

                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
