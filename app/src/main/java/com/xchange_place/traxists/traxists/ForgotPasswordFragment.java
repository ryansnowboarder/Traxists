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
 * Created by Ryan Fletcher on 8/9/2015.
 *
 * This fragment is intended to be used by the user-client to recovery his or her account
 * if he or she has forgotten his or her password. The user-client must have his or her username
 * and answers to recovery questions available in order to recover his or her password. For brevity
 * purposes, this sample application does not contain more sophisticated account recovery
 * mechanisms.
 */
public class ForgotPasswordFragment extends Fragment {

    private static final String TAG = "ForgotPasswordFragment";

    // GUI objects
    private EditText username_edittext;
    private EditText recovery1_edittext;
    private EditText recovery2_edittext;
    private EditText recovery3_edittext;
    private Button submit_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_recovery_pwd, container, false);

        // link XML elements to Java logic
        username_edittext = (EditText) v.findViewById(R.id.recover_username);
        recovery1_edittext = (EditText) v.findViewById(R.id.recover_answer_1);
        recovery2_edittext = (EditText) v.findViewById(R.id.recover_answer_2);
        recovery3_edittext = (EditText) v.findViewById(R.id.recover_answer_3);
        submit_button = (Button) v.findViewById(R.id.recover_submit);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // see below
                recoverPassword();
            }
        });

        return v;
    }

    // query Parse to recover the password of the user-client
    private void recoverPassword(){
        ParseQuery<ParseObject> query;
        if (MainActivity.getUser().getAccType() == 0)
            query = ParseQuery.getQuery("Creators");
        if (MainActivity.getUser().getAccType() == 1)
            query = ParseQuery.getQuery("Admins");
        else
            query = ParseQuery.getQuery("Users");
        query.whereEqualTo("username", username_edittext.getText().toString());
        query.whereEqualTo("recovery1", recovery1_edittext.getText().toString());
        query.whereEqualTo("recovery2", recovery2_edittext.getText().toString());
        query.whereEqualTo("recovery3", recovery3_edittext.getText().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        String pwd = list.get(0).getString("password");
                        Toast.makeText(getActivity(), "Password: " + pwd, Toast.LENGTH_SHORT);
                    }
                }
            else {
                Toast.makeText(getActivity(), "Incorrect information.", Toast.LENGTH_SHORT);
            }
        }
    });
    }
}
