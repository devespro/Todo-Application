package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.fhb.fbi.acs.maas.todoapp.LoginActivity;
import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;
import de.fhb.fbi.acs.maas.todoapp.model.TodoItemCRUDAccessor;

/**
 * @author novruzov
 */
public class RemoteTodoItemListAccessor extends AbstractActivityDataAccessor implements TodoItemListAccessor {
    private List<TodoItem> todos = new ArrayList<>();
    private ArrayAdapter<TodoItem> adapter;
    private TodoItemCRUDAccessor accessor = new HttpURLConnectionTodoItemCRUDAccessor(LoginActivity.getRestBaseUrl() + "/todoitems");

    public RemoteTodoItemListAccessor(){
    }
    @Override
    public TodoItem addItem(TodoItem item) {
       return accessor.createItem(item);
    }

    @Override
    public ArrayAdapter getAdapter(List<TodoItem> todos) {

        this.adapter = TodoItemListViews.createTodoItemArrayAdapter(getActivity(),todos);
        Collections.sort(todos);
        this.adapter.setNotifyOnChange(true);
        return this.adapter;
    }

    @Override
    public TodoItem updateItem(TodoItem item) {
        accessor.updateItem(item);
        return item;
    }

    @Override
    public boolean deleteItem(TodoItem item) {
        return accessor.deleteItem(item.getId());
    }

    @Override
    public TodoItem getSelectedItem(int itemPosition) {
        return adapter.getItem(itemPosition);
    }

    @Override
    public void close() {

    }

    @Override
    public void setItems(List<TodoItem> items) {
        this.todos = items;
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public List<TodoItem> getAll() {
        todos = accessor.readAllItems();
        Collections.sort(todos);
        return todos;
    }
}
