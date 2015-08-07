package com.xchange_place.traxists.traxists;

import android.app.ProgressDialog;
import android.os.AsyncTask;
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
 * Created by Ryan Fletcher on 8/3/2015.
 *
 * The user-client is expected to enter a username and a password for his or her new account on
 * this fragment. The username is verified for availability via the Parse MBAAS and the password
 * is verified to be more than ten (10) characters in length.
 *
 */
public class EnterUsernameAndPasswordFragment extends Fragment {

    // GUI objects found in fragment_enter_username_and_password
    private EditText username_edittext;
    private EditText password_edittext;
    private Button login_setup_prev_button;
    private Button login_setup_next_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_acc_type, container, false);

        // connect the EditText XML components to the variables in this Java logic
        username_edittext = (EditText) v.findViewById(R.id.username_edittext);
        password_edittext = (EditText) v.findViewById(R.id.password_edittext);

        // connect the Button XML components to the variables in this Java logic
        login_setup_prev_button = (Button) v.findViewById(R.id.login_setup_prev_button);
        login_setup_next_button = (Button) v.findViewById(R.id.login_setup_next_button);

        // when login_setup_prev_button is clicked, the client-user is brought back
        // to the SelectAccountTypeFragment
        login_setup_prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceWithSelectAccountTypeFragmentAndAddToBackstack();
            }
        });

        // When the login_setup_next_button is pressed, an AsyncTask is created to communicate
        // with the Parse MBAAS to verify the availability of the username. If available, the
        // username is stored in MainActivity and the onPostExecute method of the AsyncTask
        // brings the user to the ConfirmPassword Fragment. If not available, the
        // taken username prompts a Toast to be displayed warning the user-client that
        // the username is taken. The AsyncTask  onPostExecute method also confirms
        // that the password entered by the user is more than ten (10) characters in length
        // for adequate security purposes.
        login_setup_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VerifyUsername().doInBackground();
            }
        });

        return v;
    }

    // replaces the EnterUsernameAndPasswordFragment with SelectAccountTypeFragment
    private void ReplaceWithSelectAccountTypeFragmentAndAddToBackstack(){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_enter_username_and_password,
                        new SelectAccountTypeFragment())
                .addToBackStack(null)
                .commit();
    }

    // the aforementioned AsyncTask
    private class VerifyUsername extends AsyncTask<Void, String, Void> {

        // while Parse is being queried, a ProgressDialog is displayed
        private ProgressDialog pd;

        // the username to be verified
        private String username;

        // a flag to store whether or not the latest username entered is available
        private boolean isUsernameAvailable;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = username_edittext.getText().toString();
            // setup the ProgressDialog
            pd = new ProgressDialog(getActivity());
            pd.setTitle("Please Wait...");
            pd.setMessage("Checking if username is in use.");
            // make the ProgressDialog non-cancelable so the user cannot
            // interact with the GUI while the query is being executing,
            // which will potentially prevent bugs
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // create a ParseQuery instance to check the availability of the username
                // that the user-client has entered into the EditText in the GUI
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Creators");
                query.whereEqualTo("username", username);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            // no errors have occured, meaning that the username has been found
                            // in the Parse MBAAS table and is not available for usage
                            isUsernameAvailable = false;
                        } else {
                            // if an error occurs, the username has not been found in the
                            // Parse MBAAS and is currently available for usage
                            isUsernameAvailable = true;
                        }
                    }
                });
            } catch (Exception e) {
                // if there is an exception, treat the query as if an error
                // had occurred
                isUsernameAvailable = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // dismiss the ProgressDialog instance
            pd.dismiss();
            if (!isUsernameAvailable) {
                // if the username is not available, create a Toast warning
                // the user that the username is already taken and to enter
                // a new username
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "Username is already taken.\nEnter a different one.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // if the username is available, check the password for proper length
                String password = password_edittext.getText().toString();
                if (password.length() > 10) {
                    // if the password is of proper length, store the username
                    // and password configuration in the User instance of the
                    // MainActivity
                    MainActivity.getUser().setUsername(username);
                    MainActivity.getUser().setPassword(password);
                    ReplaceWithConfirmPasswordFragmentAndAddToBackstack();
                } else {
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Password must be more than\n10 characters long.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        // replace EnterUsernameAndPasswordFragment with ConfirmPasswordFragment
        private void ReplaceWithConfirmPasswordFragmentAndAddToBackstack() {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_enter_username_and_password,
                            new ConfirmPasswordFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
