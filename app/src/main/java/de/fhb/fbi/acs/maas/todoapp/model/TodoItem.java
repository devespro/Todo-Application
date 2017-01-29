package de.fhb.fbi.acs.maas.todoapp.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The TodoItem class represents the data model for each item in TodoApplication
 * @author Esien Novruzov
 */
public class TodoItem extends GenericEntity implements Serializable, Comparable<TodoItem> {

    private String title;
    private String description;
    private boolean isDone;
    private boolean isFavourite;
    private long date;
    private long time;

    private static final long serialVersionUID = -7481912314472891511L;

    public TodoItem(long id, String title, String description, boolean isDone, boolean isFavourite, long date, long time) {
        this.setId(id == -1 ? ID++ : id);
        this.title = title;
        this.description = description;
        this.isDone = isDone;
        this.isFavourite = isFavourite;
        this.date = date;
        this.time = time;
    }

    public TodoItem(String title, String description, boolean isDone, boolean isFavourite, long date, long time) {
        this.title = title;
        this.description = description;
        this.isDone = isDone;
        this.isFavourite = isFavourite;
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

    public boolean isDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
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
        this.setIsFavourite(item.isFavourite);
        this.setIsDone(item.isDone);
        this.setDate(item.getDate());
        this.setTime(item.getTime());
        Log.e("mytag", "updateFrom: #######" + item );
    }

    @Override
    public int compareTo(TodoItem another) {
        if ((this.isDone() && another.isDone()) || (!this.isDone() && !another.isDone()))
            return 0;
        else if (this.isDone() && !another.isDone()){
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
                        if (lhs.isFavourite() && !rhs.isFavourite()){
                            return -1;
                        } else if (!lhs.isFavourite() && rhs.isFavourite()){
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
                        if (lhs.isFavourite() && !rhs.isFavourite()){
                            return -1;
                        } else if (!lhs.isFavourite() && rhs.isFavourite()){
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

    //TODO only for debugging reasons! Delete before submitting
    @Override
    public String toString() {
        return "TodoItem{" +
                "id= " + getId() + "\n" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isDone=" + isDone +
                ", isFavourite=" + isFavourite +
                ", date=" + date +
                ", time=" + time +
                '}';
    }

    public static void main(String[] args) {
        List<TodoItem> items = new ArrayList<>();
        TodoItem item1 = new TodoItem(-1, "first title","1 description",false,true,1234567l,0l);
        TodoItem item2 = new TodoItem(-1, "second title","2 description",true,false,1234569l,0l);
        TodoItem item3 = new TodoItem(-1, "third title","3 description",false,false,1234561l,0l);
        TodoItem item4 = new TodoItem(-1, "forth title","4 description",true,true, 1234563l,0l);
        TodoItem item5 = new TodoItem(-1, "fifth title","5 description",false,true,1234568l,0l);
        TodoItem item6 = new TodoItem(-1, "sixth title","6 description",true,true,1234563l,0l);
        TodoItem item7 = new TodoItem(-1, "seventh title","7 description",true,false,1234562l,0l);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);
        items.add(item7);

        System.out.println("Initial list");
        for (TodoItem item : items){
            System.out.println(item.isDone + "/" + item.isFavourite() + "/" + item.getDate());
        }

        System.out.println("After sorting by isDone");
        Collections.sort(items);
        for (TodoItem item : items){
            System.out.println(item.isDone + "/" + item.isFavourite() + "/" + item.getDate());
        }

        System.out.println("####after sorting by isDone, Date and Favourite######");
        Collections.sort(items, sortByDateAndFavouriteComparator());
        for (TodoItem item : items){
            System.out.println(item.isDone + "/" + item.isFavourite() + "/" + item.getDate());
        }

        System.out.println("####after sorting by isDone, Favourite and Date######");
        Collections.sort(items, sortByFavouriteAndDateComparator());
        for (TodoItem item : items){
            System.out.println(item.isDone + "/" + item.isFavourite() + "/" + item.getDate());
        }
    }
}
