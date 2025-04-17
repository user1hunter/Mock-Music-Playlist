package com.example.playlistapp.config;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.logging.structured.StructuredLogFormatter;

import ch.qos.logback.classic.spi.ILoggingEvent;

class CustomLogging implements StructuredLogFormatter<ILoggingEvent> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @Override
    public String format(ILoggingEvent event) {
        String formattedTime = formatter.format(Instant.ofEpochMilli(event.getTimeStamp()));
        return "time=" + formattedTime + " level=" + event.getLevel() + " message=" + event.getMessage() + "\n";
    }

}