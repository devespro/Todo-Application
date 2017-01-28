package de.fhb.fbi.acs.maas.todoapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deves.maus.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.fhb.fbi.acs.maas.todoapp.accessors.AbstractActivityDataAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.DataItemAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.IntentTodoItemAccessor;
import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;
import de.fhb.fbi.acs.maas.todoapp.utility.TodoUtility;

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
            this.accessor = new IntentTodoItemAccessor();
            // if we have an ActivityDataAccessor, we pass ourselves
            if (accessor instanceof AbstractActivityDataAccessor) {
                ((AbstractActivityDataAccessor) accessor).setActivity(this);
            }
            // obtain the ui elements
            final EditText itemTitle = (EditText) findViewById(R.id.item_title);
            final EditText itemDescription = (EditText) findViewById(R.id.item_description);
            final CheckBox itemIsFavourite = (CheckBox) findViewById(R.id.item_is_favourite_checkbox);
            final CheckBox itemIsDone = (CheckBox) findViewById(R.id.item_is_done_checkbox);
            final DatePicker itemDate = (DatePicker) findViewById(R.id.item_date_picker);
            final TextView dateAsText = (TextView) findViewById(R.id.date_as_text);
            Button saveButton = (Button) findViewById(R.id.saveButton);
            Button deleteButton = (Button) findViewById(R.id.deleteButton);
            TodoItem todoItem = accessor.readItem();

            if (todoItem != null && todoItem.getDate() != 0) {
                dateAsText.setText(TodoUtility.formatDate(todoItem.getDate()));
            }
            Calendar calendar = todoItem == null ? Calendar.getInstance() : TodoUtility.getCalendarFromLong(todoItem.getDate());

            itemDate.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                    GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);

                    //dateAsText.setText(calendar.toString());
                    Toast.makeText(ItemDetailsActivity.this, "Selected date: " + calendar.toString(), Toast.LENGTH_SHORT);
                    dateAsText.setText(TodoUtility.formatDate(calendar.getTimeInMillis()));
                }
            });

            setTitle("Detail View");

            // if we do not have an item, we assume we need to create a new one
            if (accessor.hasItem()) {
                // set name and description
                TodoItem item = accessor.readItem();
                Log.e(LOG_TAG, "onCreate:DETAIL_ACTIVITY CHECKBOX _>  " + item.isFavourite());
                itemTitle.setText(item.getTitle());
                itemDescription.setText(item.getDescription());
                itemIsFavourite.setChecked(item.isFavourite());
                itemIsDone.setChecked(item.isDone());
            } else {
                accessor.createItem();
            }


            // we only set a listener on the ok button that will collect the
            // edited fields and set the values on the item
            saveButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(LOG_TAG, "setOnClickListener the checkbox status is  " + itemIsFavourite.isChecked() );
                    processItemSave(accessor, itemTitle, itemDescription, itemIsFavourite, itemIsDone, itemDate);
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
     * @param isFavourite
     */
    protected void processItemSave(DataItemAccessor accessor, EditText title,
                                   EditText description, CheckBox isFavourite, CheckBox isDone, DatePicker itemDate) {
        TodoItem item = accessor.readItem();
        // re-set the fields of the item
        Log.i(LOG_TAG, "processItemSave(): ITEM_ID" + accessor.readItem().getId());
        item.setTitle(title.getText().toString());
        item.setDescription(description.getText().toString());
        item.setIsFavourite(isFavourite.isChecked());
        item.setIsDone(isDone.isChecked());

        GregorianCalendar calendar = new GregorianCalendar(itemDate.getYear(), itemDate.getMonth(), itemDate.getDayOfMonth());
        item.setDate(calendar.getTimeInMillis());
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