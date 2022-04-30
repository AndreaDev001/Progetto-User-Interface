package com.progetto.progetto.model.exceptions;

public class FilmNotFoundException extends Exception{
    public FilmNotFoundException(String errorMessage)
    {
        super(errorMessage);
    }
}
