package com.xchange_place.traxists.traxists;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Ryan Fletcher on 8/4/2015.
 *
 * This fragment is utilized to log in a customer who has already created an account.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    // get GUI objects found in fragment_login
    private EditText login_username_edittext;
    private EditText login_password_edittext;
    private Button login_button;
    private Button forgot_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        // find the GUI objects
        login_username_edittext = (EditText) v.findViewById(R.id.login_username_edittext);
        login_password_edittext = (EditText) v.findViewById(R.id.login_password_edittext);
        login_button = (Button) v.findViewById(R.id.login_button);
        forgot_button = (Button) v.findViewById(R.id.forgot_button);

        // when the login_button is pressed, execute the login function
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // when the forgot_button is pressed, execute the forgot function
        forgot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgot();
            }
        });
        return v;
    }

    // check for a username and password match in the tables of creators, admins, and users
    private void login() {
        ParseQuery<ParseObject> query;
        // query the table associated with the account type of the user-client
        if (MainActivity.getUser().getAccType() == 0) {
            query = ParseQuery.getQuery("Creators");

            // subscribe to PUSH notifications
            ParsePush.subscribeInBackground(MainActivity.getUser().getUsername());
        }
        if (MainActivity.getUser().getAccType() == 1){
            query = ParseQuery.getQuery("Admins");
        }
        else {
            query = ParseQuery.getQuery("Users");
        }
        // enter the username and password into the query
        query.whereEqualTo("username", login_username_edittext.getText().toString());
        query.whereEqualTo("password", login_password_edittext.getText().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    // if not errors have occurred, the user is now logged in through the
                    // MainActivity and user is brought to the next screen
                    MainActivity.getUser().setLoggedIn(true);
                    MainActivity.getUser().setUsername(list.get(0).getString("username"));
                    MainActivity.getUser().setPassword((list.get(0).getString("password")));
                    MainActivity.getUser().setRecovery1((list.get(0).getString("recovery1")));
                    MainActivity.getUser().setRecovery2((list.get(0).getString("recovery2")));
                    MainActivity.getUser().setRecovery3((list.get(0)).getString("recovery3"));
                    if (MainActivity.getUser().getAccType() == 1){

                    }
                    BringCustomerToNextScreen();
                } else {
                    // if there is an error, warn the user of a username/password mismatch
                    MainActivity.getUser().setLoggedIn(false);
                    Toast.makeText(
                            getActivity(),
                            "Username and password mismatch",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    // brings the customer to the next screen, depending on their account type
    private void BringCustomerToNextScreen(){
        // bring creator accounts to AddOrDeleteAdminFragment where they
        // can manage their list of admins and logout
        if (MainActivity.getUser().getAccType() == 0){
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment,
                            new AddOrDeleteAdminFragment())
                    .addToBackStack(null)
                    .commit();
        }
        // bring admin accounts to the Google Maps API where they can view
        // the locations of their related user accounts
        if (MainActivity.getUser().getAccType() == 1){
            MainActivity.onCreateOptionMenu(MainActivity.menu);
        }
        // Bring user accounts to the ApiLoginsFragment where they
        // log into the Facebook API. Permission to use the Google
        // Geolocation API is granted or denied upon the launch of
        // MainActivity.
        else {
            if (MainActivity.getUser().getAccType() == 2){
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment,
                                new ApiLoginsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    // bring the user-client to the ForgotPasswordFragment
    private void forgot(){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment,
                        new ForgotPasswordFragment())
                .addToBackStack(null)
                .commit();
    }

    private AsyncTask<Void, Void, Void> queryUserLocationDataAndAddToMap = new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... params) {
            // infinitely loop until application is killed
            while (true) {
                // setup the Parse query
                ParseQuery<ParseObject> query = ParseQuery.getQuery(
                        MainActivity.getUser().getUsername() + "_users"
                );
                List<ParseObject> users;
                try {
                    users = query.find();
                    // iterate through all the found users
                    for (int i = 0; i < users.size(); i++) {
                        // process each user
                        ParseObject user = users.get(i);

                        // save the user account location data in a Vector using UserLocation objects
                        UserLocation usersLocation = new UserLocation();
                        usersLocation.setUsername(user.getString("username"));
                        usersLocation.setLatitude(user.getString("latitude"));
                        usersLocation.setLongitude(user.getString("longitude"));
                        MainActivity.getUserLocationVector().add(usersLocation);

                        // put the user account location data into LatLng objects for use in
                        // mapFragment
                        LatLng userLocation = new LatLng(
                                Double.valueOf(user.getString("latitude")),
                                Double.valueOf(user.getString("longitude"))
                        );

                        // add the user account location data as a marker onto the mapFragment
                        MainActivity.mapFragment.getMap()
                                .addMarker(
                                        new MarkerOptions()
                                                .position(userLocation)
                                                .title(user.getString("username")));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    wait(1000 * 60 * 5);
                } catch (InterruptedException e) {
                }


                return null;
            }
        }
    };

    private AsyncTask<Void, Void, Void> sendPushesToCreators = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            // infinitely loop until application is exited
            while (true) {
                // setup the Parse query
                ParseQuery<ParseObject> query = ParseQuery.getQuery(MainActivity.getUser().getUsername() + "_creators");
                try {
                    List<ParseObject> associatedCreators = query.find();
                    // iterate through associated creator accounts
                    for (int i = 0; i < associatedCreators.size(); i++) {
                        // iterate through user locations
                        for (int j = 0; j < MainActivity.getUserLocationVector().size(); j++) {
                            // send PUSH notifications with user locations
                            ParsePush push = new ParsePush();
                            push.setChannel(associatedCreators.get(i).getString("username"));
                            push.setMessage("Username: " + MainActivity.getUserLocationVector().get(j).getUsername()
                                    + " at " + MainActivity.getUserLocationVector().get(j).getLatitude() + " lat " +
                                    MainActivity.getUserLocationVector().get(j).getLongitude() + " long.");
                            push.sendInBackground();
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // wait 5 minutes between iterations
                try {
                    wait(1000 * 60 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    };
}

