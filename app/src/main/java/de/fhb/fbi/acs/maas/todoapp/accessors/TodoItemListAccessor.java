package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.widget.ListAdapter;

import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;


/**
 * Interface for dealing with a list of data items, used by ItemListActivity
 *
 * @author Joern Kreutel
 *
 */
public interface TodoItemListAccessor {

    /**
     * add an item to the list
     *
     * @param item
     */
    void addItem(TodoItem item);

    /**
     * get an adapter for the list
     * @return
     */
    ListAdapter getAdapter();

    /**
     * update an existing item
     *
     * @param item
     */
    void updateItem(TodoItem item);

    /**
     * delete an item
     *
     * @param item
     */
    void deleteItem(TodoItem item);

    /**
     * determine the item selected by the user given either the position in the
     * list or the item id
     *
     * @param itemPosition
     * @param itemId
     * @return
     */
    TodoItem getSelectedItem(int itemPosition, long itemId);

    /**
     * end processing the list of items
     */
    void close();

}
