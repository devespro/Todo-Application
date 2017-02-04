package de.fhb.fbi.acs.maas.todoapp.utility;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author novruzov
 */
public class TodoUtility {
    public static String formatDate(long dateInMilliseconds) {
        Date date = dateInMilliseconds == 0 ? new Date() : new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }

    public static String formatTime(long timeInMilliseconds) {
        Date date = timeInMilliseconds == 0 ? null : new Date(timeInMilliseconds);
        if (date == null)
            return "--:--";
        return String.format("%02d:%02d", date.getHours(), date.getMinutes());
    }

    public static String getStringDateFromLong(long dateInMilliseconds){
        Date date = dateInMilliseconds == 0 ? null :new Date(dateInMilliseconds);
        if (date == null)
            return "--/--/----";
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return String.format("%02d/%02d/%d", day, month, year);
    }

    public static Calendar getCalendarFromLong(long dateInMilliseconds){
        Calendar cal = Calendar.getInstance();
        if (dateInMilliseconds > 0) {
            Date date = new Date(dateInMilliseconds);
            cal.setTime(date);
        }
        return cal;
    }
}
