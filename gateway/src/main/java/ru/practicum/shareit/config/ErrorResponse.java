package ru.practicum.shareit.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class ErrorResponse {
    private String error;
    private int errorCode;
    private List<String> details;
}

