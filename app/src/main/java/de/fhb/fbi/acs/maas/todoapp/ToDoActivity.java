package de.fhb.fbi.acs.maas.todoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.deves.maus.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.fhb.fbi.acs.maas.todoapp.accessors.AbstractActivityDataAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.IntentTodoItemAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.RemoteTodoItemListAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.SQLiteTodoItemListAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.TodoItemListAccessor;
import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;

/**
 * @author Esien Novruzov
 */
public class TodoActivity extends Activity {

    private static final String LOG_TAG = TodoActivity.class.getName();

    /**
     * the argname with which we will pass the item to the subview
     */
    public static final String ARG_ITEM_OBJECT = "itemObject";

    /**
     * the result code that indicates that some item was changed
     */
    public static final int RESPONSE_ITEM_EDITED = 1;

    /**
     * the result code that indicates that the item shall be deleted
     */
    public static final int RESPONSE_ITEM_DELETED = 2;

    /**
     * the result code that indicates that nothing has been changed
     */
    public static final int RESPONSE_NOCHANGE = -1;

    /**
     * the constant for the subview request
     */
    public static final int REQUEST_ITEM_DETAILS = 2;

    /**
     * the constant for the new item request
     */
    public static final int REQUEST_ITEM_CREATION = 1;
    public static final int DELETE_CONTEXT_MENU_ID = 1;
    public static final int CANCEL_CONTEXT_MENU_ID = 2;
    public static final int EDIT_CONTEXT_MENU_ID = 3;
    private ListView listview;
    /**
     * the data accessor for the data items
     */
    private TodoItemListAccessor accessor;

    /**
     * the items that will be display
     */
    private List<TodoItem> itemlist = new ArrayList<>();

    private ArrayAdapter<TodoItem> adapter;

    private String accessorInfo;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Available actions");
        menu.setHeaderIcon(android.R.drawable.ic_menu_delete);
        menu.add(0, EDIT_CONTEXT_MENU_ID, 0, "Edit");
        menu.add(0, DELETE_CONTEXT_MENU_ID, 0, "Delete");
        menu.add(0, CANCEL_CONTEXT_MENU_ID, 0, "Cancel");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.e(LOG_TAG, "onContextItemSelected: id-> " + item.getItemId());
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;
        TodoItem item1 = accessor.getSelectedItem(listPosition);
        Log.e(LOG_TAG, "onContextItemSelected: id-> " + item1.getId());
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        String connectionStatus = getIntent().getExtras().getString(LoginActivity.CONNECTION_STATUS);
        accessorInfo = getIntent().getExtras().getString(LoginActivity.ACCESSOR_CLASS);
        Log.e(LOG_TAG, "onCreate: status-> " + connectionStatus);

        TextView connectionStatusTextView = (TextView) findViewById(R.id.connection_status);
        setConnectionStatus(connectionStatusTextView,connectionStatus);

        listview = (ListView) findViewById(R.id.list);
        registerForContextMenu(listview);
        // the button for adding new items
        Button newitemButton = (Button) findViewById(R.id.newitemButton);

        if (accessorInfo.equals("local")) {
            accessor = new SQLiteTodoItemListAccessor();
        } else {
            accessor = new RemoteTodoItemListAccessor();
        }
        Log.e(LOG_TAG, "onCreate: using accessor -> " + accessor.getClass().getSimpleName() );
        itemlist = new ArrayList<>();

        if (accessor instanceof AbstractActivityDataAccessor) {
            ((AbstractActivityDataAccessor) accessor).setActivity(this);
        }

        adapter = accessor.getAdapter(itemlist);
        // the adapter is set to display changes immediately
        adapter.setNotifyOnChange(true);
        listview.setAdapter(adapter);

        listview.setScrollBarStyle(ListView.SCROLLBARS_INSIDE_OVERLAY);

