package com.xchange_place.traxists.traxists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Ryan Fletcher on 8/4/2015.
 */
public class AddOrDeleteAdminFragment extends Fragment {

    // GUI objects found in fragment_add_or_delete_admin screen
    private EditText add_or_delete_admin_username_edittext;
    private Button add_or_delete_admin_username_add_button;
    private Button add_or_delete_admin_username_delete_button;
    private Button creator_logout_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_or_delete_admin, container, false);

        add_or_delete_admin_username_edittext = (EditText) v.findViewById(R.id.add_or_delete_admin_username);
        add_or_delete_admin_username_add_button = (Button) v.findViewById(R.id.add_or_delete_admin_add);
        add_or_delete_admin_username_delete_button = (Button) v.findViewById(R.id.add_or_delete_admin_delete);
        creator_logout_button = (Button) v.findViewById(R.id.creator_logout_button);

        add_or_delete_admin_username_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAdminToNetwork();
            }
        });

        add_or_delete_admin_username_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAdminFromNetwork();
            }
        });

        creator_logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return v;
    }

    private void addAdminToNetwork(){
        ParseObject admins = new ParseObject(MainActivity.getUser().getUsername() + "_admins");
        admins.put("username", add_or_delete_admin_username_edittext.getText().toString());
        admins.saveInBackground();
    }

    private void deleteAdminFromNetwork(){
        String adminsTableName = MainActivity.getUser().getUsername() + "_admins";
        ParseQuery<ParseObject> query = ParseQuery.getQuery(adminsTableName);
        query.whereEqualTo("username", add_or_delete_admin_username_edittext.getText().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).deleteInBackground();
                }
            }
        });
    }

    private void logout(){
        // clear stored user data
        User user = MainActivity.getUser();
        user.setAccType((short) -1);
        user.setRecovery3(null);
        user.setRecovery2(null);
        user.setRecovery1(null);
        user.setUsername(null);
        user.setPassword(null);
        user.setLoggedIn(false);

        // move to LoginFragment
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_add_or_delete_admins,
                        new LoginFragment())
                .addToBackStack(null)
                .commit();
    }
}
