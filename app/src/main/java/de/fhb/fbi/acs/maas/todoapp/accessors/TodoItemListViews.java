package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.deves.maus.R;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.fhb.fbi.acs.maas.todoapp.LoginActivity;
import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;
import de.fhb.fbi.acs.maas.todoapp.utility.TodoUtility;

/**
 * @author novruzov
 */
public class TodoItemListViews {
    private static final int MAX_TITLE_LENGTH = 12;
    private static final long MILLISECONDS_IN_ONE_DAY = 86400000L;
    private static final String LOG_TAG = TodoItemListViews.class.getSimpleName();
    private static TodoItemListAccessor accessor;


    public static  ArrayAdapter<TodoItem> createTodoItemArrayAdapter(final Activity aContext, final List<TodoItem> aItems) {
        String connectionMode = aContext.getIntent().getExtras().getString(LoginActivity.ACCESSOR_CLASS);
        Log.e(LOG_TAG, "createTodoItemArrayAdapter: STATUS _> " + connectionMode);
        if (connectionMode.equals("remote")){
            accessor = new RemoteTodoItemListAccessor();
        }else {
            accessor = new SQLiteTodoItemListAccessor();
        }
        return new ArrayAdapter<TodoItem>(aContext, R.layout.todo_item_in_listview, aItems) {

            @Override
            public View getView(final int position, View listItemView, ViewGroup parent) {
               final  SQLiteDBHelper mHelper = new SQLiteDBHelper(aContext);


                final View layout = listItemView == null ? aContext.getLayoutInflater().inflate(R.layout.todo_item_in_listview, parent, false) : listItemView;
                final TextView itemTitle = (TextView) layout.findViewById(R.id.todo_item_title);
                final TodoItem item = getItem(position);
                String title = getItem(position).getTitle();
                title = checkTitle(title);
                itemTitle.setText(title);
                final ImageView imageView = (ImageView) layout.findViewById(R.id.todo_item_icon);
                final TextView itemDate = (TextView) layout.findViewById(R.id.todo_item_date);
                final TextView itemTimeTextView = (TextView) layout.findViewById(R.id.todo_item_time);
                itemDate.setText(TodoUtility.getStringDateFromLong(item.getDate()));
                itemTimeTextView.setText(TodoUtility.formatTime(item.getTime()));

                if (!item.isFavouriteStatus()) {
                    imageView.setImageResource(android.R.drawable.star_off);
                } else {
                    imageView.setImageResource(R.drawable.star_on);
                }
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!item.isFavouriteStatus()){
                            imageView.setImageResource(R.drawable.star_yellow);
                            item.setFavouriteStatus(true);
                            Log.e(LOG_TAG, "setOnClickListener: " + "UPDATING ITEM! " + item);
                            Log.e(LOG_TAG, "accessor: " + accessor);
                            if (accessor instanceof RemoteTodoItemListAccessor){
                                Log.e(LOG_TAG, "processRemoteItemUpdate: " + item);
                                processRemoteItemUpdate(aItems, item);
                                notifyDataSetChanged();
                                return;
                            }
                            mHelper.update(item);
                            notifyDataSetChanged();
                        } else {
                            imageView.setImageResource(R.drawable.star_grey);
                            item.setFavouriteStatus(false);
                            if (accessor instanceof RemoteTodoItemListAccessor) {
                                Log.e(LOG_TAG, "processRemoteItemUpdate: " + item);
                                processRemoteItemUpdate(aItems, item);
                                notifyDataSetChanged();
                                return;
                            }
                            mHelper.update(item);
                            notifyDataSetChanged();
                        }
                    }
                });

                final CheckBox checkBox = (CheckBox)layout.findViewById(R.id.todo_item_checkbox);
                if (item.isDoneStatus()){
                    checkBox.setChecked(true);
                    itemTitle.setTextColor(Color.GRAY);
                    itemDate.setTextColor(Color.GRAY);
                    itemTimeTextView.setTextColor(Color.GRAY);
                    itemTitle.setPaintFlags(itemTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    itemTitle.setTextColor(Color.WHITE);
                    itemDate.setTextColor(Color.WHITE);
                    itemTimeTextView.setTextColor(Color.WHITE);
                    itemTitle.setPaintFlags(itemTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    checkBox.setChecked(false);
                }

                if (item.getDate() != 0 && item.getDate() + MILLISECONDS_IN_ONE_DAY < new Date().getTime()){
                    itemDate.setTextColor(Color.RED);
                }
                checkBox.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()) {
                            item.setDoneStatus(true);
                            if (accessor instanceof RemoteTodoItemListAccessor) {
                                Log.e(LOG_TAG, "processRemoteItemUpdate: " + item);
                                processRemoteItemUpdate(aItems, item);
                                Collections.sort(aItems);
                                notifyDataSetChanged();
                                return;
                            }
                            mHelper.update(item);
                            Collections.sort(aItems);
                            notifyDataSetChanged();
                        } else {
                            item.setDoneStatus(false);

                            if (accessor instanceof RemoteTodoItemListAccessor) {
                                Log.e(LOG_TAG, "processRemoteItemUpdate: " + item);
                                processRemoteItemUpdate(aItems, item);
                                Collections.sort(aItems);
                                notifyDataSetChanged();
                                return;
                            }
                            mHelper.update(item);
                            Collections.sort(aItems);
                            notifyDataSetChanged();
                        }
                    }
                });
                return layout;
            }
        };
    }

    private static String checkTitle(String title){
        if (title.length() > MAX_TITLE_LENGTH){
            return title.substring(0, MAX_TITLE_LENGTH) + "..";
        } else
            return title;
    }

    private static void processRemoteItemUpdate(final List<TodoItem> items, TodoItem item){
        new AsyncTask<TodoItem, Void, TodoItem>() {
            @Override
            protected TodoItem doInBackground(TodoItem... items) {
                return accessor.updateItem(items[0]);
            }

            @Override
            protected void onPostExecute(TodoItem item) {
                if (item != null) {
                    // read out the item from the list and update it
                    items.get(items.indexOf(item)).updateFrom(item);
                    // notify the adapter that the item has been changed
                }
            }
        }.execute(item);

    }
}
