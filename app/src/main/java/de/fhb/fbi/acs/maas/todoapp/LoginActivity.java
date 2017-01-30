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

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhb.fbi.acs.maas.todoapp.model.Credentials;

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

    private ObjectMapper mObjectMapper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (isServerAvailable()) {

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
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (loginButton.isEnabled())
                            Log.i(LOG_TAG, "onEditorAction: " + " sending the input data for checking");
                        MyTaskParams myTaskParams = new MyTaskParams(email.getText().toString(), password.getText().toString());
                        runSyncDialog(myTaskParams);
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

    /**
     * The login button is enabled only when a valid email and a 6-digit password are submitted
     */
    private void updateLoginButtonState() {
        if (email != null && password != null && !email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() && isValidEmail(email.getText().toString()) &&
                password.getText().toString().length() == 6) {

            loginButton.setEnabled(true);
        } else {
            loginButton.setEnabled(false);
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
        startActivity(intent);
    }

    private void showErrorMessage() {
        errorTextView.setText(R.string.error_message_invalid_login);
    }

    private boolean processLogin(String email, String password) {
        Credentials credentials = new Credentials(email, password);
        // obtain a http url connection from the base url
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) (new URL(getRestLoginBaseUrl())).openConnection();
            Log.d(LOG_TAG, "processLogin(): got connection: " + con);

            con.setRequestMethod("POST");
            // indicate that we want to send a request body
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            // obtain the output stream and write the item as json object to it
            OutputStream os = con.getOutputStream();
            os.write(mObjectMapper.writeValueAsString(credentials).getBytes());
            // then initiate sending the request...
            // InputStream is = con.getInputStream();
            // check the response code
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {

                Log.i(LOG_TAG, "User validation - success");
                return true;

            } else {
                Log.e(LOG_TAG, "Invalid login: got response code: " + con.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Run a dialog that is synchronised with some background process
     */
    public void runSyncDialog(MyTaskParams params) {
        Log.e(LOG_TAG, "runSyncDialog: EMAIL -> " + params.email + " pas " + params.password);
        LoginTask loginTask = new LoginTask();
        loginTask.execute(params);

    }

    /**
     * get the baseUrl of the webapp used as data source and media resource provider
     *
     * @return
     */
    private String getWebappBaseUrl() {
        return "http://10.0.2.2:8080/DataAccessRemoteWebapp/";
    }

    private String getRestBaseUrl() {
        return getWebappBaseUrl() + "rest";
    }

    private String getRestLoginBaseUrl() {
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
                    Log.e(LOG_TAG, "checkServerConnection: INSIDE CHECK_SERVER");
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
                    Log.e(LOG_TAG, "STATUS _> " + aBoolean);
                }
            }.execute().get();
        } catch (Exception e) {
            Log.e(LOG_TAG, "isServerAvailable: got exception " + e);
        }
        return false;
    }

}
