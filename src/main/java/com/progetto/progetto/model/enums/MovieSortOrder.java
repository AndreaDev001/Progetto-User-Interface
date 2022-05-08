package com.progetto.progetto.model.enums;

public enum MovieSortOrder
{
    ASC("ascended"),
    DESC("descended");

    private final String name;
    MovieSortOrder(String name)
    {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
