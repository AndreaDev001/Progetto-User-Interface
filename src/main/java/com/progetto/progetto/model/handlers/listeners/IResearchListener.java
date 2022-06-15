package com.progetto.progetto.model.handlers.listeners;

import com.progetto.progetto.model.enums.MovieViewMode;
import info.movito.themoviedbapi.model.MovieDb;

import java.util.List;

public interface IResearchListener
{
    void OnResearchStarted();
    void OnResearchSuccessed(List<MovieDb> result,boolean isText);
    void OnResearchFailed();
}
