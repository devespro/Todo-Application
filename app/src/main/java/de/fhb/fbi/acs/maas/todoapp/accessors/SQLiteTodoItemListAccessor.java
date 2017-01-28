package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.database.Cursor;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;

/**
 * Created by Esien Novruzov on 28/01/17.
 */
public class SQLiteTodoItemListAccessor extends AbstractActivityDataAccessor implements TodoItemListAccessor {

    private static final String LOG_TAG = SQLiteTodoItemListAccessor.class.getSimpleName();

    /**
     * the list of items
     */
    private List<TodoItem> items = new ArrayList<>();

    /**
     * the adapter operating on the list
     */
    private ArrayAdapter<TodoItem> adapter;

    private SQLiteDBHelper mHelper;

    @Override
    public void addItem(TodoItem item) {
        mHelper.insert(item);

        this.adapter.add(item);
    }

    @Override
    public ListAdapter getAdapter() {
        mHelper = new SQLiteDBHelper(getActivity());
        mHelper.prepareSQLiteDatabase();

        readOutItemsFromDatabase();

        this.adapter = TodoItemListViews.createTodoItemArrayAdapter(getActivity(),items);
        this.adapter.setNotifyOnChange(true);

        return this.adapter;
    }

    private void readOutItemsFromDatabase(){
        Cursor cursor = mHelper.getCursor();

        Log.i(LOG_TAG, "getAdapter(): got a cursor: " + cursor);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // create a new item and add it to the list
            this.items.add(mHelper.createItemFromCursor(cursor));
            cursor.moveToNext();
        }

        Log.i(LOG_TAG, "readOutItemsFromDatabase(): read out items: " + this.items);
    }

    @Override
    public void updateItem(TodoItem item) {
        mHelper.update(item);
        this.adapter.notifyDataSetChanged();
        // then update the item and notify the adapter
        if (lookupItem(item) != null) {
            Log.e(LOG_TAG, "updateItem " + item);
            lookupItem(item).updateFrom(item);
            this.adapter.notifyDataSetChanged();
        } else {
            Log.e(LOG_TAG, "updateItem NOT FOUND" + item);
        }
    }

    @Override
    public void deleteItem(TodoItem item) {
        mHelper.delete(item);

        this.adapter.remove(lookupItem(item));
    }

    /**
     * get the item from the list, checking identity of ids (as the argument
     * value may have been serialised/deserialised we cannot check for identity
     * of objects)
     */
    private TodoItem lookupItem(TodoItem item) {
        for (TodoItem current : this.items) {
            if (current.getId() == item.getId()) {
                return current;
            }
        }
        return null;
    }

    public List<TodoItem> getItems() {
        return items;
    }

    @Override
    public TodoItem getSelectedItem(int itemPosition, long itemId) {
        return adapter.getItem(itemPosition);
    }

    @Override
    public void close() {
        mHelper.close();
    }
}
