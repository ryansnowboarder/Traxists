package com.xchange_place.traxists.traxists;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.braintreepayments.api.Braintree;
import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseObject;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener{

    // allows storage of menu for later use in app flow
    private static Menu menu;

    public static User user;

    // store a copy of MainActivity object for use in application
    private static MainActivity mainActivity;

    // store a copy of MainActivityFragment for use in application
    private static MainActivityFragment mainActivityFragment;

    // store a copy of SupportMapFragment (Google Maps API) for use in application
    private static SupportMapFragment mapFragment;

    // store MainActivityFragment in a FrameLayout so it can be visible or gone
    private static FrameLayout fragmentFrame;

    public static Menu getMenu() {
        return menu;
    }

    public static void setMenu(Menu menu) {
        MainActivity.menu = menu;
    }

    public static FrameLayout getFragmentFrame() {
        return fragmentFrame;
    }

    public static void setFragmentFrame(FrameLayout fragmentFrame) {
        MainActivity.fragmentFrame = fragmentFrame;
    }

    public static FrameLayout getMapFragmentFrame() {
        return mapFragmentFrame;
    }

    public static void setMapFragmentFrame(FrameLayout mapFragmentFrame) {
        MainActivity.mapFragmentFrame = mapFragmentFrame;
    }

    // store SupportMapFragment in a FrameLayout so it can be visible or gone
    private static FrameLayout mapFragmentFrame;

    // a CallbackManager for the Facebook API login
    CallbackManager callbackManager;

    // a request code used in invoking user interactions with Google APIs
    private static final int RC_SIGN_IN = 0;

    // client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // a flag indicating that a PendingIntent is in progress  to Googlw APIs and prevents
    // the starting of further intents
    private boolean mIntentInProgress;

    // Braintree is the payment API used in this application
    private Braintree brainTree;

    // a request code used in invoking payments with Braintree payment services
    private static final int BRAINTREE_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = new User();

        mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        fragmentFrame = (FrameLayout) findViewById(R.id.fragment_frame);
        mapFragmentFrame = (FrameLayout) findViewById(R.id.map_fragment_frame);

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

        // initialize Braintree payment service
        brainTree = Braintree.restoreSavedInstanceState(this, savedInstanceState);
        if (brainTree != null) {
            // brainTree is ready to use
        } else {
            Braintree.setup(this,
                    "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJhYWMxN2QxOGRhMDM4MTQ5ODdlZmUyNmZkYmViODY5NWMwMWI5ZjFlOTRkZDcxMjAwNDdlZjc1ZTg5MjMwZjBmfGNyZWF0ZWRfYXQ9MjAxNS0wOC0wNFQwNjo0NDo1Ny4zMzkwODA4NjArMDAwMFx1MDAyNm1lcmNoYW50X2lkPWRjcHNweTJicndkanIzcW5cdTAwMjZwdWJsaWNfa2V5PTl3d3J6cWszdnIzdDRuYzgiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvZGNwc3B5MmJyd2RqcjNxbi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzL2RjcHNweTJicndkanIzcW4vY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIn0sInRocmVlRFNlY3VyZUVuYWJsZWQiOnRydWUsInRocmVlRFNlY3VyZSI6eyJsb29rdXBVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvZGNwc3B5MmJyd2RqcjNxbi90aHJlZV9kX3NlY3VyZS9sb29rdXAifSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwibWVyY2hhbnRBY2NvdW50SWQiOiJzdGNoMm5mZGZ3c3p5dHc1IiwiY3VycmVuY3lJc29Db2RlIjoiVVNEIn0sImNvaW5iYXNlRW5hYmxlZCI6dHJ1ZSwiY29pbmJhc2UiOnsiY2xpZW50SWQiOiIxMWQyNzIyOWJhNThiNTZkN2UzYzAxYTA1MjdmNGQ1YjQ0NmQ0ZjY4NDgxN2NiNjIzZDI1NWI1NzNhZGRjNTliIiwibWVyY2hhbnRBY2NvdW50IjoiY29pbmJhc2UtZGV2ZWxvcG1lbnQtbWVyY2hhbnRAZ2V0YnJhaW50cmVlLmNvbSIsInNjb3BlcyI6ImF1dGhvcml6YXRpb25zOmJyYWludHJlZSB1c2VyIiwicmVkaXJlY3RVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbS9jb2luYmFzZS9vYXV0aC9yZWRpcmVjdC1sYW5kaW5nLmh0bWwiLCJlbnZpcm9ubWVudCI6Im1vY2sifSwibWVyY2hhbnRJZCI6ImRjcHNweTJicndkanIzcW4iLCJ2ZW5tbyI6Im9mZmxpbmUiLCJhcHBsZVBheSI6eyJzdGF0dXMiOiJtb2NrIiwiY291bnRyeUNvZGUiOiJVUyIsImN1cnJlbmN5Q29kZSI6IlVTRCIsIm1lcmNoYW50SWRlbnRpZmllciI6Im1lcmNoYW50LmNvbS5icmFpbnRyZWVwYXltZW50cy5zYW5kYm94LkJyYWludHJlZS1EZW1vIiwic3VwcG9ydGVkTmV0d29ya3MiOlsidmlzYSIsIm1hc3RlcmNhcmQiLCJhbWV4Il19fQ==",
                    new Braintree.BraintreeSetupFinishedListener() {
                        @Override
                        public void onBraintreeSetupFinished(boolean setupSuccessful, Braintree braintree, String errorMessage, Exception exception) {
                            if (setupSuccessful) {
                                // braintree is now setup and available for use
                            } else {
                                // Braintree could not be initialized, check errors and try again
                                // This is usually a result of a network connectivity error
                            }
                        }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show the menu if an Admin account is logged in.
        return onCreateOptionMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    public static void onOptionsItemSelected(int id){
        if (id == R.id.action_maps) {
            user.setLoginState((short) 6);
            mapFragmentFrame.setVisibility(View.VISIBLE);
            fragmentFrame.setVisibility(View.GONE);
        }
        if (id == R.id.action_comms) {
            user.setLoginState((short) 7);
            if (mapFragmentFrame.getVisibility() == View.VISIBLE)
                mapFragmentFrame.setVisibility(View.GONE);
            if (fragmentFrame.getVisibility() == View.GONE)
                fragmentFrame.setVisibility(View.VISIBLE);
        }
        if (id == R.id.action_settings) {
            user.setLoginState((short) 8);
            if (mapFragmentFrame.getVisibility() == View.VISIBLE)
                mapFragmentFrame.setVisibility(View.GONE);
            if (fragmentFrame.getVisibility() == View.GONE)
                fragmentFrame.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // enter data here
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    public void onConnected(Bundle bundle) {
        // do application logic here
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
        brainTree.onPause(this);

        // Tracks 'app deactivate' App Event on Facebook API
        AppEventsLogger.deactivateApp(this);
    }

    protected void onResume() {
        super.onResume();
        brainTree.onResume(this);
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

        // Braintree API Login
        if (requestCode == BRAINTREE_REQUEST_CODE) {
            if (responseCode == BraintreePaymentActivity.RESULT_OK) {
                String paymentMethodNonce = intent.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                postNonceToServer(paymentMethodNonce);
                postUserToParse();
                replaceRecoveryFragmentWithNextFragment();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (brainTree != null) {
            brainTree.onSaveInstanceState(outState);
        }
    }

    private void postNonceToServer(String nonce) {
    }

    // once the client has inputed all account information and purchased the service
    // through the Braintree API, save the account information in Parse
    private void postUserToParse(){
        if (user.getAccType() == 0){
            // post creator account to Parse
            ParseObject creator = new ParseObject("creator");
            creator.put("username", user.getUsername());
            creator.put("password", user.getPassword());
            creator.put("recovery1", user.getRecovery1());
            creator.put("recovery2", user.getRecovery2());
            creator.put("recovery3", user.getRecovery3());
            creator.saveInBackground();
        }
        if (user.getAccType() == 1){
            // post admin account to Parse
            ParseObject admin = new ParseObject("admin");
            admin.put("username", user.getUsername());
            admin.put("password", user.getPassword());
            admin.put("recovery1", user.getRecovery1());
            admin.put("recovery2", user.getRecovery2());
            admin.put("recovery3", user.getRecovery3());
            admin.saveInBackground();
        }
        if (user.getAccType() == 2){
            // post user account to Parse
            ParseObject userClient = new ParseObject("user");
            userClient.put("username", user.getUsername());
            userClient.put("password", user.getPassword());
            userClient.put("recovery1", user.getRecovery1());
            userClient.put("recovery2", user.getRecovery2());
            userClient.put("recovery3", user.getRecovery3());
            userClient.saveInBackground();
        }
    }

    private void replaceRecoveryFragmentWithNextFragment(){
        if (user.getAccType() == 0){
            // move to AddOrDeleteAdminsFragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_recovery_questions,
                            new AddOrDeleteAdminFragment())
                    .addToBackStack(null)
                    .commit();
        }
        if (user.getAccType() == 1){
            // create the menu
            onCreateOptionMenu(menu);
            // make Google Maps fragment visible
            mapFragmentFrame.setVisibility(View.VISIBLE);
            // make other fragment invisible
            fragmentFrame.setVisibility(View.INVISIBLE);
        }
        if (user.getAccType() == 2){
            // move to API logouts screen
        }

    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        MainActivity.user = user;
    }
}
