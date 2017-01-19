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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by deves on 31/10/16.
 */
public class ToToActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);


        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().add(R.id.todo_layout, new ToDoFragment()).commit();
        }
    }

    public static class ToDoFragment extends ListFragment {
        private ArrayAdapter<String> arrayAdapter;

        private String[] SAMPLE_DATA = new String[]{"first","second","third","forth"};

        private List<String> data = new ArrayList<>(Arrays.asList(SAMPLE_DATA));

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);

            arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, data);
            setListAdapter(arrayAdapter);

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
