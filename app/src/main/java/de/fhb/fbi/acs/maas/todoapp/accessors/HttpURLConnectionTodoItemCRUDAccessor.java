package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;
import de.fhb.fbi.acs.maas.todoapp.model.TodoItemCRUDAccessor;

/**
 * @author novruzov
 */
public class HttpURLConnectionTodoItemCRUDAccessor implements TodoItemCRUDAccessor {
    protected static String logger = HttpURLConnectionTodoItemCRUDAccessor.class.getSimpleName();

    private String baseUrl;

    private ObjectMapper mObjectMapper = new ObjectMapper();

    public HttpURLConnectionTodoItemCRUDAccessor(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public List<TodoItem> readAllItems() {

        Log.i(logger, "readAllItems()");

        try {
            // obtain a http url connection from the base url
            HttpURLConnection con = (HttpURLConnection) (new URL(baseUrl))
                    .openConnection();
            Log.d(logger, "readAllItems(): got connection: " + con);
            // set the request method (GET is default anyway...)
            con.setRequestMethod("GET");
            // then initiate sending the request...
            InputStream is = con.getInputStream();
            // check the response code
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final List<TodoItem> items = mObjectMapper.readValue(is, new TypeReference<List<TodoItem>>() {});
                Log.i(logger, "readAllItems(): " + items);

                return items;
            } else {
                Log.e(logger,
                        "readAllItems(): got response code: "
                                + con.getResponseCode());
            }
        } catch (Exception e) {
            Log.e(logger, "readAllItems(): got exception: " + e);
        }

        return new ArrayList<>();

    }

    @Override
    public TodoItem createItem(TodoItem item) {
        Log.i(logger, "createItem(): " + item);

        try {
            // obtain a http url connection from the base url
            HttpURLConnection con = (HttpURLConnection) (new URL(baseUrl))
                    .openConnection();
            Log.d(logger, "createItem(): got connection: " + con);
            // set the request method 
            con.setRequestMethod("POST");
            // indicate that we want to send a request body
            con.setRequestProperty("Content-Type", "application/json");
            Log.e(logger, "createItem(): " + "after setProperty");
            con.setDoOutput(true);
            Log.e(logger, "createItem(): " + "after setOutput");
            // obtain the output stream and write the item as json object to it
            OutputStream os = con.getOutputStream();
            Log.e(logger, "createItem(): " + "after setOutputStream");
            os.write(mObjectMapper.writeValueAsString(item).getBytes());
            Log.e(logger, "createItem(): " + "after writeValues");
            // then initiate sending the request...
            InputStream is = con.getInputStream();
            // check the response code
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                TodoItem ret = mObjectMapper.readValue(is, TodoItem.class);
                Log.i(logger, "createItem(): " + ret);

                return ret;
            } else {
                Log.e(logger,
                        "createItem(): got response code: "
                                + con.getResponseCode());
            }
        } catch (Exception e) {
            Log.e(logger, "createItem(): got exception: " + e);
        }

        return null;
    }

    @Override
    public boolean deleteItem(long itemId) {
        Log.i(logger, "deleteItem(): " + itemId);

        try {
            // obtain a http url connection from the base url
            HttpURLConnection con = (HttpURLConnection) (new URL(baseUrl + "/"
                    + itemId)).openConnection();
            Log.d(logger, "deleteItem(): got connection: " + con);
            // set the request method 
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            // then initiate sending the request...
            InputStream is = con.getInputStream();
            Log.e(logger, "deleteItem: InputStream " + is);
            // check the response code
            Log.e(logger, "deleteItem: getResponseCode"+ con.getResponseCode());
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {

                return mObjectMapper.readValue(is, Boolean.class);
            } else {
                Log.e(logger,
                        "deleteItem(): got response code: "
                                + con.getResponseCode());
            }
        } catch (Exception e) {
            Log.e(logger, "deleteItem(): got exception: " + e);
        }

        return false;
    }

    /**
     * the only difference from create is the PUT method, i.e. the common
     * functionality could be factored out...
     */
    @Override
    public TodoItem updateItem(TodoItem item) {
        Log.i(logger, "updateItem(): " + item);

        try {
            // obtain a http url connection from the base url
            HttpURLConnection con = (HttpURLConnection) (new URL(baseUrl))
                    .openConnection();
            Log.d(logger, "updateItem(): got connection: " + con);
            // set the request method 
            con.setRequestMethod("PUT");
            // indicate that we want to send a request body
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            // obtain the output stream and write the item as json object to it
            OutputStream os = con.getOutputStream();
            os.write(mObjectMapper.writeValueAsString(item).getBytes());
            // then initiate sending the request...
            InputStream is = con.getInputStream();
            // check the response code
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final TodoItem ret = mObjectMapper.readValue(is, TodoItem.class);

                Log.i(logger, "updateItem(): " + ret);

                return ret;
            } else {
                Log.e(logger,
                        "updateItem(): got response code: "
                                + con.getResponseCode());
            }
        } catch (Exception e) {
            Log.e(logger, "updateItem(): got exception: " + e);
        }

        return null;
    }
}
