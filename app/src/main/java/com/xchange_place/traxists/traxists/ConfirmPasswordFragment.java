package com.xchange_place.traxists.traxists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Ryan Fletcher on 8/3/2015.
 *
 * This fragment is utilized for confirming the entered password that the user is submitting for
 * usage in his or her new account.
 *
 */
public class ConfirmPasswordFragment extends Fragment {

    // GUI objects found in fragment_confirm_password
    private EditText confirm_password_edittext;
    private Button confirm_password_prev_button;
    private Button confirm_password_next_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_confirm_password, container, false);

        // connect the EditText XML components to the variables in this Java logic
        confirm_password_edittext = (EditText) v.findViewById(R.id.confirm_password_edittext);

        // connect the Button XML components to the variables in this Java logic
        confirm_password_prev_button = (Button) v.findViewById(R.id.confirm_password_prev);
        confirm_password_next_button = (Button) v.findViewById(R.id.confirm_password_next);

        // set an onOnClickListener to confirm_password_prev_button that brings the user-client
        // back to EnterUsernameAndPassword fragment
        confirm_password_prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceWithEnterUsernameAndPasswordFragmentAndAddToBackstack();
            }
        });

        // Set an onOnClickListener to confirm_password_next_button that brings the user-client
        // forward to RecoveryFragment fragment and saves the password to the User object in
        // the MainActivity. Also checks for password mismatches.
        confirm_password_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPasswordMatch(confirm_password_edittext.getText().toString())) {
                    replaceWithRecoveryFragmentAndAddToBackstack();
                } else {
                    DisplayPasswordMismatchToast();
                }
            }
        });

        return v;
    }

    // sends the user-client back to the UsernameAndPasswordFragment from this fragment
    private void replaceWithEnterUsernameAndPasswordFragmentAndAddToBackstack() {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment,
                        new EnterUsernameAndPasswordFragment())
                .addToBackStack(null)
                .commit();
    }

    // check that the two password entries are the the same array of characters
    private boolean checkPasswordMatch(String reEnteredPassword){
        if (MainActivity.getUser().getPassword() == reEnteredPassword){
            return true;
        }
        else {
            return false;
        }
    }

    // sends the user to the RecoveryFragment from this fragment
    private void replaceWithRecoveryFragmentAndAddToBackstack(){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment,
                        new RecoveryFragment())
                .addToBackStack(null)
                .commit();

    }

    // if the two passwords mismatch, a toast is displayed saying "Password Mismatch"
    private void DisplayPasswordMismatchToast(){
        Toast.makeText(
                getActivity().getApplicationContext(),
                "Password Mismatch",
                Toast.LENGTH_SHORT).show();
    }

}
