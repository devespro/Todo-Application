package de.fhb.fbi.acs.maas.todoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.deves.maus.R;

import java.util.Collections;
import java.util.List;

import de.fhb.fbi.acs.maas.todoapp.accessors.AbstractActivityDataAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.IntentTodoItemAccessor;
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

        getActionBar().setHomeButtonEnabled(true);

        listview = (ListView) findViewById(R.id.list);
        registerForContextMenu(listview);
        // the button for adding new items
        Button newitemButton = (Button) findViewById(R.id.newitemButton);
        //TODO during rest connection -> remote ItemListAccessor
        accessor = new SQLiteTodoItemListAccessor();

        if (accessor instanceof AbstractActivityDataAccessor) {
            ((AbstractActivityDataAccessor) accessor).setActivity(this);
        }

        // obtain the adapter from the accessor, passing it the id of the
        // item layout to be used
        final ListAdapter adapter = accessor.getAdapter();
        listview.setAdapter(adapter);

        // set the listview as scrollable
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sort_date_fav, menu);
        getMenuInflater().inflate(R.menu.sort_fav_date, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        List<TodoItem> items = ((SQLiteTodoItemListAccessor)accessor).getItems();
        int id = item.getItemId();
        switch (id){
            case R.id.sort_date_fav: {
                sortItemsByDateAndFavourite(items);
                ((SQLiteTodoItemListAccessor) accessor).setItems(items);
                return true;
            }
            case R.id.sort_fav_date : {
                sortItemsByFavouriteAndDate(items);
                ((SQLiteTodoItemListAccessor) accessor).setItems(items);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(LOG_TAG, "onActivityResult(): " + data);

        TodoItem item = data != null ? (TodoItem) data.getSerializableExtra(ARG_ITEM_OBJECT) : null;
        // check which request we had
        if (requestCode == REQUEST_ITEM_DETAILS) {
            if (resultCode == RESPONSE_ITEM_EDITED) {
                Log.i(LOG_TAG, "onActivityResult(): updating the edited item with CHECKBOX " + item.isFavourite());
                this.accessor.updateItem(item);
            } else if (resultCode == RESPONSE_ITEM_DELETED) {
                Log.e(LOG_TAG, "onActivityResult(): ITEM TO DELETE -> " + item.getId());
                this.accessor.deleteItem(item);
            }
        } else if (requestCode == REQUEST_ITEM_CREATION
                && resultCode == RESPONSE_ITEM_EDITED) {
            Log.i(LOG_TAG, "onActivityResult(): adding the created item");
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
}
