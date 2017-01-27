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
import android.widget.Toast;

import com.deves.maus.R;

import java.util.List;

import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;


public class TodoItemListViews {
    private static int count;

    public static  ArrayAdapter<TodoItem> createTodoItemArrayAdapter(final Activity aContext, final List<TodoItem> aItems) {
        return new ArrayAdapter<TodoItem>(aContext, R.layout.todo_item, aItems) {

            @Override
            public View getView(final int position, View listItemView, ViewGroup parent) {
                View layout = listItemView == null ? aContext.getLayoutInflater().inflate(R.layout.todo_item, parent, false) : listItemView;

                final TextView itemView = (TextView) layout.findViewById(R.id.todo_item_title);
                itemView.setText(getItem(position).getTitle());

                itemView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View aClickedView) {
                        Toast.makeText(getContext(), itemView.getText(), Toast.LENGTH_LONG).show();

                    }
                });
                final ImageView imageView = (ImageView) layout.findViewById(R.id.todo_item_icon);
                imageView.setImageResource(R.drawable.star_grey);

                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (count % 2 == 0){
                            imageView.setImageResource(R.drawable.star_yellow);
                        } else
                            imageView.setImageResource(R.drawable.star_grey);
                        count++;
                    }
                });

                final CheckBox checkBox = (CheckBox)layout.findViewById(R.id.todo_item_checkbox);
                TodoItem item = getItem(position);
                if (item.isDone()){
                    checkBox.setChecked(true);
                    Log.e("MY_TAG", "onClick: checkbox is clicked" );
                    itemView.setPaintFlags(itemView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                checkBox.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()){
                            Log.e("MY_TAG", "onClick: checkbox is clicked" );
                            itemView.setPaintFlags(itemView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            Log.e("MY_TAG", "onClick: checkbox is NOT clicked");
                            itemView.setPaintFlags(itemView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        }
                    }
                });
                return layout;
            }

        };
    }

    //TODO add ViewHolder pattern

}
