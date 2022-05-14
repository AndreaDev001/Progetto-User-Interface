package com.progetto.progetto.model.enums;

import com.progetto.progetto.view.StyleHandler;

public enum MovieSortOrder
{
    ASC("ascended"),
    DESC("descended");

    private final String name;

    MovieSortOrder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return StyleHandler.getInstance().getResourceBundle().getString(this.toString() + ".name");
    }
}