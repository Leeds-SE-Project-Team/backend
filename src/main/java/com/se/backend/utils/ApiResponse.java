package com.se.backend.utils;

import lombok.Getter;

/**
 * Represents the API response.
 *
 * @param <T> The type of the data.
 */
@Getter
public final class ApiResponse<T> {


    private final String message;

    private final T data;

    private final boolean success;

    private ApiResponse(String message, T data, boolean success) {
        this.message = message;
        this.data = data;
        this.success = success;
    }

    /**
     * Creates a success ApiResponse with a message and data.
     *
     * @param <T>     The type of the data.
     * @param message The success message.
     * @param data    The data to include in the response.
     * @return A success ApiResponse.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data, true);
    }

    /**
     * Creates an error ApiResponse with a message.
     *
     * @param <T>     The type of the data.
     * @param message The error message.
     * @return An error ApiResponse.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, null, false);
    }

}
