package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.widget.ArrayAdapter;

import java.util.List;

import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;


/**
 * Interface for dealing with a list of data items, used by ItemListActivity
 *
 * @author Joern Kreutel
 *
 */
public interface TodoItemListAccessor {

    List<TodoItem> getAll();

    /**
     * add an item to the list
     *
     * @param item
     */
    TodoItem addItem(TodoItem item);

    /**
     * get an adapter for the list
     * @return
     */
    ArrayAdapter getAdapter(List<TodoItem> todoItemList);

    /**
     * update an existing item
     *
     * @param item
     */
    TodoItem updateItem(TodoItem item);

    /**
     * delete an item
     *
     * @param item
     */
    boolean deleteItem(TodoItem item);

    /**
     * determine the item selected by the user given either the position in the
     * list
     *
     * @param itemPosition
     * @return
     */
    TodoItem getSelectedItem(int itemPosition);

    /**
     * end processing the list of items
     */
    void close();

    void setItems(List<TodoItem> items, boolean comparisonMode);

}
