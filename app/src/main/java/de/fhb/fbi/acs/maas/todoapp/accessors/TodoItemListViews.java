package de.fhb.fbi.acs.maas.todoapp.accessors;

/**
 * Created by Esien Novruzov on 19/01/17.
 */

import android.app.Activity;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.deves.maus.R;

import java.util.List;

import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;
import de.fhb.fbi.acs.maas.todoapp.utility.TodoUtility;


public class TodoItemListViews {
    private static int count;
    private static final int MAX_TITLE_LENGTH = 12;
    private static final String LOG_TAG = TodoItemListViews.class.getSimpleName();

    public static  ArrayAdapter<TodoItem> createTodoItemArrayAdapter(final Activity aContext, final List<TodoItem> aItems) {
        return new ArrayAdapter<TodoItem>(aContext, R.layout.todo_item_in_listview, aItems) {

            @Override
            public View getView(final int position, View listItemView, ViewGroup parent) {

                View layout = listItemView == null ? aContext.getLayoutInflater().inflate(R.layout.todo_item_in_listview, parent, false) : listItemView;
                final TextView itemTitle = (TextView) layout.findViewById(R.id.todo_item_title);
                final TodoItem item = getItem(position);
                String title = getItem(position).getTitle();
                title = checkTitle(title);
                itemTitle.setText(title);
                final ImageView imageView = (ImageView) layout.findViewById(R.id.todo_item_icon);
                final TextView itemDate = (TextView) layout.findViewById(R.id.todo_item_date);
                itemDate.setText(TodoUtility.getStringDateFromLong(item.getDate()));

                if (!item.isFavourite()) {
                    imageView.setImageResource(R.drawable.star_grey);
                } else {
                    imageView.setImageResource(R.drawable.star_yellow);
                }

                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (count % 2 == 0){
                            imageView.setImageResource(R.drawable.star_yellow);
                            item.setIsFavourite(true);
                            Log.e(LOG_TAG, "onClick: image " + item);
                        } else {
                            imageView.setImageResource(R.drawable.star_grey);
                            Log.e(LOG_TAG, "onClick: image " + item);
                            item.setIsFavourite(false);
                        }
                        count++;
                    }
                });

                final CheckBox checkBox = (CheckBox)layout.findViewById(R.id.todo_item_checkbox);
                if (item.isDone()){
                    checkBox.setChecked(true);
                    Log.e("MY_TAG", "onClick: checkbox is clicked" );
                    itemTitle.setPaintFlags(itemTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    itemTitle.setPaintFlags(itemTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    checkBox.setChecked(false);
                }
                checkBox.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()){
                            Log.e("MY_TAG", "onClick: checkbox is clicked" );
                            itemTitle.setPaintFlags(itemTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            Log.e("MY_TAG", "onClick: checkbox is NOT clicked");
                            itemTitle.setPaintFlags(itemTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
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

    //TODO add ViewHolder pattern

}
