package de.fhb.fbi.acs.maas.todoapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.deves.maus.R;

import de.fhb.fbi.acs.maas.todoapp.accessors.AbstractActivityDataAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.DataItemAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.IntentTodoItemAccessor;

/**
 * Created by Esien Novruzov on 28/01/17.
 */
public class ItemDetailsActivity extends Activity {

    /**
     * the logger
     */
    protected static final String LOG_TAG = ItemDetailsActivity.class.getSimpleName();

    /**
     * the accessor for dealing with the item to be displayed and edited
     */
    private DataItemAccessor accessor;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_detailview);

        try {
            // obtain the ui elements
            final EditText itemTitle = (EditText) findViewById(R.id.item_title);
            final EditText itemDescription = (EditText) findViewById(R.id.item_description);
            Button saveButton = (Button) findViewById(R.id.saveButton);
            Button deleteButton = (Button) findViewById(R.id.deleteButton);


            this.accessor = new IntentTodoItemAccessor();
            // if we have an ActivityDataAccessor, we pass ourselves
            if (accessor instanceof AbstractActivityDataAccessor) {
                ((AbstractActivityDataAccessor) accessor).setActivity(this);
            }

            setTitle("Detail View");

            // if we do not have an item, we assume we need to create a new one
            if (accessor.hasItem()) {
                // set name and description
                itemTitle.setText(accessor.readItem().getTitle());
                itemDescription.setText(accessor.readItem().getDescription());
            } else {
                accessor.createItem();
            }

            // we only set a listener on the ok button that will collect the
            // edited fields and set the values on the item
            saveButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemSave(accessor, itemTitle, itemDescription);
                }
            });

            // we also set a listener on the delete button
            // we only set a listener on the ok button that will collect the
            // edited fields and set the values on the item
            deleteButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemDelete(accessor);
                }
            });


        } catch (Exception e) {
            String err = "got exception: " + e;
            Log.e(LOG_TAG, err, e);
        }

    }

    /**
     * save the item and finish
     * @param accessor
     * @param title
     * @param description
     */
    protected void processItemSave(DataItemAccessor accessor, EditText title,
                                   EditText description) {
        // re-set the fields of the item
        accessor.readItem().setTitle(title.getText().toString());
        accessor.readItem().setDescription(description.getText().toString());

        // save the item
        accessor.writeItem();

        // and finish
        finish();
    }

    /**
     * delete the item and finish
     * @param accessor
     */
    protected void processItemDelete(DataItemAccessor accessor) {
        // delete the item
        accessor.deleteItem();

        // and finish
        finish();
    }

}