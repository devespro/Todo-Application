package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;

/**
 * Created by deves on 19/01/17.
 */
public class SimpleTodoitemListAccessor extends AbstractActivityDataAccessor implements TodoItemListAccessor {

    /**
     * the list of items
     */
    List<TodoItem> items = new ArrayList<TodoItem>();

    /**
     * the adapter operating on the list
     */
    private ArrayAdapter<TodoItem> adapter;


    public SimpleTodoitemListAccessor(){
        TodoItem newItem = new TodoItem(1,"some title", "some desc", false, false, 123123, 123123);
        TodoItem newItem2 = new TodoItem(2,"some title2", "some desc2", true, false, 123123, 123123);
        items.add(newItem);
        items.add(newItem2);
    }

    @Override
    public void addItem(TodoItem item) {

    }

    @Override
    public ListAdapter getAdapter() {
        //adapter = TodoItemListViews.createTodoItemArrayAdapter(getActivity(), items);
        String[] SAMPLE_DATA = new String[]{"first_test","second_test","third_test","forth_test"};

        List<String> data = new ArrayList<>(Arrays.asList(SAMPLE_DATA));
        Log.e("AAAAAAAAA", "getAdapter: getActivity -> " + getActivity() );
        adapter = TodoItemListViews.createTodoItemArrayAdapter(getActivity(), items);
        return adapter;
    }

    @Override
    public void updateItem(TodoItem item) {

    }

    @Override
    public void deleteItem(TodoItem item) {

    }

    @Override
    public TodoItem getSelectedItem(int itemPosition) {
        return null;
    }

    @Override
    public void close() {

    }
}
