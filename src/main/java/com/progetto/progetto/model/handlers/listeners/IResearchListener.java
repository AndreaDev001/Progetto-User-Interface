package com.progetto.progetto.model.handlers.listeners;

import com.progetto.progetto.model.enums.MovieViewMode;
import info.movito.themoviedbapi.model.MovieDb;

import java.util.List;

public interface IResearchListener
{
    void OnResearchStarted();
    void OnResearchSucceeded(List<MovieDb> result,boolean isText);
    void OnResearchFailed(boolean connection);
}
