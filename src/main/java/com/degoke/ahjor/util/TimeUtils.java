package com.degoke.ahjor.util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static Date addMinutes(int noMinutes) {
        Calendar currentTime = Calendar.getInstance();

        currentTime.add(Calendar.MINUTE, noMinutes);

        Date timePlusNoMinutes = currentTime.getTime();

        return timePlusNoMinutes;
    }

    public static boolean isExpired(Date date) {
        Calendar currentTimeInstance = Calendar.getInstance();

        Date currentTime = currentTimeInstance.getTime();

        int result = currentTime.compareTo(date);

        return result > 0;
    }
}
