package com.example.sony.ribbit.helper;

/**
 * Created by SONY on 11.09.2015.
 */
public class Credentials {

    private String mUserName="";
    private String mPassword="";
    private String mEmailAddress="";
    private int mIndex=0;


    public Credentials(String userNameString, String passwordString){
        mUserName=userNameString;
        mPassword=passwordString;
        mIndex=1; //Determines how method will work


    }

    public Credentials(String userNameString, String passwordString, String email){
        mUserName=userNameString;
        mPassword=passwordString;
        mEmailAddress=email;
        mIndex=2;

    }

    public boolean checkCredentials(){
        mUserName = mUserName.trim();
        mPassword = mPassword.trim();
        mEmailAddress=mEmailAddress.trim();

        switch (mIndex) {
            case 1:
                if (mUserName.isEmpty() || mPassword.isEmpty()) return false;
                break;
            case 2:
                if ((mUserName.isEmpty() || mPassword.isEmpty())||mEmailAddress.isEmpty()) return false;
                break;
        }


    return true;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }
}
