package timetogeter.context.time.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public record TimeSlotDTO(@JsonFormat(pattern = "HH:mm:ss") LocalTime startTime,
                          @JsonFormat(pattern = "HH:mm:ss") LocalTime endTime) {}
