package de.fhb.fbi.acs.maas.todoapp.accessors;

import android.app.Activity;

/**
 * @author novruzov
 */
public abstract class AbstractActivityDataAccessor {

    private Activity activity;

    protected Activity getActivity() {
        return this.activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
