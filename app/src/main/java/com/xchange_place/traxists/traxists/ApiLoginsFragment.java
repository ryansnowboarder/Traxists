package com.xchange_place.traxists.traxists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Ryan Fletcher on 8/4/2015.
 *
 * This fragment is only seen by user accounts. The purpose of this fragment is for the user-client
 * to log into the APIs that then send location data to the admin accounts and creator accounts.
 */
public class ApiLoginsFragment extends Fragment {

    private static final String TAG = "ApiLoginsFragment";

    private static CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializes the static CallbackManager instance
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_api_logins, container, false);

        // find the Facebook LoginButton object in the XML and connect it to the Java logic
        loginButton = (LoginButton) v.findViewById(R.id.facebook_login_button);

        // set the initial permissions a user is prompted for when clicking the loginButton object
        loginButton.setReadPermissions("current_location");

        // the loginButton object is required to be connected to the fragment if it is not
        // in an activity
        loginButton.setFragment(this);
        // Other app specific specialization

        // register a callback to the loginButton that...
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code (To-do)
            }

            @Override
            public void onCancel() {
                // App code (To-do)
            }

            @Override
            public void onError(FacebookException e) {
                // App code (To-do)
            }
        });

        return v;
    }

    // a getter for the callbackManager object
    public static CallbackManager getCallbackManager() {
        return callbackManager;
    }
}