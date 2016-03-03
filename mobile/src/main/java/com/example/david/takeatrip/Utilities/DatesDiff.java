package com.example.david.takeatrip.Utilities;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by david on 03/03/2016.
 */

    public class DatesDiff {

        public static int seconds, minutes, hours, days, years;



        public static int eta (String strDate1, String strDate2) {
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
                years = days/360;
                Log.i("TEST", "anno: " + years);

            }   catch (Exception e) {
                System.err.println(e);
            }
       return years;
        }
    }

