package com.xchange_place.traxists.traxists;

import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.List;
import java.util.Vector;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener{

    private static final String TAG = "MainActivity";

    // allows storage of menu for later use in app flow
    public static Menu menu;

    // stores the login data of the user-client
    public static User user;

    // store a copy of MainActivity object for use in application
    private static MainActivity mainActivity;

    // store a copy of SupportMapFragment (Google Maps API) for use in application
    public static SupportMapFragment mapFragment;

    // a request code used in invoking user interactions with Google APIs
    private static final int RC_SIGN_IN = 0;

    // client used to interact with Google APIs
    private static GoogleApiClient mGoogleApiClient;

    // a flag indicating that a PendingIntent is in progress  to Googlw APIs and prevents
    // the starting of further intents
    private boolean mIntentInProgress;

    private static Vector<UserLocation> userLocationVector;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // store a pointer to this activity for retrieving its
        // objects, such as the instance of the User object
        mainActivity = this;

        setContentView(R.layout.activity_main);

        // initialize an instance of the User object in order
        // to store login data about the current user-client
        user = new User();

        // initialize the Facebook SDK
        FacebookSdk.sdkInitialize(this);

        // initialize a SupportMapFragment for use
        // after an admin account logs in
        mapFragment = new SupportMapFragment();

        // asynchronously retrieve Google Map from Google API
        mapFragment.getMapAsync(this);

        // setup GoogleApiClient that communicates location data from Google
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // enable Parse local datastore
        Parse.enableLocalDatastore(this);
        // connect to Parse MBAAS service
        Parse.initialize(this, "ohQwVLtLq2NLrJXZ2UlrGt11mL394tclcfC69q8t", "gPQYbQhd3EkFtKOeSg1B9Now2WJJIj4cpNYCVJrJ");
        // save the current installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment,
                        new SelectAccountTypeFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // only display the Menu if an admin is logged into the service
        // this is not equal to super.onCreateOptionsMenu(menu)
        return onCreateOptionMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle action bar item clicks here
        int id = item.getItemId();
        onOptionsItemSelected(id);

        return super.onOptionsItemSelected(item);
    }

    public static boolean onCreateOptionMenu(Menu menuInFunction){
        // store menuInFunction for later calls to OnCreateOptionsMenu()
        if (menu == null){
            menu = menuInFunction;
        }

        // only show the menu if an Admin account is logged in
        if (user.isLoggedIn()){
            if (user.getAccType() == 1) {
                mainActivity.getMenuInflater().inflate(R.menu.menu_main, menu);
                return true;
            }
        }
        // else close the menu
        else {
            mainActivity.closeOptionsMenu();
            return false;
        }
        return true;
    }

    //
    public static void onOptionsItemSelected(int id){
        // show the Google Maps API and hide the other fragments
        if (id == R.id.action_maps) {
            // mapFragmentFrame.setVisibility(View.VISIBLE);
            // fragmentFrame.setVisibility(View.GONE);
        }

        // hide the Google Maps API and show the other fragments
        if (id == R.id.action_settings) {
            // if (mapFragmentFrame.getVisibility() == View.VISIBLE)
            //     mapFragmentFrame.setVisibility(View.GONE);
            // if (fragmentFrame.getVisibility() == View.GONE)
            //    fragmentFrame.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // enter data here
        // LatLng sydney = new LatLng(-34, 151);
        // googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    public void onConnected(Bundle bundle) {
        // when the map begins any connection to the internet,
        // begin querying the location data of the users
        // associated with an admin account if and
        // only if an admin account is currently logged in
        if (user != null)
        if (user.isLoggedIn())
            if (user.getAccType() == 1) {
                queryUserLocationDataAndAddToMap.execute();
                sendPushesToCreators.execute();
            }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // this tracks 'app deactivate' app event on the Facebook API
        AppEventsLogger.deactivateApp(this);
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        // Facebook Login callback manager
        ApiLoginsFragment.getCallbackManager().onActivityResult(requestCode, responseCode, intent);

        // Google APIs Login
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // save the login information of the user
        outState.putInt("account_type", user.getAccType());
        outState.putString("username", user.getUsername());
        outState.putString("password", user.getPassword());
        outState.putString("recovery1", user.getRecovery1());
        outState.putString("recovery2", user.getRecovery2());
        outState.putString("recovery3", user.getRecovery3());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState){

        // restore the login information of the user
        user.setAccType(inState.getInt("account_type"));
        user.setUsername(inState.getString("username"));
        user.setPassword(inState.getString("password"));
        user.setRecovery1(inState.getString("recovery1"));
        user.setRecovery2(inState.getString("recovery2"));
        user.setRecovery3(inState.getString("recovery3"));


        super.onRestoreInstanceState(inState);
    }

    // once the client has inputted all account information,
    // save the account information in Parse
    public static void postUserToParse(){
        if (user.getAccType() == 0){
            // post creator account to Parse
            ParseObject creator = new ParseObject("Creators");
            creator.put("username", user.getUsername());
            creator.put("password", user.getPassword());
            creator.put("recovery1", user.getRecovery1());
            creator.put("recovery2", user.getRecovery2());
            creator.put("recovery3", user.getRecovery3());
            creator.saveInBackground();

            // subscribe to PUSH notifications
            ParsePush.subscribeInBackground(user.getUsername());
        }
        if (user.getAccType() == 1){
            // post admin account to Parse
            ParseObject admin = new ParseObject("Admins");
            admin.put("username", user.getUsername());
            admin.put("password", user.getPassword());
            admin.put("recovery1", user.getRecovery1());
            admin.put("recovery2", user.getRecovery2());
            admin.put("recovery3", user.getRecovery3());
            admin.saveInBackground();
        }
        if (user.getAccType() == 2){
            // post user account to Parse
            ParseObject userClient = new ParseObject("Users");
            userClient.put("username", user.getUsername());
            userClient.put("password", user.getPassword());
            userClient.put("recovery1", user.getRecovery1());
            userClient.put("recovery2", user.getRecovery2());
            userClient.put("recovery3", user.getRecovery3());
            userClient.saveInBackground();
        }
    }

    private AsyncTask<Void, Void, Void> queryUserLocationDataAndAddToMap = new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... params) {
            // infinitely loop until application is killed
            while (true) {
                // setup the Parse query
                ParseQuery<ParseObject> query = ParseQuery.getQuery(
                        getUser().getUsername() + "_users"
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
                        userLocationVector.add(usersLocation);

                        // put the user account location data into LatLng objects for use in
                        // mapFragment
                        LatLng userLocation = new LatLng(
                                Double.valueOf(user.getString("latitude")),
                                Double.valueOf(user.getString("longitude"))
                        );

                        // add the user account location data as a marker onto the mapFragment
                        mapFragment.getMap()
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
                ParseQuery<ParseObject> query = ParseQuery.getQuery(getUser().getUsername() + "_creators");
                try {
                    List<ParseObject> associatedCreators = query.find();
                    // iterate through associated creator accounts
                    for (int i = 0; i < associatedCreators.size(); i++) {
                        // iterate through user locations
                        for (int j = 0; j < userLocationVector.size(); j++) {
                            // send PUSH notifications with user locations
                            ParsePush push = new ParsePush();
                            push.setChannel(associatedCreators.get(i).getString("username"));
                            push.setMessage("Username: " + userLocationVector.get(j).getUsername()
                                    + " at " + userLocationVector.get(j).getLatitude() + " lat " +
                                    userLocationVector.get(j).getLongitude() + " long.");
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

    /*
    *
    * GETTERS AND SETTERS
    *
     */

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        MainActivity.user = user;
    }

    public static GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static Vector<UserLocation> getUserLocationVector() {
        return userLocationVector;
    }
}
