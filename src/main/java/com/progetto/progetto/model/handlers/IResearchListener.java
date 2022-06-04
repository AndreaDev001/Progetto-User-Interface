package com.progetto.progetto.model.handlers;

import com.progetto.progetto.model.enums.MovieViewMode;
import info.movito.themoviedbapi.model.MovieDb;

import java.util.List;

public interface IResearchListener
{
    void OnResearchStarted();
    void OnResearchCompleted(List<MovieDb> result,boolean isText);
    void OnViewChanged(MovieViewMode viewMode,boolean clear,boolean search);
    void OnResearchFailed();
}
