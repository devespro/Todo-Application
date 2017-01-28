package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.content.Intent;

import de.fhb.fbi.acs.maas.todoapp.TodoActivity;
import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;

/**
 * Created by deves on 28/01/17.
 */
public class IntentTodoItemAccessor extends AbstractActivityDataAccessor
        implements DataItemAccessor {

    private TodoItem item;

    @Override
    public TodoItem readItem() {
        if (this.item == null) {
            this.item = (TodoItem) getActivity().getIntent().getSerializableExtra(TodoActivity.ARG_ITEM_OBJECT);
        }

        return this.item;
    }

    @Override
    public void writeItem() {
        // and return to the calling activity
        Intent returnIntent = new Intent();

        // set the item
        returnIntent.putExtra(TodoActivity.ARG_ITEM_OBJECT, this.item);

        // set the result code
        getActivity().setResult(TodoActivity.RESPONSE_ITEM_EDITED, returnIntent);
    }

    public boolean hasItem() {
        return readItem() != null;
    }

    public void createItem() {
        this.item = new TodoItem(-1,"", "", false,false, 0,0);
    }

    @Override
    public void deleteItem() {
        // and return to the calling activity
        Intent returnIntent = new Intent();

        // set the item
        returnIntent.putExtra(TodoActivity.ARG_ITEM_OBJECT, this.item);

        // set the result code
        getActivity().setResult(TodoActivity.RESPONSE_ITEM_DELETED, returnIntent);
    }

}