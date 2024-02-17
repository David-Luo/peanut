package com.peanut.diff.runtime.checker;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class LocalDateTimeChecker extends DefaultDiffChecker<LocalDateTime> {

    @Override
    public boolean equals(LocalDateTime before, LocalDateTime after) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        before = LocalDateTime.parse(dateTimeFormatter.format(before),dateTimeFormatter);
        after = LocalDateTime.parse(dateTimeFormatter.format(after),dateTimeFormatter);
        return Objects.equals(before,after);
    }
}
