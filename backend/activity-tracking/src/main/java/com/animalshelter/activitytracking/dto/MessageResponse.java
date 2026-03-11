package com.animalshelter.activitytracking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponse {

    private boolean success;
    private String message;
}
