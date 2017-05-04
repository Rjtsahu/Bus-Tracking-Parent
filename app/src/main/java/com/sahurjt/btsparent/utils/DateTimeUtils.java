package com.sahurjt.btsparent.utils;

import android.text.format.DateUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rajat_Sahu on 05-04-2017.
 */

public class DateTimeUtils {
    private static SimpleDateFormat dateFormat;
    private static final String DATE_FORMAT_STR = "yyyy-MM-dd kk:mm:ss";
    private static Date date;

    public static String getPreetyTimeString(String this_date) {
        dateFormat = new SimpleDateFormat(DATE_FORMAT_STR);
        date = new Date();
        try {
            date = dateFormat.parse(this_date);
            PrettyTime prettyTime = new PrettyTime();
            return prettyTime.format(date);
        } catch (NullPointerException|ParseException p) {
            L.err("Cant parse date:" + this_date);
            return "";
        }
    }

    // return time substring from given date string
    public static String getTime(String date){
        if (date!=null){
            try {
               return date.substring(11,19);
            }catch (Exception e){
                L.err(String.format("cant parse date %s to time",date ));
            }
        }
        return "";
    }

}
