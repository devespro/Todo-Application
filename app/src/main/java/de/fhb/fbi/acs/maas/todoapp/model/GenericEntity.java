package de.fhb.fbi.acs.maas.todoapp.model;

/**
 * Created by Esien Novruzov on 19/01/17.
 */
public abstract class GenericEntity {
    protected static long ID;
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GenericEntity that = (GenericEntity) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
