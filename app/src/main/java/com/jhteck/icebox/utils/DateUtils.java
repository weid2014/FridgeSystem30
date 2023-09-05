package com.jhteck.icebox.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtils {

    public final static String format_yyyyMMddhhmmssfff = "yyyy-MM-dd HH:mm:ss SSS";
    public final static String format_yyyyMMddhhmmss = "yyyy-MM-dd HH:mm:ss";
    public final static String format_yyyyMMddhhmmssNew = "yyyyMMddHHmmss";

    public final static String format_yyyyMMddhhmm = "yyyy-MM-dd HH:mm";

    public final static String format_yyyyMMddhh = "yyyy-MM-dd HH";

    public final static String format_yyyyMMdd = "yyyy-MM-dd";
    public final static String format_yyyyMMddnew = "yyyyMMdd";

    public final static String format_hhmmss = "HH:mm:ss";

    public final static String format_hhmm = "HH:mm";

    public final static String format_ss = "ss";

    public final static String format_mm = "mm";

    public final static String format_hh = "HH";

    //日期转字符串
    public static String formatDateToString(Date date, String formatDate) {
        if (date == null) {
            return null;
        }

        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat(formatDate);
        String resStr = format.format(date);
        return resStr;
    }

    //字符串转日期
    public static Date formatStringToDate(String dateStr, String formatDate) {
        if (dateStr == null) {
            return null;
        }
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat(formatDate);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //时间格式转换
    public static String convertDateFormatOfString(String dateStr, String oldFormatDate, String newFormatDate) {
        if (dateStr == null) {
            return null;
        }
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat(oldFormatDate);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String resStr = null;
        @SuppressLint("SimpleDateFormat") DateFormat format1 = new SimpleDateFormat(newFormatDate);
        if (date != null) {
            resStr = format1.format(date);
        }
        return resStr;
    }

    //获得分裂的时间
    public static String getFormat_hhmmssSplit(String dateStr, String formatDate) {
        if (dateStr == null) {
            return null;
        }

        String[] timeArray = dateStr.split(":");
        if (timeArray.length < 3) {
            return null;
        }

        String temp;
        switch (formatDate) {
            case format_hh:
                temp = timeArray[0];
                break;
            case format_mm:
                temp = timeArray[1];
                break;
            default:
                temp = timeArray[3];
        }
        return temp;
    }

    //时间比较
    public static int dateCompare(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return -1;
        }
        return date1.compareTo(date2);
    }

    public static int dateCompare(String date1, String date2, String formatDate) {
        if (date1 == null || date2 == null || formatDate == null) {
            return -1;
        }
        Date d1 = formatStringToDate(date1, formatDate);
        Date d2 = formatStringToDate(date2, formatDate);
        return dateCompare(d1, d2);
    }
}