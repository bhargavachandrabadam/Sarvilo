package com.easy.stazy.shared.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {


    private String status;

    private String message;

    private HttpStatus httpStatus;

    @Builder.Default
    private ZonedDateTime timeStamp = ZonedDateTime.now();

    /**
     * Static factory method to create a successful ApiResponse with the given message.
     *
     * @param message the message to be included in the ApiResponse
     * @return a successful ApiResponse
     */
    public static ApiResponse success(String message) {
        return ApiResponse.builder()
                .status("SUCCESS")
                .message(message)
                .timeStamp(ZonedDateTime.now())
                .build();
    }
    /**
     * Static factory method to create a successful ApiResponse with the given message and http status.
     *
     * @param message the message to be included in the ApiResponse
     * @param httpStatus the http status of the ApiResponse
     * @return a successful ApiResponse
     */
    public static ApiResponse success(String message, HttpStatus httpStatus){
        return ApiResponse.builder()
                .status("SUCCESS")
                .message(message)
                .timeStamp(ZonedDateTime.now())
                .httpStatus(httpStatus)
                .build();
    }

    /**
     * Static factory method to create a failed ApiResponse with the given message.
     *
     * @param message the message to be included in the ApiResponse
     * @return a failed ApiResponse
     */
    public static ApiResponse error(String message){
        return ApiResponse.builder()
                .status("ERROR")
                .message(message)
                .timeStamp(ZonedDateTime.now())
                .build();
    }

    /**
     * Static factory method to create a failed ApiResponse with the given message and http status.
     *
     * @param message the message to be included in the ApiResponse
     * @param httpStatus the http status of the ApiResponse
     * @return a failed ApiResponse
     */
    public static ApiResponse error(String message, HttpStatus httpStatus){
        return ApiResponse.builder()
                .status("ERROR")
                .message(message)
                .timeStamp(ZonedDateTime.now())
                .httpStatus(httpStatus)
                .build();
    }


}
