package de.fhb.fbi.acs.maas.todoapp.accessors;

import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;

/**
 * @author novruzov
 */
public interface TodoItemAccessor {

    /**
     *
     * @return a current todoItem
     */
    TodoItem readItem();

    /**
     * writes a current todoItem
     */
    void writeItem();

    /**
     *
     * @return true if a todoItem was found
     */
    boolean hasItem();

    /**
     * creates the todoItem entity
     */
    void createItem();

    /**
     * deletes a current todoItem
     */
    void deleteItem();
}
