package com.progetto.progetto.model.handlers;

import info.movito.themoviedbapi.model.MovieDb;

import java.util.List;

public interface IResearchListener
{
    void OnResearchCompleted(List<MovieDb> result,boolean isText,boolean isLibrary);
    void OnResearchFailed();
}