        // set a listener that reacts to the selection of an element
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View itemView, int itemPosition, long itemId) {

                Log.i(LOG_TAG, "onItemClick: position is: " + itemPosition
                        + ", id is: " + itemId);
                TodoItem item = accessor.getSelectedItem(itemPosition);
                processItemSelection(item);
            }

        });

        newitemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNewItemRequest();
            }
        });

        // finally, we add the itemlist asynchronously
        new AsyncTask<Void, Void, List<TodoItem>>() {
            @Override
            protected List<TodoItem> doInBackground(Void... items) {
                Log.e(LOG_TAG, "onPostExecute: INSIDE_ON_BACKGROUND" );
                return TodoActivity.this.accessor.getAll();
            }

            @Override
            protected void onPostExecute(List<TodoItem> items) {
                Log.e(LOG_TAG, "onPostExecute: INSIDE_ONPOST" );
                itemlist.addAll(items);
                adapter.notifyDataSetChanged();
            }
        }.execute();

    }

    private void processNewItemRequest() {
        Log.i(LOG_TAG, "processNewItemRequest()");
        Intent intent = new Intent(TodoActivity.this, ItemDetailsActivity.class);
        // start the details activity with the intent
        // also specify the accessor class
        intent.putExtra("accessorClass",
                IntentTodoItemAccessor.class.getName());
        startActivityForResult(intent, REQUEST_ITEM_CREATION);
    }

    private void processItemSelection(TodoItem item) {
        Log.i(LOG_TAG, "processItemSelection(): " + item);
        // create an intent for opening the details view
        Intent intent = new Intent(TodoActivity.this,
                ItemDetailsActivity.class);
        // pass the item to the intent
        intent.putExtra(ARG_ITEM_OBJECT, item);
        // also specify the accessor class
        intent.putExtra("accessorClass",
                IntentTodoItemAccessor.class.getName());
        // start the details activity with the intent
        startActivityForResult(intent, REQUEST_ITEM_DETAILS);

    }

    private void setConnectionStatus(TextView view, String connectionStatus){
        if (connectionStatus.equals("offline")){
            view.setText(" offline ");
            view.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.presence_offline, 0, 0, 0);
        } else {
            view.setText(" online ");
            view.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.presence_online, 0, 0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sort_date_fav, menu);
        getMenuInflater().inflate(R.menu.sort_fav_date, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        List<TodoItem> items;
        if (accessor instanceof SQLiteTodoItemListAccessor) {
            items = accessor.getAll();
        } else {
            items = getRemoteAll((RemoteTodoItemListAccessor)accessor);
        }
        int id = item.getItemId();
        switch (id){
            case R.id.sort_date_fav: {
                if (accessor instanceof RemoteTodoItemListAccessor){
                    processRemoteSortByDateAndFavourite(items);
                    Log.e(LOG_TAG, "Sorting : sort_date_fav");
                    accessor.setItems(items);
                    return true;
                }
                sortItemsByDateAndFavourite(items);
                accessor.setItems(items);
                return true;
            }
            case R.id.sort_fav_date : {
                if (accessor instanceof RemoteTodoItemListAccessor){
                    processRemoteSortByFavouriteAndDate(items);
                    Log.e(LOG_TAG, "Sorting : sort_fav_date");
                    accessor.setItems(items);
                    return true;
                }
                sortItemsByFavouriteAndDate(items);
                accessor.setItems(items);
                return true;
            }
            default:
                Log.i(LOG_TAG, "onMenuItemSelected(): invalid item id " + id);

        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void sortItemsByDateAndFavourite(List<TodoItem> items){
        Collections.sort(items, TodoItem.sortByDateAndFavouriteComparator());
    }

    private void sortItemsByFavouriteAndDate(List<TodoItem> items){
        Collections.sort(items, TodoItem.sortByFavouriteAndDateComparator());
    }

    private void processRemoteSortByDateAndFavourite(final List<TodoItem> items){
        Log.e(LOG_TAG, "processRemoteSortByDateAndFavourite: ITEMS_BEFORE " + items );
        new AsyncTask<List<TodoItem>, Void, List<TodoItem>>() {
            @Override
            protected List<TodoItem> doInBackground(final List<TodoItem>... params) {
                Collections.sort(params[0], TodoItem.sortByDateAndFavouriteComparator());
                return params[0];
            }

            @Override
            protected void onPostExecute(List<TodoItem> todoItemList) {
                super.onPostExecute(todoItemList);
                items.clear();
                items.addAll(todoItemList);
                adapter.notifyDataSetChanged();
            }
        }.execute(items);
        Log.e(LOG_TAG, "processRemoteSortByDateAndFavourite: ITEMS_AFTER " + items);
    }

    private List<TodoItem> getRemoteAll(final RemoteTodoItemListAccessor accessor){
        final List<TodoItem> list = new ArrayList<>();
        new AsyncTask<Void, Void, List<TodoItem>>() {
            @Override
            protected List<TodoItem> doInBackground(Void... params) {
                return accessor.getAll();
            }

            @Override
            protected void onPostExecute(List<TodoItem> todoItemList) {
                list.addAll(todoItemList);
            }
        }.execute();
        Log.e(LOG_TAG, "processRemoteSortByDateAndFavourite: ITEMS_AFTRERR " + list);
        return list;
    }

    private void processRemoteSortByFavouriteAndDate(List<TodoItem> items){

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(LOG_TAG, "onActivityResult(): " + data);

        TodoItem item = data != null ? (TodoItem) data.getSerializableExtra(ARG_ITEM_OBJECT) : null;
        boolean onlineMode = !accessorInfo.equals("local");
        // check which request we had
        if (requestCode == REQUEST_ITEM_DETAILS) {
            if (resultCode == RESPONSE_ITEM_EDITED) {
                if (onlineMode){
                    processRemoteItemUpdate(item);
                    return;
                }
                this.accessor.updateItem(item);
            } else if (resultCode == RESPONSE_ITEM_DELETED) {
                Log.e(LOG_TAG, "onActivityResult(): ITEM TO DELETE -> " + item.getId());
                if (onlineMode){
                    processRemoteItemDeleted(item);
                    return;
                }
                this.accessor.deleteItem(item);
            }
        } else if (requestCode == REQUEST_ITEM_CREATION
                && resultCode == RESPONSE_ITEM_EDITED) {
            Log.i(LOG_TAG, "onActivityResult(): adding the created item");
            if (onlineMode){
                processRemoteItemAdd(item);
                return;
            }
            this.accessor.addItem(item);
        }
    }
    /**
     * if we stop, we signal this to the accessor (which is necessary in order to avoid trouble when operating on dbs)
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy(): will signal finalisation to the accessors");
        this.accessor.close();

        super.onStop();
    }

    private void processRemoteItemAdd(TodoItem item){
        Log.e(LOG_TAG, "processRemoteItemAdd: " + item);
        new AsyncTask<TodoItem, Void, TodoItem>() {
            @Override
            protected TodoItem doInBackground(TodoItem... items) {
                return TodoActivity.this.accessor.addItem(items[0]);
            }

            @Override
            protected void onPostExecute(TodoItem item) {
                if (item != null) {
                    adapter.add(item);
                }
            }
        }.execute(item);
    }

    public void processRemoteItemUpdate(TodoItem item){
        Log.e(LOG_TAG, "processRemoteItemUpdate: " + item);
        new AsyncTask<TodoItem, Void, TodoItem>() {
            @Override
            protected TodoItem doInBackground(TodoItem... items) {
                return TodoActivity.this.accessor
                        .updateItem(items[0]);
            }

            @Override
            protected void onPostExecute(TodoItem item) {
                if (item != null) {
                    // read out the item from the list and update it
                    itemlist.get(itemlist.indexOf(item)).updateFrom(item);
                    // notify the adapter that the item has been changed
                    adapter.notifyDataSetChanged();
                }
            }
        }.execute(item);
    }

    private void processRemoteItemDeleted(TodoItem item){
        new AsyncTask<TodoItem, Void, TodoItem>() {
            @Override
            protected TodoItem doInBackground(TodoItem... items) {
                if (TodoActivity.this.accessor.deleteItem(items[0])) {
                    return items[0];
                } else {
                    Log.e(LOG_TAG, "the item" + items[0]
                            + " could not be deleted by the accessor!");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(TodoItem item) {
                if (item != null) {
                    adapter.remove(item);
                }
            }
        }.execute(item);
    }
}
