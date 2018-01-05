package com.labs.jharkhandi.companymeetingscheduler.Util;

import android.annotation.SuppressLint;

import java.util.IllegalFormatException;

/**
 * Created by sumit on 4/1/18.
 */

public class Util {

    public static String[] DAY_OF_WEEK = {
            // 0         1          2           3
            "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY",
            //  4          5         6
            "THURSDAY", "FRIDAY", "SATURDAY"
    };

    @SuppressLint("DefaultLocale")
    public static String getAMPMTime(String time24HrFormat) throws Exception{
        String[] splitTime = time24HrFormat.split(":");

        if(splitTime.length != 2) throw new Exception("Wrong format");

        int hourOfDay = Integer.parseInt(splitTime[0]);
        int minute = Integer.parseInt(splitTime[1]);
        return String.format("%02d:%02d%s",
                hourOfDay == 0 ? 12:(hourOfDay <= 12 ? hourOfDay: hourOfDay - 12),
                minute,
                hourOfDay < 12 ? "AM":"PM");
    }

    public static int getHourOfDay(String time24HrFormat){
        String[] splitTime = time24HrFormat.split(":");
        return Integer.parseInt(splitTime[0]);
    }

    public static int getMinute(String time24HrFormat){
        String[] splitTime = time24HrFormat.split(":");
        return Integer.parseInt(splitTime[1]);
    }
}
