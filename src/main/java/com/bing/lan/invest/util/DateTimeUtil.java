package com.bing.lan.invest.util;

import java.text.*;
import java.time.*;
import java.util.*;
import java.util.regex.*;

/**
 * Created by lanbing at 2023/3/14 14:02
 */

public class DateTimeUtil {

    public static boolean currentTimeIn(Integer[] period) {
        return currentTimeIn(period[0], period[1], period[2], period[3]);
    }

    // cn.hutool.core.date.DateUtil.isIn
    public static boolean currentTimeIn(int startHour, int startMinute, int endHour, int endMinute) {
        LocalTime currentTime = LocalTime.now();
        LocalTime startTime = LocalTime.of(startHour, startMinute);
        LocalTime endTime = LocalTime.of(endHour, endMinute);
        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }

    public static String timestampFormat(String timestamp) {
        // log.info("timestampFormat: {}", timestamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(Long.parseLong(timestamp));
    }

    public static Date chineseStandardTimeFormat(String dateString) {
        // Wed Mar 22 09:38:34 +0800 2023
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.US);
        try {
            return oldDateFormat.parse(dateString);
        } catch (Exception e) {
            throw new RuntimeException("时间转化格式错误 [dateString=" + dateString + "]");
        }
    }

    /**
     * z 正确格式： +0800  或者  GMT+08:00
     */
    public static Date chineseStandardTimeFormat1(String dateString) {
        try {
            // Tue Aug 21 2018 00:00:00 GMT+0800 (中国标准时间) 00:00:00
            dateString = dateString.split(Pattern.quote("(中国标准时间)"))[0];

            // Tue Aug 21 2018 00:00:00 GMT+0800
            dateString = dateString.replace("GMT+0800", "+0800");// 或者 dateString.replace("GMT+0800","GMT+08:00");

            // Tue Aug 21 2018 00:00:00 GMT+08:00
            SimpleDateFormat sf1 = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
            return sf1.parse(dateString);
        } catch (Exception e) {
            throw new RuntimeException("时间转化格式错误 [dateString=" + dateString + "]");
        }
    }

    public static void main(String[] args) {
        String dateString = "Tue Aug 21 09:38:34 +0800 2018";
        System.out.println(chineseStandardTimeFormat(dateString));
        dateString = "Tue Aug 21 2018 12:30:40 GMT+0800 (中国标准时间) 00:00:00";
        System.out.println(chineseStandardTimeFormat1(dateString));
    }
}
