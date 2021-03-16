package com.example.RESTExample.error;

public class CustomErrorResponse {
    private boolean success;
    private String errorMessage;

    public CustomErrorResponse() {
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
