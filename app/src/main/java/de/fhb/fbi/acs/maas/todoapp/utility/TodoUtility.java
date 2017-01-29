package de.fhb.fbi.acs.maas.todoapp.utility;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by deves on 19/01/17.
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
        Date date = new Date(dateInMilliseconds);
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


    /*
    public static void main(String[] args) {
        Date date = new Date(1484607600000L);

        Calendar cal = Calendar.getInstance();
        //cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        System.out.println(day + "/" + month +"/"+ year );
        System.out.println("After formating");
        System.out.println(getStringDateFromLong(1484607600000L));
        System.out.println("#####");


    }
  */

}
