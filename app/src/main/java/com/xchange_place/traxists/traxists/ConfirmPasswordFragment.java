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
 */
public class ConfirmPasswordFragment extends Fragment {

    // GUI objects found in fragment_confirm_password
    private EditText confirm_password_edittext;
    private Button confirm_password_prev_button;
    private Button confirm_password_next_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_confirm_password, container, false);

        confirm_password_edittext = (EditText) v.findViewById(R.id.confirm_password_edittext);

        confirm_password_prev_button = (Button) v.findViewById(R.id.confirm_password_prev);
        confirm_password_next_button = (Button) v.findViewById(R.id.confirm_password_next);

        confirm_password_prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceWithEnterUsernameAndPasswordFragmentAndAddToBackstack();
            }
        });

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

    private void replaceWithEnterUsernameAndPasswordFragmentAndAddToBackstack() {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_confirm_password,
                        new EnterUsernameAndPasswordFragment())
                .addToBackStack(null)
                .commit();
    }

    private boolean checkPasswordMatch(String reEnteredPassword){
        if (MainActivity.getUser().getPassword() == reEnteredPassword){
            return true;
        }
        else {
            return false;
        }
    }

    private void replaceWithRecoveryFragmentAndAddToBackstack(){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_confirm_password,
                        new RecoveryFragment())
                .addToBackStack(null)
                .commit();

    }

    private void DisplayPasswordMismatchToast(){
        Toast.makeText(
                getActivity().getApplicationContext(),
                "Password Mismatch",
                Toast.LENGTH_SHORT).show();
    }

}
