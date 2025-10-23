package com.example.assessment.controller;

import com.example.assessment.exception.DeviceInUseException;
import com.example.assessment.exception.DeviceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(
        basePackages = "com.example.assessment.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<String> handleDeviceNotFound(DeviceNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DeviceInUseException.class)
    @ResponseBody
    public ResponseEntity<String> handleDeviceInUse(DeviceInUseException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
