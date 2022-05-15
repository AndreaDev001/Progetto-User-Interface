package com.progetto.progetto.model.enums;

public enum MovieSortType
{
    ORIGINAL_TITLE ("original title"),
    RELEASE_DATE ("release date"),
    POPULARITY ("popularity"),
    VOTE_AVERAGE ("vote average"),
    VOTE_COUNT ("vote count"),
    REVENUE("revenue");

    private final String name;
    MovieSortType(String name) {this.name = name;}
    public String getName()
    {
        return name;
    }
}
