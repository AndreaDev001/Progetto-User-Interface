package com.progetto.progetto.model.enums;

public enum ErrorType
{
    CONNECTION("Failed connection, retry later.",1),
    LOADING_PAGE("Page loading failed, something went wrong with the application!", 2),
    FILE("Something went wrong with the application!", 3);

    private final String message;
    private final int errorID;
    ErrorType(String message, int errorID)
    {
        this.message = message;
        this.errorID = errorID;
    }

    public String getMessage()
    {
        return message;
    }

    public int getErrorID()
    {
        return errorID;
    }
}
