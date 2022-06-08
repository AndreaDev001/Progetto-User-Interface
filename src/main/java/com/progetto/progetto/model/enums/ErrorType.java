package com.progetto.progetto.model.enums;

public enum ErrorType
{
    CONNECTION("connectionError.name",1),
    LOADING_PAGE("pageLoadError.name", 2),
    FILE("fileLoadError.name", 3);

    private final String message;
    private final int errorID;
    ErrorType(String message, int errorID)
    {
        this.message = message;
        this.errorID = errorID;
    }

    public String getUnlocalizedMessage()
    {
        return message;
    }

    public int getErrorID()
    {
        return errorID;
    }
}
