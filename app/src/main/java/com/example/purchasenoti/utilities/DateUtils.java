package com.example.purchasenoti.utilities;

import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

    public static String getNextDate(String date, int year, int month, int day) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date lastDate = formatter.parse(date);

            if (lastDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(lastDate);

                calendar.add(Calendar.YEAR, year);
                calendar.add(Calendar.MONTH, month);
                calendar.add(Calendar.DATE, day);

                return formatter.format(calendar.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getDiffDay(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

            Date currentDate = new Date();
            Date endDate = formatter.parse(date);

            if (endDate != null) {
                return (endDate.getTime() - currentDate.getTime()) / MILLIS_PER_DAY;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getDDayString(String date) {
        long diffDay = getDiffDay(date);

        if (diffDay > 0) {
            return "(D - " + diffDay + ")";
        }
        return "(D + " + (-diffDay) + ")";
    }

    public static String getDate(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        return formatter.format(calendar.getTime());
    }

    public static Calendar getCalendar(String lastDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date date = formatter.parse(lastDate);

            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return calendar;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
