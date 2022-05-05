package com.progetto.progetto.model.enums;

public enum MovieListType
{
    MOST_POPULAR("Most Popular"),
    UPCOMING_MOVIES("Upcoming Movies"),
    TOP_RATED_MOVIES("Top Rated Movies");

    private final String name;
    MovieListType(String name) {this.name = name;}
    public String getName()
    {
        return name;
    }
}
