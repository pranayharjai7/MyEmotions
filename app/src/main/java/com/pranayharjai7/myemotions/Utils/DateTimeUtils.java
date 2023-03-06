package com.pranayharjai7.myemotions.Utils;

import java.time.LocalDateTime;

public class DateTimeUtils {

    public static String convertDateAndTimeToString(LocalDateTime dateTime) {
        StringBuilder resultDateTime = new StringBuilder();
        resultDateTime.append("")
                .append(dateTime.getDayOfMonth())
                .append("-")
                .append(dateTime.getMonthValue())
                .append("-")
                .append(dateTime.getYear());

        resultDateTime.append("T")
                .append(dateTime.getHour())
                .append("-")
                .append(dateTime.getMinute())
                .append("-")
                .append(dateTime.getSecond())
                .append("-")
                .append(dateTime.getNano());
        return resultDateTime.toString();
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateTime) {
        String date = dateTime.split("T")[0];
        String time = dateTime.split("T")[1];

        String[] splitDate = date.split("-");
        String[] splitTime = time.split("-");

        int dayOfMonth = Integer.parseInt(splitDate[0]);
        int month = Integer.parseInt(splitDate[1]);
        int year = Integer.parseInt(splitDate[2]);

        int hour = Integer.parseInt(splitTime[0]);
        int minutes = Integer.parseInt(splitTime[1]);
        int seconds = Integer.parseInt(splitTime[2]);
        int nano = Integer.parseInt(splitTime[3]);

        return LocalDateTime.of(year, month, dayOfMonth, hour, minutes, seconds, nano);
    }
}
