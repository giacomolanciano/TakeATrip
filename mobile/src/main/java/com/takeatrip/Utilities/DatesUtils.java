package com.takeatrip.Utilities;

import android.util.Log;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by david on 03/03/2016.
 */

public class DatesUtils {

    private static final String TAG = "TEST DatesUtils";


    public static int seconds, minutes, hours, days, years;

    public static String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        int cMonth = calendar.get(Calendar.MONTH) + 1;
        int cYear = calendar.get(Calendar.YEAR);
        String data = cYear+"-"+cMonth+"-"+cDay;

        return data;
    }


    public static int calcolaEta(String strDate1, String strDate2) {
        try {

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            fmt.setLenient(false);

            // Parses the two strings.
            Date d1 = fmt.parse(strDate1);
            Date d2 = fmt.parse(strDate2);

            // Calculates the difference in milliseconds.
            long millisDiff = d2.getTime() - d1.getTime();

            // Calculates days/hours/minutes/seconds.
            seconds = (int) (millisDiff / 1000 % 60);
            minutes = (int) (millisDiff / 60000 % 60);
            hours = (int) (millisDiff / 3600000 % 24);
            days = (int) (millisDiff / 86400000);
            years = days / 365;
            Log.i(TAG, "anno: " + years);

        } catch (Exception e) {
            System.err.println(e);
        }
        return years;
    }


    public static String convertFormatStringDate(String date, String from, String to) {
        String result;

        if(date.equals(null) && to.equals(null) && from.equals(null))
            return null;

        SimpleDateFormat fromFormat = new SimpleDateFormat(from);
        SimpleDateFormat toFormat = new SimpleDateFormat(to);
        Date formattedDate;

        formattedDate = fromFormat.parse(date, new ParsePosition(0));
        result = toFormat.format(formattedDate);


        return result;
    }

    public static Calendar getDateFromString(String date, String dateFormat) {
        Calendar cal = null;

        try{
            if(date.equals(null) || dateFormat.equals(null))
                return null;

            cal = Calendar.getInstance();

            SimpleDateFormat fromFormat = new SimpleDateFormat(dateFormat);
            Date formattedDate = fromFormat.parse(date, new ParsePosition(0));

            cal.setTime(formattedDate);
        }
        catch(Exception e){
            Log.e(TAG, "Thrown exception:" + e);

        }


        return cal;
    }

    public static String getStringFromDate(Date date, String dateFormat) {

        String result;

        if(date == (null) || dateFormat.equals(null))
            return null;

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat fromFormat = new SimpleDateFormat(dateFormat);
        result = fromFormat.format(date);

        return result;
    }
}

