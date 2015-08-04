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

    // this function checks for a username and password match in the tables of creators,
    // admins, and users
    private void login() {
        ParseQuery<ParseObject> query;
        if (MainActivity.getUser().getAccType() == 0) {
            query = ParseQuery.getQuery("creator");
        }
        if (MainActivity.getUser().getAccType() == 1){
            query = ParseQuery.getQuery("admin");
        }
        else {
            query = ParseQuery.getQuery("user");
        }
        query.whereEqualTo("username", login_username_edittext.getText().toString());
        query.whereEqualTo("password", login_password_edittext.getText().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    MainActivity.getUser().setLoggedIn(true);
                } else {
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
                    .replace(R.id.fragment_login,
                            new AddOrDeleteAdminFragment())
                    .addToBackStack(null)
                    .commit();
        }
        if (MainActivity.getUser().getAccType() == 1){
            MainActivity.getMapFragmentFrame().setVisibility(View.VISIBLE);
            MainActivity.getFragmentFrame().setVisibility(View.GONE);
        }
        else {
            if (MainActivity.getUser().getAccType() == 2){
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_login,
                                new ApiLoginsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
}

