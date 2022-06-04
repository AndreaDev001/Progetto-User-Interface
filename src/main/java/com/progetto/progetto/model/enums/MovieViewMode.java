package com.progetto.progetto.model.enums;

import com.progetto.progetto.view.StyleHandler;

public enum MovieViewMode
{
    HOME("Home"),
    LIBRARY("Library");

    private final String name;
    MovieViewMode(String name)
    {
        this.name = name;
    }
    public final String getName() {return name;}
    public final String getLocalizedName()
    {
        return StyleHandler.getInstance().getResourceBundle().getString(this.toString() + ".name");
    }
}
