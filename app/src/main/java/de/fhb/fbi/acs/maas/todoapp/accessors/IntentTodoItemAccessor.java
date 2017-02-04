package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.content.Intent;

import de.fhb.fbi.acs.maas.todoapp.TodoActivity;
import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;

/**
 * @author novruzov
 * Intent implementation of TodoItemAccessor
 */
public class IntentTodoItemAccessor extends AbstractActivityDataAccessor implements TodoItemAccessor {

    private TodoItem item;

    /**
     * @see TodoItemAccessor documentation
     * @return
     */
    @Override
    public TodoItem readItem() {
        if (this.item == null) {
            this.item = (TodoItem) getActivity().getIntent().getSerializableExtra(TodoActivity.ARG_ITEM_OBJECT);
        }
        return this.item;
    }

    /**
     * @see TodoItemAccessor documentation
     */
    @Override
    public void writeItem() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TodoActivity.ARG_ITEM_OBJECT, this.item);
        getActivity().setResult(TodoActivity.RESPONSE_ITEM_EDITED, returnIntent);
    }

    /**
     * @see TodoItemAccessor documentation
     * @return true if todoItem was found
     */
    public boolean hasItem() {
        return readItem() != null;
    }

    /**
     * @see TodoItemAccessor documentation
     */
    public void createItem() {
        this.item = new TodoItem(-1,"", "", false,false, 0,0);
    }

    /**
     *  @see TodoItemAccessor documentation
     */
    @Override
    public void deleteItem() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TodoActivity.ARG_ITEM_OBJECT, this.item);
        getActivity().setResult(TodoActivity.RESPONSE_ITEM_DELETED, returnIntent);
    }

}