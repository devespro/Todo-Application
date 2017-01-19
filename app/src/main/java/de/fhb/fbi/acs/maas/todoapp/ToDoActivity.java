package de.fhb.fbi.acs.maas.todoapp;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.deves.maus.R;

import de.fhb.fbi.acs.maas.todoapp.accessors.AbstractActivityDataAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.SimpleTodoitemListAccessor;
import de.fhb.fbi.acs.maas.todoapp.accessors.TodoItemListAccessor;
import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;

/**
 * @author Esien Novruzov
 */
public class TodoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().add(R.id.todo_layout, new ToDoFragment()).commit();
        }
    }

    public static class ToDoFragment extends ListFragment {
        /**
         * the data accessor for the data items
         */
        private TodoItemListAccessor accessor;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);

            accessor = new SimpleTodoitemListAccessor();
            if (accessor instanceof AbstractActivityDataAccessor){
                ((AbstractActivityDataAccessor) accessor).setActivity(getActivity());
            }

            final ArrayAdapter<TodoItem> adapter = (ArrayAdapter)accessor.getAdapter();
            setListAdapter(adapter);
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
        }
    }
}
