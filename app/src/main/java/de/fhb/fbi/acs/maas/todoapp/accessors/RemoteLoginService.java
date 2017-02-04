package de.fhb.fbi.acs.maas.todoapp.accessors;


import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.fhb.fbi.acs.maas.todoapp.model.Credentials;

/**
 * @author novruzov
 * Rest implementation of LoginService
 */
public class RemoteLoginService implements LoginService {
    private static final String LOG_TAG = RemoteLoginService.class.getSimpleName();
    private ObjectMapper mObjectMapper = new ObjectMapper();

    @Override
    public boolean processLogin(String email, String password) {
        Credentials credentials = new Credentials(email, password);
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) (new URL(getRestLoginBaseUrl())).openConnection();
            Log.d(LOG_TAG, "processLogin(): got connection: " + con);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            // obtain the output stream and write the credentials object as json object to it
            OutputStream os = con.getOutputStream();
            os.write(mObjectMapper.writeValueAsString(credentials).getBytes());
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.i(LOG_TAG, "User validation - success");
                return true;
            } else {
                Log.e(LOG_TAG, "Invalid login: got response code: " + con.getResponseCode());
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    private String getWebappBaseUrl() {
        return "http://10.0.2.2:8080/DataAccessRemoteWebapp/";
    }

    private String getRestBaseUrl() {
        return getWebappBaseUrl() + "rest";
    }

    private String getRestLoginBaseUrl() {
        return getRestBaseUrl() + "/authentication";
    }
}
