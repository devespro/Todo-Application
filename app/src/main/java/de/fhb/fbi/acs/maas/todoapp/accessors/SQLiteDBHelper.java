package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.Locale;

import de.fhb.fbi.acs.maas.todoapp.model.TodoItem;

/**
 * @author Esien Novruzov
 * //TODO extends SQLiteOpenHelper
 */
public class SQLiteDBHelper{

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    private final Activity mActivity;

    public static final String DATABASE_NAME = "todolist.db";

    /**
     * the creation table query
     */
    private static final String SQL_CREATE_TODO_LIST_TABLE = "CREATE TABLE " + TodoContract.TodoEntry.TABLE_NAME +
            " (" + TodoContract.TodoEntry._ID + " INTEGER PRIMARY KEY," +
            TodoContract.TodoEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
            TodoContract.TodoEntry.COLUMN_DESCRIPTION + " TEXT ," +
            TodoContract.TodoEntry.COLUMN_ISDONE + " BOOLEAN NOT NULL," +
            TodoContract.TodoEntry.COLUMN_ISFAVOURITE + " BOOLEAN NOT NULL," +
            TodoContract.TodoEntry.COLUMN_DATE + " INTEGER , " +
            TodoContract.TodoEntry.COLUMN_TIME +  " INTEGER " +
            " );";

    /**
     * the where clause for item deletion
     */
    private static final String WHERE_IDENTIFY_ITEM = TodoContract.TodoEntry._ID + "=?";

    private static final String[] COLUMNS_TO_RETURN = {
            TodoContract.TodoEntry._ID,
            TodoContract.TodoEntry.COLUMN_TITLE,
            TodoContract.TodoEntry.COLUMN_DESCRIPTION,
            TodoContract.TodoEntry.COLUMN_ISDONE,
            TodoContract.TodoEntry.COLUMN_ISFAVOURITE,
            TodoContract.TodoEntry.COLUMN_DATE,
            TodoContract.TodoEntry.COLUMN_TIME,
    };

    private static final String ORDERING = TodoContract.TodoEntry._ID + " ASC";

    private static final String LOG_TAG = SQLiteDBHelper.class.getSimpleName();

    /**
     * the database
     */
    private SQLiteDatabase db;

    public SQLiteDBHelper(Activity activity) {
        super();
        this.mActivity = activity;
    }

    /**
     * prepare the database
     */
    public void prepareSQLiteDatabase() {

        this.db = getActivity().openOrCreateDatabase(DATABASE_NAME,
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

        // we need to check whether it is empty or not...
        Log.d(LOG_TAG, "db version is: " + db.getVersion());
        if (this.db.getVersion() == DATABASE_VERSION) {
            Log.i(LOG_TAG, "the db has just been created. Need to create the table...");
            db.execSQL("DROP TABLE todolist");
            db.setLocale(Locale.getDefault());
            db.setVersion(DATABASE_VERSION + 1);
            db.execSQL(SQL_CREATE_TODO_LIST_TABLE);
        } else {
            Log.i(LOG_TAG, "the db exists already. No need for table creation.");
        }

    }

    /**
     * the ContentValues object is needed to save the object into the db
     */
    public static ContentValues createDBTodoItem(TodoItem item){
        ContentValues insertItem = new ContentValues();
        insertItem.put(TodoContract.TodoEntry.COLUMN_TITLE, item.getTitle());
        insertItem.put(TodoContract.TodoEntry.COLUMN_DESCRIPTION, item.getDescription());
        insertItem.put(TodoContract.TodoEntry.COLUMN_ISDONE, item.isDone());
        insertItem.put(TodoContract.TodoEntry.COLUMN_ISFAVOURITE, item.isFavourite());
        insertItem.put(TodoContract.TodoEntry.COLUMN_DATE, item.getDate());
        insertItem.put(TodoContract.TodoEntry.COLUMN_TIME, item.getTime());

        return insertItem;
    }

    public Cursor getCursor(){
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TodoContract.TodoEntry.TABLE_NAME);

        Cursor cursor = queryBuilder.query(this.db, COLUMNS_TO_RETURN, null, null, null, null, ORDERING);
        return cursor;
    }

    public TodoItem createItemFromCursor(Cursor cursor){
        TodoItem todoItem = new TodoItem();
        todoItem.setId(cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry._ID)));
        todoItem.setTitle(cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TITLE)));
        todoItem.setDescription(cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DESCRIPTION)));
        todoItem.setIsDone(cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_ISDONE)) > 0);
        todoItem.setIsFavourite(cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_ISFAVOURITE)) > 0);
        todoItem.setDate(cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DATE)));
        todoItem.setTime(cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TIME)));
        return todoItem;
    }


    public void insert(TodoItem item){
        Log.i(LOG_TAG, "insert into db: " + item);
        ContentValues contentValues = SQLiteDBHelper.createDBTodoItem(item);

        long newItemId = this.db.insert(TodoContract.TodoEntry.TABLE_NAME, null, contentValues);
        item.setId(newItemId);
    }

    public void delete(TodoItem item){
        Log.i(LOG_TAG, "removing from db: " + item);

        this.db.delete(TodoContract.TodoEntry.TABLE_NAME, WHERE_IDENTIFY_ITEM, new String[]{String.valueOf(item.getId())});
        Log.i(LOG_TAG, "removing from db: " + item + " is completed");
    }

    public void update(TodoItem item){
        Log.i(LOG_TAG, "update(): " + item);
        Log.i(LOG_TAG, "update id: " + item.getId());
        this.db.update(TodoContract.TodoEntry.TABLE_NAME, createDBTodoItem(item), WHERE_IDENTIFY_ITEM, new String[]{String.valueOf(item.getId())});
        Log.i(LOG_TAG, "update(): " + item + " finished.");
        Log.i(LOG_TAG, "updated id: " + item.getId());
    }

    public void close(){
        Log.i(LOG_TAG, "closing the db..");
        this.db.close();
        Log.i(LOG_TAG, "The db has been closed");
    }

    public Activity getActivity() {
        return mActivity;
    }
}
