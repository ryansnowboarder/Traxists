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

        username_edittext = (EditText) v.findViewById(R.id.username_edittext);
        password_edittext = (EditText) v.findViewById(R.id.password_edittext);

        login_setup_prev_button = (Button) v.findViewById(R.id.login_setup_prev_button);
        login_setup_next_button = (Button) v.findViewById(R.id.login_setup_next_button);

        login_setup_prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceWithSelectAccountTypeFragmentAndAddToBackstack();
            }
        });

        login_setup_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VerifyUsername().doInBackground();
            }
        });

        return v;
    }

    private void ReplaceWithSelectAccountTypeFragmentAndAddToBackstack(){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_enter_username_and_password,
                        new SelectAccountTypeFragment())
                .addToBackStack(null)
                .commit();
    }

    private class VerifyUsername extends AsyncTask<Void, String, Void> {

        private ProgressDialog pd;

        // the username to be verified
        private String username;

        // a flag to store whether or not the latest username entered is available
        private boolean isUsernameAvailable;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = username_edittext.getText().toString();
            pd = new ProgressDialog(getActivity());
            pd.setTitle("Please Wait...");
            pd.setMessage("Checking if username is in use.");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Creators");
                query.whereEqualTo("username", username);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            isUsernameAvailable = false;
                        } else {
                            isUsernameAvailable = true;
                        }
                    }
                });
            } catch (Exception e) {
                isUsernameAvailable = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pd.dismiss();
            if (!isUsernameAvailable) {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "Username is already taken.\nEnter a different one.",
                        Toast.LENGTH_SHORT).show();
            } else {
                String password = password_edittext.getText().toString();
                if (password.length() > 10) {
                    MainActivity.getUser().setUsername(username);
                    MainActivity.getUser().setPassword(password);
                    ReplaceWithConfirmPasswordFragmentAndAddToBackstack();
                } else {
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Password must be at least\n10 characters long.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

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
