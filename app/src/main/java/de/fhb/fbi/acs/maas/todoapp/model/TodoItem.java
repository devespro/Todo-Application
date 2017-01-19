package de.fhb.fbi.acs.maas.todoapp.model;

import java.io.Serializable;

/**
 * The TodoItem class represents the data model for each item in TodoApplication
 * Created by Esien Novruzov on 19/01/17.
 */
public class TodoItem extends GenericEntity implements Serializable {

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
        this.setDate(item.getDate());
        this.setTime(item.getTime());
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
}
