package com.xchange_place.traxists.traxists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;

/**
 * Created by Ryan Fletcher on 8/3/2015.
 */
public class RecoveryFragment extends Fragment {

    // GUI objects found in fragment_recovery_questions
    private EditText recovery_question_1_edittext;
    private EditText recovery_question_2_edittext;
    private EditText recovery_question_3_edittext;
    private Button recovery_questions_prev_button;
    private Button recovery_questions_next_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_confirm_password, container, false);

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
                replaceWithGoogleBillingFragmentAndAddToBackstackAndStoreRecoveryAnswers();
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
                .replace(R.id.fragment_recovery_questions,
                        new ConfirmPasswordFragment())
                .addToBackStack(null)
                .commit();
    }

    // commits a FragmentTransaction that brings the user forward to GoogleBillingFragment,
    // adds the transaction to the backstack, and stores the answers to the recovery questions
    // in the User object of MainActivity

    private void replaceWithGoogleBillingFragmentAndAddToBackstackAndStoreRecoveryAnswers(){
        MainActivity.getUser().setRecovery1(recovery_question_1_edittext.getText().toString());
        MainActivity.getUser().setRecovery2(recovery_question_2_edittext.getText().toString());
        MainActivity.getUser().setRecovery3(recovery_question_3_edittext.getText().toString());

        // start Braintree billing
        Intent intent = new Intent(getActivity(), BraintreePaymentActivity.class);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJhYWMxN2QxOGRhMDM4MTQ5ODdlZmUyNmZkYmViODY5NWMwMWI5ZjFlOTRkZDcxMjAwNDdlZjc1ZTg5MjMwZjBmfGNyZWF0ZWRfYXQ9MjAxNS0wOC0wNFQwNjo0NDo1Ny4zMzkwODA4NjArMDAwMFx1MDAyNm1lcmNoYW50X2lkPWRjcHNweTJicndkanIzcW5cdTAwMjZwdWJsaWNfa2V5PTl3d3J6cWszdnIzdDRuYzgiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvZGNwc3B5MmJyd2RqcjNxbi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzL2RjcHNweTJicndkanIzcW4vY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIn0sInRocmVlRFNlY3VyZUVuYWJsZWQiOnRydWUsInRocmVlRFNlY3VyZSI6eyJsb29rdXBVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvZGNwc3B5MmJyd2RqcjNxbi90aHJlZV9kX3NlY3VyZS9sb29rdXAifSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwibWVyY2hhbnRBY2NvdW50SWQiOiJzdGNoMm5mZGZ3c3p5dHc1IiwiY3VycmVuY3lJc29Db2RlIjoiVVNEIn0sImNvaW5iYXNlRW5hYmxlZCI6dHJ1ZSwiY29pbmJhc2UiOnsiY2xpZW50SWQiOiIxMWQyNzIyOWJhNThiNTZkN2UzYzAxYTA1MjdmNGQ1YjQ0NmQ0ZjY4NDgxN2NiNjIzZDI1NWI1NzNhZGRjNTliIiwibWVyY2hhbnRBY2NvdW50IjoiY29pbmJhc2UtZGV2ZWxvcG1lbnQtbWVyY2hhbnRAZ2V0YnJhaW50cmVlLmNvbSIsInNjb3BlcyI6ImF1dGhvcml6YXRpb25zOmJyYWludHJlZSB1c2VyIiwicmVkaXJlY3RVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbS9jb2luYmFzZS9vYXV0aC9yZWRpcmVjdC1sYW5kaW5nLmh0bWwiLCJlbnZpcm9ubWVudCI6Im1vY2sifSwibWVyY2hhbnRJZCI6ImRjcHNweTJicndkanIzcW4iLCJ2ZW5tbyI6Im9mZmxpbmUiLCJhcHBsZVBheSI6eyJzdGF0dXMiOiJtb2NrIiwiY291bnRyeUNvZGUiOiJVUyIsImN1cnJlbmN5Q29kZSI6IlVTRCIsIm1lcmNoYW50SWRlbnRpZmllciI6Im1lcmNoYW50LmNvbS5icmFpbnRyZWVwYXltZW50cy5zYW5kYm94LkJyYWludHJlZS1EZW1vIiwic3VwcG9ydGVkTmV0d29ya3MiOlsidmlzYSIsIm1hc3RlcmNhcmQiLCJhbWV4Il19fQ==");
        startActivityForResult(intent, 100);
    }

}
