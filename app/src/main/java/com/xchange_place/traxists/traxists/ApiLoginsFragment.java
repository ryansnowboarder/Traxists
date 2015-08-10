package com.xchange_place.traxists.traxists;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

/**
 * Created by Ryan Fletcher on 8/4/2015.
 *
 * This fragment is only seen by user accounts. The purpose of this fragment is for the user-client
 * to log into the APIs that then send location data to Parse. Admin accounts and Creator accounts
 * receive updates of the location of their associated user accounts.
 */
public class ApiLoginsFragment extends Fragment {

    private static final String TAG = "ApiLoginsFragment";

    private static CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    private Button logoutButton;
    private Location googleLastLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializes the static CallbackManager instance
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_api_logins, container, false);

        // find the buttons in the XML and connect it to the Java logic
        facebookLoginButton = (LoginButton) v.findViewById(R.id.facebook_login_button);
        logoutButton = (Button) v.findViewById(R.id.user_traxists_logout);

        // the loginButton object is required to be connected to the fragment if it is not
        // in an activity
        facebookLoginButton.setFragment(this);

        // register a callback to the loginButton that...
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // begin querying user data and adding it to Parse MBAAS
                QueryLocationData.execute();
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

        // when logoutButton is pressed, execute the logout function
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return v;
    }

    // queries both the Google Location API and the Facebook API to find
    // the last known location of the user account, then saves this
    // location in Parse
    public AsyncTask QueryLocationData = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            // get the GoogleAPIClient initialized in MainActivity
            GoogleApiClient googleApiClient = MainActivity.getmGoogleApiClient();

            // store the last known lattitudes from Google API and
            // Facebook API in these variables, which are initialized to zero (0)
            // these are used to check for location changes since the previous
            // execution of the loop
            String lastGoogleLat = "0";
            String lastGoogleLong = "0";
            String lastFacebookLat = "0";
            String lastFacebookLong = "0";

            // store the current known lattitudes and longitudes from the Google API and
            // Facebook API in these variables, which are initialized to zero (0)
            String googleLat = "0";
            String googleLong = "0";
            String facebookLat = "0";
            String facebookLong = "0";

            // a flag to store which location data has been updated last
            // false -> Google source
            // true -> Facebook source
            boolean lastSourceToUpdate = false;

            // The entire Facebook FQL query as an HTTP request
            String FQLquery = "https://graph.facebook.com/fql?q=SELECT+current_location+FROM+user+WHERE+uid=me()";

            // repeatedly loop location query every five (5) minutes
            while (true) {

                // get the last known location of the user from Google
                Location lastGoogleLocation = LocationServices.
                        FusedLocationApi.
                        getLastLocation(googleApiClient);

                // get the last known location of the user from Facebook
                HttpResponse response = null;
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(FQLquery));
                    response = client.execute(request);
                } catch (Exception e) {
                }
                if (response != null) {
                    JSONObject FBresultJSON = null;
                    try {
                        FBresultJSON = new JSONObject(EntityUtils.toString(response.getEntity()));
                    } catch (Exception e) {
                    }
                    try {
                        String facebookLattitude = FBresultJSON.
                                getJSONArray("data").
                                getJSONObject(0)
                                .getString("lattitude");
                        facebookLat = facebookLattitude;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        String facebookLongitude = FBresultJSON
                                .getJSONArray("data")
                                .getJSONObject(0)
                                .getString("longitude");
                        facebookLong = facebookLongitude;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // update the flag that indicates the last source of location data
                if (!(googleLat.equals(lastGoogleLat) && googleLong.equals(lastGoogleLong)))
                    lastSourceToUpdate = false;
                if (!(facebookLat.equals(lastFacebookLat) && facebookLong.equals(lastFacebookLong)))
                    lastSourceToUpdate = true;


                // update the User account location data stored in Parse MBAAS
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
                query.whereEqualTo("username", MainActivity.getUser().getUsername());
                try {
                    ParseObject user = query.find().get(0);
                    if (!lastSourceToUpdate){
                        user.put("lattitude", googleLat);
                        user.put("longitude", googleLong);
                    }
                    else {
                        user.put("lattitude", facebookLat);
                        user.put("longitude", facebookLong);
                    }
                    user.save();
                } catch (ParseException e) {
                }

                // after data is processed and before the next execution
                // of the loop, set the currently found data to the
                // last found data
                lastGoogleLat = googleLat;
                lastGoogleLong = googleLong;
                lastFacebookLat = facebookLat;
                lastFacebookLong = facebookLong;
            }
        }
    };

    // logs the user account out of the Traxists application
    private void logout(){
        // clear stored user data
        User user = MainActivity.getUser();
        user.setAccType(-1);
        user.setRecovery3(null);
        user.setRecovery2(null);
        user.setRecovery1(null);
        user.setUsername(null);
        user.setPassword(null);
        user.setLoggedIn(false);

        // move to SelectAccountTypeFragment
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment,
                        new SelectAccountTypeFragment())
                .addToBackStack(null)
                .commit();
    }

    public static CallbackManager getCallbackManager() {
        return callbackManager;
    }
}