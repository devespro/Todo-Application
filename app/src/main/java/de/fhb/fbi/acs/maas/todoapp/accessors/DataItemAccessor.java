package de.fhb.fbi.acs.maas.todoapp.accessors;

import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;

/**
 * Created by deves on 28/01/17.
 */
public interface DataItemAccessor {

    TodoItem readItem();

    void writeItem();

    boolean hasItem();

    void createItem();

    void deleteItem();
}
