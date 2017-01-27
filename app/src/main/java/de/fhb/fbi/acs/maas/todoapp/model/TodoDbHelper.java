package de.fhb.fbi.acs.maas.todoapp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Esien Novruzov
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "todolist.db";

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TODO_LIST_TABLE = "CREATE TABLE " + TodoContract.TodoEntry.TABLE_NAME +
                " (" + TodoContract.TodoEntry._ID + " INTEGER PRIMARY KEY," +
                TodoContract.TodoEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                TodoContract.TodoEntry.COLUMN_DESCRIPTION + "TEXT ," +
                TodoContract.TodoEntry.COLUMN_ISDONE + " BOOLEAN NOT NULL," +
                TodoContract.TodoEntry.COLUMN_ISFAVOURITE + " BOOLEAN NOT NULL," +
                TodoContract.TodoEntry.COLUMN_DATE + " INTEGER , " +
                TodoContract.TodoEntry.COLUMN_TIME +  " INTEGER" +
                 " );";
        db.execSQL(SQL_CREATE_TODO_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TodoContract.TodoEntry.TABLE_NAME);
        onCreate(db);
    }
}
