package de.fhb.fbi.acs.maas.todoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.deves.maus.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.fhb.fbi.acs.maas.todoapp.accessors.AbstractActivityDataAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.TodoItemAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.IntentTodoItemAccessor;
import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;
import de.fhb.fbi.acs.maas.todoapp.utility.TodoUtility;

/**
 * @author novruzov
 */
public class ItemDetailsActivity extends Activity {

    private static final String LOG_TAG = ItemDetailsActivity.class.getSimpleName();

    /**
     * the accessor for dealing with the item to be displayed and edited
     */
    private TodoItemAccessor accessor;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_detailview);
        setTitle(R.string.detail_view_title);

        this.accessor = new IntentTodoItemAccessor();
        if (accessor instanceof AbstractActivityDataAccessor) {
            ((AbstractActivityDataAccessor) accessor).setActivity(this);
        }

        final ViewHolder viewHolder = new ViewHolder(this);

        TodoItem todoItem = accessor.readItem();

        if (todoItem != null) {
            if (todoItem.getDate() != 0) {
                viewHolder.dateAsText.setText(TodoUtility.formatDate(todoItem.getDate()));
            }

            if (todoItem.getTime() != 0){
                viewHolder.timeAsText.setText(TodoUtility.formatTime(todoItem.getTime()));
            }
        }

        Calendar calendar = todoItem == null ? null : TodoUtility.getCalendarFromLong(todoItem.getDate());
        if (calendar != null) {

            viewHolder.itemDate.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                    viewHolder.dateAsText.setText(TodoUtility.formatDate(calendar.getTimeInMillis()));
                }
            });

            viewHolder.itemTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    viewHolder.timeAsText.setText(String.format("%02d:%02d", hourOfDay, minute));
                }
            });
        }

        // if we do not have an item, we assume we need to create a new one
        if (accessor.hasItem()) {
            TodoItem item = accessor.readItem();
            viewHolder.itemTitle.setText(item.getTitle());
            viewHolder.itemDescription.setText(item.getDescription());
            viewHolder.itemIsFavourite.setChecked(item.isFavouriteStatus());
            viewHolder.itemIsDone.setChecked(item.isDoneStatus());
            viewHolder.timeAsText.setText(TodoUtility.formatTime(item.getTime()));

            if (item.getTime() > 0) {
                viewHolder.itemTime.setCurrentHour(new Date(item.getTime()).getHours());
                viewHolder.itemTime.setCurrentMinute(new Date(item.getTime()).getMinutes());
            }
        } else {
            accessor.createItem();
        }

        viewHolder.saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "onClick: saving the item...");
                processItemSave(accessor, viewHolder);
            }
        });

        viewHolder.deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ItemDetailsActivity.this);
                alertBuilder.setTitle("Delete current item?");
                alertBuilder.setMessage("This operation cannot be undone. Would you like to proceed?");
                alertBuilder.setCancelable(true);

                alertBuilder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(LOG_TAG, "onClick: deleting the item with id " + id);
                                processItemDelete(accessor);
                            }
                        });

                alertBuilder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alertBuilder.setIcon(android.R.drawable.ic_dialog_alert);

                AlertDialog alert11 = alertBuilder.create();
                alert11.show();
            }
        });

    }

    private void processItemSave(TodoItemAccessor accessor, ViewHolder viewHolder){
        TodoItem item = accessor.readItem();
        item.setTitle(viewHolder.itemTitle.getText().toString());
        item.setDescription(viewHolder.itemDescription.getText().toString());
        item.setDoneStatus(viewHolder.itemIsDone.isChecked());
        item.setFavouriteStatus(viewHolder.itemIsFavourite.isChecked());

        if (isDateChanged(viewHolder)) {
            GregorianCalendar calendar = new GregorianCalendar(viewHolder.itemDate.getYear(), viewHolder.itemDate.getMonth(), viewHolder.itemDate.getDayOfMonth());
            item.setDate(calendar.getTimeInMillis());
        }
        if (isTimeChanged(viewHolder)) {
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(Calendar.HOUR_OF_DAY, viewHolder.itemTime.getCurrentHour());
            mCalendar.set(Calendar.MINUTE, viewHolder.itemTime.getCurrentMinute());
            item.setTime(mCalendar.getTimeInMillis());
        }
        accessor.writeItem();
        finish();
    }

    private boolean isTimeChanged(ViewHolder viewHolder) {
        Calendar widgetTime = Calendar.getInstance();
        widgetTime.set(Calendar.HOUR_OF_DAY, viewHolder.itemTime.getCurrentHour());
        widgetTime.set(Calendar.MINUTE, viewHolder.itemTime.getCurrentMinute());
        int widgetHour = widgetTime.getTime().getHours();
        int widgetMinute = widgetTime.getTime().getMinutes();

        Calendar currentDate = Calendar.getInstance();
        int currentHour = currentDate.getTime().getHours();
        int currentMinute = currentDate.getTime().getMinutes();

        return (widgetHour != currentHour || widgetMinute != currentMinute);
    }

    private boolean isDateChanged(ViewHolder viewHolder){
        Calendar widgetDate = new GregorianCalendar(viewHolder.itemDate.getYear(), viewHolder.itemDate.getMonth(),
                viewHolder.itemDate.getDayOfMonth());
        Calendar currentDate = Calendar.getInstance();
        int widgetFields = widgetDate.get(Calendar.YEAR) + widgetDate.get(Calendar.MONTH) + widgetDate.get(Calendar.DAY_OF_MONTH);
        int currentFields = currentDate.get(Calendar.YEAR) + currentDate.get(Calendar.MONTH) + currentDate.get(Calendar.DAY_OF_MONTH);
        boolean result = widgetFields != currentFields;
        return result;
    }

    /**
     * delete the item and finish
     *
     * @param accessor
     */
    protected void processItemDelete(TodoItemAccessor accessor) {
        // delete the item
        accessor.deleteItem();

        // and finish
        finish();
    }

    /**
     * UI elements of the item's detail view.
     * View holder pattern
     */
    public static class ViewHolder {
        public final EditText itemTitle;
        public final EditText itemDescription;
        public final CheckBox itemIsFavourite;
        public final CheckBox itemIsDone;
        public final DatePicker itemDate;
        public final TimePicker itemTime;
        public final TextView dateAsText;
        public final TextView timeAsText;
        public final Button saveButton;
        public final Button deleteButton;

        public ViewHolder(Activity activity){
            itemTitle = (EditText) activity.findViewById(R.id.item_title);
            itemDescription = (EditText) activity.findViewById(R.id.item_description);
            itemIsFavourite = (CheckBox) activity.findViewById(R.id.item_is_favourite_checkbox);
            itemIsDone = (CheckBox) activity.findViewById(R.id.item_is_done_checkbox);
            itemDate = (DatePicker) activity.findViewById(R.id.item_date_picker);
            itemTime = (TimePicker) activity.findViewById(R.id.item_time_picker);
            dateAsText = (TextView) activity.findViewById(R.id.date_as_text);
            timeAsText = (TextView) activity.findViewById(R.id.time_as_text);
            saveButton = (Button) activity.findViewById(R.id.saveButton);
            deleteButton = (Button) activity.findViewById(R.id.deleteButton);
        }
    }
}