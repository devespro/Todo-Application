package com.deves.testtodoapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Esien Novruzov
 */
public class LoginActivity extends Activity {
    public static final String EMAIL_PATTERN = "[A-Z0-9._%+-]+@[A-Z0-9-]+\\.[A-Z]{2,4}";
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    /**
     * the UI elements
     */
    private TextView errorTextView;
    private EditText email;
    private EditText password;
    private Button loginButton;

    /**
     * sample data fo debugging //TODO delete them after submitting the final version!
     */
    private static final String SAMPLE_EMAIL = "test@gmail.com";
    private static final int SAMPLE_PASSWORD = 123456;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        errorTextView = (TextView) findViewById(R.id.error_textview);
        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_button);

        assert email != null;
        assert password != null;
        loginButton.setEnabled(false);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorTextView.setText("");
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (email.getText().toString().isEmpty() || email.getText().toString() == "") {
                    errorTextView.setText(R.string.error_massage_empty_email);
                    Log.i(LOG_TAG, "onEditorAction: the submitted email was empty");
                    return true;
                } else {
                    if (isValidEmail(email.getText().toString())) {
                        Log.i(LOG_TAG, "onEditorAction: the submitted email was valid");
                        return false;
                    } else {
                        errorTextView.setText(R.string.error_massage_incorrect_email);
                        Log.e(LOG_TAG, "onEditorAction: the submitted email was invalid");
                        return true;
                    }
                }
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    if (loginButton.isEnabled())
                        Log.i(LOG_TAG, "onEditorAction: " + " sending the input data for checking");
                        runSyncDialog();
                }
                return false;
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If the error message is showing "Incorrect email" -> the changing of password digit will not erase the error message
                if (!errorTextView.getText().toString().equals(getResources().getString(R.string.error_massage_incorrect_email))) {
                    errorTextView.setText("");
                }
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(LOG_TAG, "onClick: the Login button has been clicked");
                    runSyncDialog();
                }
            });


        }

    /**
     * The login button is enabled only when a valid email and a 6-digit password are submitted
     */
    private void updateLoginButtonState(){
        if (email != null && password != null && !email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() && isValidEmail(email.getText().toString()) &&
                password.getText().toString().length() == 6) {

            loginButton.setEnabled(true);
        } else {
            loginButton.setEnabled(false);
        }
    }

    private boolean isValidEmail(String email){
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()){
            Log.e(LoginActivity.class.getSimpleName(), "onEditorAction: invalid email: " + email);
            return true;
        } else {
            Log.i(LoginActivity.class.getSimpleName(), "onEditorAction: the email is matched");
            return false;
        }
    }

    /**
     * //TODO need to be checked
     * @param email
     * @param password
     */
    private void startToDoActivity(String email, int password){
        if (isValidLogin(email, password)) {
            Intent intent = new Intent(this, ToToActivity.class);
            startActivity(intent);
        } else {
            errorTextView.setText(R.string.error_message_invalid_login);
        }
    }

    //TODO Don't forget about this
    private boolean isValidLogin(String email, int password){
        return email.equals(SAMPLE_EMAIL) && password == SAMPLE_PASSWORD;
    }

    /**
     * Run a dialog that is synchronised with some background process
     */
    public void runSyncDialog() {

        new AsyncTask<Void, Void, Object>() {

            private ProgressDialog dialog = null;

            @Override
            protected void onPreExecute() {

                dialog = ProgressDialog.show(LoginActivity.this,
                        "Please wait...", "Verifying your input data...");
            }

            /*
             * the "background process": sleep for 1 second
             */
            @Override
            protected Object doInBackground(Void... arg) {
                try {
                    // sleep and try it again...
                    Thread.sleep(1000);
                } catch (Throwable t) {
                    String err = "got exception on doInBackground(): " + t;
                    Log.e(LoginActivity.this.getClass().getName(), err, t);
                }

                return "test";
            }

            @Override
            protected void onPostExecute(Object response) {
                Log.i(LoginActivity.this.getClass().getName(), "onPostExecute()...");
                dialog.cancel();

                startToDoActivity(email.getText().toString(), Integer.parseInt(password.getText().toString()));
            }

        }.execute();

    }

}
