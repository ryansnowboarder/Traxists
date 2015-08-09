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

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Ryan Fletcher on 8/3/2015.
 *
 * The user-client is expected to enter a username and a password for his or her new account on
 * this fragment. The username is verified for availability via the Parse MBAAS and the password
 * is verified to be more than ten (10) characters in length.
 *
 */
public class EnterUsernameAndPasswordFragment extends Fragment {

    private static final String TAG = "EnterUsrnmAndPwdFrag";

    // GUI objects found in fragment_enter_username_and_password
    private EditText username_edittext;
    private EditText password_edittext;
    private Button login_setup_prev_button;
    private Button login_setup_next_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_enter_username_and_password, container, false);

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
                new VerifyUsername().execute();
            }
        });

        return v;
    }

    // replaces the EnterUsernameAndPasswordFragment with SelectAccountTypeFragment
    private void ReplaceWithSelectAccountTypeFragmentAndAddToBackstack(){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment,
                        new SelectAccountTypeFragment())
                .addToBackStack(null)
                .commit();
    }

    // the aforementioned AsyncTask
    private class VerifyUsername extends AsyncTask<Void, Void, Boolean> {

        // while Parse is being queried, a ProgressDialog is displayed
        private ProgressDialog pd;

        // a flag to store whether or not the desired username is currently available
        public boolean isUsernameAvailable;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
        protected Boolean doInBackground(Void... params) {
            // create a ParseQuery instance to check the availability of the username
            // that the user-client has entered into the EditText in the GUI
            ParseQuery<ParseObject> query;
                if (MainActivity.getUser().getAccType() == 0)
                    query = ParseQuery.getQuery("Creators");
                else if (MainActivity.getUser().getAccType() == 1)
                    query = ParseQuery.getQuery("Admins");
                else
                    query = ParseQuery.getQuery("Users");

            query.whereEqualTo("username", username_edittext.getText().toString());
                try {
                    if (query.find().size() > 0)
                        return false;
                    else
                        return true;
                } catch (ParseException e) {
                    return false;
                }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            // dismiss the ProgressDialog instance
            pd.dismiss();
            if (!result) {
                // if the username is not available, create a Toast warning
                // the user that the username is already taken and to enter
                // a new username
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "Username is already taken.\nEnter a different one.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // if the username is available, check the password for proper length
                if (password_edittext.getText().length() > 10) {
                    // if the password is of proper length, store the username
                    // and password configuration in the User instance of the
                    // MainActivity
                    MainActivity.getUser().setUsername(username_edittext.getText().toString());
                    MainActivity.getUser().setPassword(password_edittext.getText().toString());
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
                    .replace(R.id.main_fragment,
                            new ConfirmPasswordFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
