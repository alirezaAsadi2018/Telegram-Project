package com.telegram.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateAndTimeManager {
    public static long calculateElapsedTime(LocalDateTime localDate){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime localDateNow = LocalDateTime.now();
            System.out.println(dateTimeFormatter.format(localDate));
            System.out.println(dateTimeFormatter.format(localDateNow));
            long elapsedDays = ChronoUnit.DAYS.between(localDate, localDateNow);
            System.out.println(elapsedDays);
            return elapsedDays;
    }
}
