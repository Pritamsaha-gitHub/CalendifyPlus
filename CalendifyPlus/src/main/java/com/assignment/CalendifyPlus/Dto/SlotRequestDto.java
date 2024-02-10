package com.assignment.CalendifyPlus.Dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class SlotRequestDto {
    private String date;
    private int duration;
    private List<String> requiredUserIds;
}
