package com.progetto.progetto.view;

public enum StyleMode
{
    LIGHT("light"),
    DARK("dark"),
    CUSTOM("custom");

    private final String name;

    StyleMode(String name) {this.name = name;}

    public String getName()
    {
        return name;
    }

}