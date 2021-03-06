package de.fhb.fbi.acs.maas.todoapp.model;

import android.util.Log;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The TodoItem class represents the data model for each item in TodoApplication
 * @author novruzov
 */
public class TodoItem extends GenericEntity implements Serializable, Comparable<TodoItem> {

    private String title;
    private String description;
    private boolean doneStatus;
    private boolean favouriteStatus;
    private long date;
    private long time;

    private static final long serialVersionUID = -7481912314472891511L;

    public TodoItem(long id, String title, String description, boolean doneStatus, boolean favouriteStatus, long date, long time) {
        this.setId(id == -1 ? ID++ : id);
        this.title = title;
        this.description = description;
        this.doneStatus = doneStatus;
        this.favouriteStatus = favouriteStatus;
        this.date = date;
        this.time = time;
    }

    public TodoItem(String title, String description, boolean doneStatus, boolean favouriteStatus, long date, long time) {
        this.title = title;
        this.description = description;
        this.doneStatus = doneStatus;
        this.favouriteStatus = favouriteStatus;
        this.date = date;
        this.time = time;
    }

    public TodoItem(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDoneStatus() {
        return doneStatus;
    }



    public boolean isFavouriteStatus() {
        return favouriteStatus;
    }

    public void setDoneStatus(boolean doneStatus) {
        this.doneStatus = doneStatus;
    }

    public void setFavouriteStatus(boolean favouriteStatus) {
        this.favouriteStatus = favouriteStatus;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Updates the current TodoItem with data from item
     * @param item for updating from
     */
    public void updateFrom(TodoItem item) {
        this.setTitle(item.getTitle());
        this.setDescription(item.getDescription());
        this.setFavouriteStatus(item.favouriteStatus);
        this.setDoneStatus(item.doneStatus);
        this.setDate(item.getDate());
        this.setTime(item.getTime());
        Log.e("mytag", "updateFrom: #######" + item );
    }

    @Override
    public int compareTo(TodoItem another) {
        if ((this.isDoneStatus() && another.isDoneStatus()) || (!this.isDoneStatus() && !another.isDoneStatus()))
            return 0;
        else if (this.isDoneStatus() && !another.isDoneStatus()){
            return 1;
        } else
            return -1;
    }

    public static Comparator<TodoItem> sortByDateAndFavouriteComparator(){
        return new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem lhs, TodoItem rhs) {
                int result = lhs.compareTo(rhs);

                if (result == 0) {
                    if (lhs.getDate() > rhs.getDate()) {
                        return 1;
                    } else if (lhs.getDate() < rhs.getDate()) {
                        return -1;
                    } else {
                        if (lhs.isFavouriteStatus() && !rhs.isFavouriteStatus()){
                            return -1;
                        } else if (!lhs.isFavouriteStatus() && rhs.isFavouriteStatus()){
                            return 1;
                        } else
                            return 0;
                    }
                }
                return result;
            }
        };
    }

    public static Comparator<TodoItem> sortByFavouriteAndDateComparator(){
        return new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem lhs, TodoItem rhs) {
                int result = lhs.compareTo(rhs);

                if (result == 0) {
                   {
                        if (lhs.isFavouriteStatus() && !rhs.isFavouriteStatus()){
                            return -1;
                        } else if (!lhs.isFavouriteStatus() && rhs.isFavouriteStatus()){
                            return 1;
                        } else {
                            if (lhs.getDate() > rhs.getDate()) {
                                return 1;
                            } else if (lhs.getDate() < rhs.getDate()) {
                                return -1;
                            } else return 0;
                        }
                    }
                }
                return result;
            }
        };
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "id= " + getId() + "\n" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", doneStatus=" + doneStatus +
                ", favouriteStatus=" + favouriteStatus +
                ", date=" + date +
                ", time=" + time +
                '}';
    }

}
