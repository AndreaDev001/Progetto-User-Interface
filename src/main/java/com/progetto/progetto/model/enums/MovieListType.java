package com.progetto.progetto.model.enums;

import com.progetto.progetto.model.handlers.StyleHandler;

public enum MovieListType
{
    MOST_POPULAR("Most Popular"),
    UPCOMING_MOVIES("Upcoming movies"),
    TOP_RATED_MOVIES("Top Rated Movies");

    private final String name;
    MovieListType(String name) {this.name = name;}
    public String getName()
    {
        return name;
    }
    public String getLocalizedName()
    {
        return StyleHandler.getInstance().getLocalizedString(this.toString() + ".name");
    }
}
