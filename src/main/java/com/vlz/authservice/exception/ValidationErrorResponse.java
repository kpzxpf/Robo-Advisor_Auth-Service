package com.vlz.authservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ValidationErrorResponse {
    private List<String> errors;
}
