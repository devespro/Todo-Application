package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.database.Cursor;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;

/**
 * @author novruzov
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
    public TodoItem addItem(TodoItem item) {
        mHelper.insert(item);
        this.adapter.add(item);
        Collections.sort(getItems());
        return item;
    }

    @Override
    public ArrayAdapter getAdapter(List<TodoItem> newItems) {
        mHelper = new SQLiteDBHelper(getActivity());
        mHelper.prepareSQLiteDatabase();

        newItems = readOutItemsFromDatabase();

        Collections.sort(newItems);
        this.adapter = TodoItemListViews.createTodoItemArrayAdapter(getActivity(),newItems);
        this.adapter.setNotifyOnChange(true);

        return this.adapter;
    }

    private List<TodoItem> readOutItemsFromDatabase(){
        Cursor cursor = mHelper.getCursor();

        Log.i(LOG_TAG, "getAdapter(): got a cursor: " + cursor);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // create a new item and add it to the list
            this.items.add(mHelper.createItemFromCursor(cursor));
            cursor.moveToNext();
        }

        Log.i(LOG_TAG, "readOutItemsFromDatabase(): read out items: " + this.items);
        return items;
    }

    @Override
    public TodoItem updateItem(TodoItem item) {
        mHelper.update(item);
        Log.i(LOG_TAG, "updateItem: updating  " + item);
        lookupItem(item).updateFrom(item);
        this.adapter.notifyDataSetChanged();
        return item;

    }

    @Override
    public boolean deleteItem(TodoItem item) {
        mHelper.delete(item);
        this.adapter.remove(lookupItem(item));
        return true;
    }

    /*
     * get the item from the list, checking identity of ids (as the argument
     * value may have been serialised/deserialized we cannot check for identity
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

    public void setItems(List<TodoItem> items, boolean comparisonMode) {
        Log.e(LOG_TAG, "setItems: " + "INSIDE_SET_ITEMS");
        this.items = items;
        if (comparisonMode) {
            addItemsIntoDB(items);
        }
        adapter.notifyDataSetChanged();
    }

    private void addItemsIntoDB(List<TodoItem> items){
        mHelper = new SQLiteDBHelper(getActivity());
        mHelper.prepareSQLiteDatabase();
        for (TodoItem item : items){
            mHelper.insert(item);
        }
    }

    @Override
    public TodoItem getSelectedItem(int itemPosition) {
        return adapter.getItem(itemPosition);
    }

    @Override
    public void close() {
        mHelper.close();
    }

    @Override
    public List<TodoItem> getAll() {
        return getItems();
    }
}
