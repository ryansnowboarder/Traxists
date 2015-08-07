package com.xchange_place.traxists.traxists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Ryan Fletcher on 8/4/2015.
 *
 * This fragment is utilized to log in a customer who has already created an account.
 */
public class LoginFragment extends Fragment {

    // get GUI objects found in fragment_login
    private EditText login_username_edittext;
    private EditText login_password_edittext;
    private Button login_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        // find the GUI objects
        login_username_edittext = (EditText) v.findViewById(R.id.login_username_edittext);
        login_password_edittext = (EditText) v.findViewById(R.id.login_password_edittext);
        login_button = (Button) v.findViewById(R.id.login_button);

        // when the login_button is pressed, execute the login function
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        return v;
    }

    // check for a username and password match in the tables of creators, admins, and users
    private void login() {
        ParseQuery<ParseObject> query;
        // query the table associated with the account type of the user-client
        if (MainActivity.getUser().getAccType() == 0) {
            query = ParseQuery.getQuery("creator");
        }
        if (MainActivity.getUser().getAccType() == 1){
            query = ParseQuery.getQuery("admin");
        }
        else {
            query = ParseQuery.getQuery("user");
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
                    MainActivity.postUserToParse();
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
}

