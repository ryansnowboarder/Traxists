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
 * This fragment is only visible to Admin accounts. In this fragment, the Admin user-client
 * is able to add or delete User accounts from the list of User accounts that they are subscribed
 * to receive location updates from. Admins are also able to logout. This fragment is nearly
 * identical to AddOrDeleteAdminFragment, but is coded separately because further things will
 * be added only to this fragment and not AddOrDeleteAdminFragment.
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    // GUI objects found in fragment_add_or_delete_admin screen
    private EditText add_or_delete_user_username_edittext;
    private Button add_or_delete_user_username_add_button;
    private Button add_or_delete_user_username_delete_button;
    private Button admin_logout_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_or_delete_admin, container, false);

        // connect the EditText XML elements to the Java logic
        add_or_delete_user_username_edittext = (EditText) v.findViewById(R.id.add_or_delete_user_username);

        // connect the Button XML elements to the Java logic
        add_or_delete_user_username_add_button = (Button) v.findViewById(R.id.add_or_delete_user_add);
        add_or_delete_user_username_delete_button = (Button) v.findViewById(R.id.add_or_delete_user_delete);
        admin_logout_button = (Button) v.findViewById(R.id.admin_logout_button);

        // sets an OnClickListener on add_or_delete_admin_username_add_button that, when the button
        // is pressed, adds the admin account to the admin accounts related to the creator account
        // in the Parse MBAAS service
        add_or_delete_user_username_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserToNetwork();
            }
        });

        // sets an OnClickListener on add_or_delete_admin_username_delete_button that, when the
        // button is pressed, deletes the admin account to the admin accounts related to the
        // creator account in the Parse MBAAS service
        add_or_delete_user_username_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserFromNetwork();
            }
        });

        // sets an OnClickListener on creator_logout_button that, when the button is pressed,
        // logs the creator account out of the running application and sends the client-user
        // to the SelectAccountTypeFragment
        admin_logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return v;
    }

    private void addUserToNetwork(){
        ParseObject admins = new ParseObject(MainActivity.getUser().getUsername() + "_users");
        admins.put("username", add_or_delete_user_username_edittext.getText().toString());
        admins.saveInBackground();
        Toast.makeText(getActivity(), "User added.", Toast.LENGTH_SHORT);
    }

    private void deleteUserFromNetwork(){
        String adminsTableName = MainActivity.getUser().getUsername() + "_users";
        ParseQuery<ParseObject> query = ParseQuery.getQuery(adminsTableName);
        query.whereEqualTo("username", add_or_delete_user_username_edittext.getText().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).deleteInBackground();
                }
            }
        });
        Toast.makeText(getActivity(), "User deleted.", Toast.LENGTH_SHORT);
    }

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
}
