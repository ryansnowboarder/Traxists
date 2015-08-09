package com.xchange_place.traxists.traxists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Ryan Fletcher on 8/3/2015.
 *
 *
 * This fragment is designed to for the user to enter recovery questions associated with their
 * account. The entered data is stored in the User instance of MainActivity and, upon clicking
 * the next button on this fragment GUI, is sent to Parse in MainActivity, creating a new
 * account.
 *
 */
public class RecoveryFragment extends Fragment {

    private static final String TAG = "RecoveryFragment";

    // GUI objects found in fragment_recovery_questions
    private EditText recovery_question_1_edittext;
    private EditText recovery_question_2_edittext;
    private EditText recovery_question_3_edittext;
    private Button recovery_questions_prev_button;
    private Button recovery_questions_next_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recovery_questions, container, false);

        // connect the EditText XML components to the variables in this Java logic
        recovery_question_1_edittext = (EditText) v.findViewById(R.id.recovery_question_1);
        recovery_question_2_edittext = (EditText) v.findViewById(R.id.recovery_question_2);
        recovery_question_3_edittext = (EditText) v.findViewById(R.id.recovery_question_3);

        // connect the Button XML components to the varaibles in this Java logic
        recovery_questions_prev_button = (Button) v.findViewById(R.id.recovery_prev_button);
        recovery_questions_next_button = (Button) v.findViewById(R.id.recovery_next_button);

        // setup onClickListener on recovery_questions_prev_button that brings the user
        // back to the ConfirmPasswordFragment seen previously in the GUI wireframe flow
        // and adds the transaction to the backstack
        recovery_questions_prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceWithConfirmPasswordFragmentAndAddToBackstack();
            }
        });

        // setup onClickListener on recovery_questions_next_button that brings the user
        // forward in the wireframe flow to the GoogleBilling screen, commits the
        // transaction to the backstack, and stores the answers to the recovery questions
        // in the User object of MainActivity
        recovery_questions_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceWithNextFragmentAndAddToBackstackAndStoreRecoveryAnswers();
            }
        });
        return v;
    }

    // commits a FragmentTransaction that brings the user back to ConfirmPasswordFragment
    // and adds the transaction to the backstack
    private void replaceWithConfirmPasswordFragmentAndAddToBackstack(){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment,
                        new ConfirmPasswordFragment())
                .addToBackStack(null)
                .commit();
    }

    // calls the MainActivity.postUserToParse() function and stores the answers to
    // the recovery questions in the User object of MainActivity
    private void replaceWithNextFragmentAndAddToBackstackAndStoreRecoveryAnswers(){
        MainActivity.getUser().setRecovery1(recovery_question_1_edittext.getText().toString());
        MainActivity.getUser().setRecovery2(recovery_question_2_edittext.getText().toString());
        MainActivity.getUser().setRecovery3(recovery_question_3_edittext.getText().toString());

        MainActivity.postUserToParse();
        BringCustomerToNextScreen();
    }

    // brings the customer to the next screen, depending on their account type
    private void BringCustomerToNextScreen(){
        // bring creator accounts to AddOrDeleteAdminFragment where they
        // can manage their list of admins and logout
        if (MainActivity.getUser().getAccType() == 0){
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment,
                            new AddOrDeleteAdminFragment())
                    .addToBackStack(null)
                    .commit();
        }
        // bring admin accounts to the Google Maps API where they can view
        // the locations of their related user accounts
        if (MainActivity.getUser().getAccType() == 1){
            MainActivity.onCreateOptionMenu(MainActivity.menu);
        }
        // Bring user accounts to the ApiLoginsFragment where they
        // log into the Facebook API. Permission to use the Google
        // Geolocation API is granted or denied upon the launch of
        // MainActivity.
        else {
            if (MainActivity.getUser().getAccType() == 2){
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment,
                                new ApiLoginsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

}
