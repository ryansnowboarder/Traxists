package com.xchange_place.traxists.traxists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

/**
 * Created by Ryan Fletcher on 8/3/2015.
 *
 * This is the first screen (disregarding the splash screen) that any user-client sees when
 * launching the application. The user selects an account type and can either login or create
 * a new account
 */
public class SelectAccountTypeFragment extends Fragment {

    // get GUI objects found in fragment_select_acc_type
    private RadioButton creator_account_selected_radio_button;
    private RadioButton admin_account_selected_radio_button;
    private RadioButton user_account_selected_radio_button;
    private Button user_login_button;
    private Button user_create_account_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_acc_type, container, false);

        // find the GUI objects
        creator_account_selected_radio_button = (RadioButton) v.findViewById(R.id.selected_creator_button);
        admin_account_selected_radio_button = (RadioButton) v.findViewById(R.id.selected_admin_button);
        user_account_selected_radio_button = (RadioButton) v.findViewById(R.id.selected_user_button);
        user_login_button = (Button) v.findViewById(R.id.go_to_login_button);
        user_create_account_button = (Button) v.findViewById(R.id.go_to_create_account_button);

        // set the default selection to login or create a creator account
        creator_account_selected_radio_button.setChecked(true);
        MainActivity.getUser().setAccType((short) 0);

        // if creator_account_selected_radio_button is checked, set account type to creator
        creator_account_selected_radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.getUser().getAccType() != (short) 0)
                    MainActivity.getUser().setAccType((short) 0);
            }
        });

        // if admin_account_selected_radio_button is checked, set account type to admin
        admin_account_selected_radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.getUser().getAccType() != (short) 1)
                    MainActivity.getUser().setAccType((short) 1);
            }
        });

        // if user_account_selected_radio_button is checked, set account type to user
        user_account_selected_radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.getUser().getAccType() != (short) 2)
                    MainActivity.getUser().setAccType((short) 2);
            }
        });

        // if user clicks the login button, bring user to the login screen
        user_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceWithLoginFragmentAndAddToBackstack();
            }
        });

        // if user clicks the create account button, bring user to the create account flow
        user_create_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceWithEnterUsernameAndPasswordFragmentAndAddToBackstack();
            }
        });
        return v;
    }

    private void ReplaceWithLoginFragmentAndAddToBackstack(){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_select_acc_type,
                        new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

    private void ReplaceWithEnterUsernameAndPasswordFragmentAndAddToBackstack(){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_select_acc_type,
                        new EnterUsernameAndPasswordFragment())
                .addToBackStack(null)
                .commit();
    }
}
