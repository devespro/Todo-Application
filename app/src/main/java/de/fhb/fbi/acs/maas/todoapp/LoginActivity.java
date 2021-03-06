package de.fhb.fbi.acs.maas.todoapp;

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

import com.deves.maus.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhb.fbi.acs.maas.todoapp.accessors.LoginService;
import de.fhb.fbi.acs.maas.todoapp.accessors.RemoteLoginService;

/**
 * @author novruzov
 */
public class LoginActivity extends Activity {
    public static final String EMAIL_PATTERN = "[A-Z0-9._%+-]+@[A-Z0-9-]+\\.[A-Z]{2,4}";
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    public static final String CONNECTION_STATUS = "connectionStatus";
    public static final String ACCESSOR_CLASS = "accessorClass";
    private String connectionStatus = "offline";
    private String accessorClass = "local";
    private static int firstFocus = 0;

    /**
     * the UI elements
     */
    private TextView errorTextView;
    private EditText email;
    private EditText password;
    private Button loginButton;

    private LoginService loginService = new RemoteLoginService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (isServerAvailable()) {
            accessorClass="remote";
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
                    return processEmailChanges();
                }
            });

            email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (firstFocus != 0)
                        processEmailChanges();
                    firstFocus++;
                }
            });

            password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (loginButton.isEnabled()) {
                            String submittedEmail = email.getText().toString();
                            String submittedPassword = password.getText().toString();
                            Log.i(LOG_TAG, "onEditorAction: " + " sending the input data for checking: " + submittedEmail + ", " + submittedPassword);
                            MyTaskParams myTaskParams = new MyTaskParams(submittedEmail, submittedPassword);
                            runSyncDialog(myTaskParams);
                        }
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
                    MyTaskParams myTaskParams = new MyTaskParams(email.getText().toString(), password.getText().toString());
                    runSyncDialog(myTaskParams);
                }
            });
        } else {
            startToDoActivity();
        }
    }


    //the login button is enabled only when a valid email and a 6-digit password are submitted

    private void updateLoginButtonState() {
        if (email != null && password != null && !email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() && isValidEmail(email.getText().toString()) &&
                password.getText().toString().length() == 6) {

            loginButton.setEnabled(true);
        } else {
            loginButton.setEnabled(false);
        }
    }

    private boolean processEmailChanges(){
        if (email.getText().toString().isEmpty() || email.getText().toString() == "") {
            errorTextView.setText(R.string.error_massage_empty_email);
            Log.e(LOG_TAG, "onEditorAction: the submitted email was empty");
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

    private boolean isValidEmail(String email) {
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    private void startToDoActivity() {
        Intent intent = new Intent(this, TodoActivity.class);
        intent.putExtra(CONNECTION_STATUS, connectionStatus);
        intent.putExtra(ACCESSOR_CLASS, accessorClass);
        startActivity(intent);
    }

    private void showErrorMessage() {
        errorTextView.setText(R.string.error_message_invalid_login);
    }

    private boolean processLogin(String email, String password) {
        boolean isValidAttempt = loginService.processLogin(email,password);
        if (isValidAttempt){
            connectionStatus = "online";
        }
        return isValidAttempt;
    }

    /**
     * Run a dialog that is synchronised with some background process
     */
    public void runSyncDialog(MyTaskParams params) {
        Log.i(LOG_TAG, "runSyncDialog: EMAIL -> " + params.email + " pas " + params.password);
        LoginTask loginTask = new LoginTask();
        loginTask.execute(params);

    }

    public static String getWebappBaseUrl() {
        return "http://10.0.2.2:8080/DataAccessRemoteWebapp/";
    }

    public static String getRestBaseUrl() {
        return getWebappBaseUrl() + "rest";
    }

    public static String getRestLoginBaseUrl() {
        return getRestBaseUrl() + "/authentication";
    }


    private class LoginTask extends AsyncTask<MyTaskParams, Void, Boolean> {

        private ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(LoginActivity.this,
                    "Please wait...", "Verifying your input data...");
        }

        /*
         * the "background process": check and sleep for 1 second
         */
        @Override
        protected Boolean doInBackground(MyTaskParams... arg) {
            boolean result = false;
            try {
                result = processLogin(arg[0].email, arg[0].password);
                Thread.sleep(1000);
            } catch (Throwable t) {
                String err = "got exception on doInBackground(): " + t;
                Log.e(LoginActivity.this.getClass().getName(), err, t);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            Log.i(LoginActivity.this.getClass().getName(), "onPostExecute()...");
            dialog.cancel();

            if (response) {
                startToDoActivity();
                accessorClass = "remote";
            } else {
                showErrorMessage();
            }
        }
    }

    private static class MyTaskParams {
        String email;
        String password;

        MyTaskParams(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    private boolean isServerAvailable() {
        try {
            return new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        HttpURLConnection con = (HttpURLConnection)(new URL(getRestLoginBaseUrl())).openConnection();
                        con.getResponseCode();
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                }
            }.execute().get();
        } catch (Exception e) {
            Log.e(LOG_TAG, "isServerAvailable: got exception " + e);
        }
        return false;
    }

}
