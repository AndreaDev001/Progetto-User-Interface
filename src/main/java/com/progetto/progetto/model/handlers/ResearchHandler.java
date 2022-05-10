package com.progetto.progetto.model.handlers;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortOrder;
import com.progetto.progetto.model.enums.MovieSortType;
import com.progetto.progetto.model.exceptions.FilmNotFoundException;
import info.movito.themoviedbapi.model.MovieDb;
import java.util.ArrayList;
import java.util.List;

public class ResearchHandler
{
    private static final ResearchHandler instance = new ResearchHandler();
    private MovieListType currentListType = MovieListType.MOST_POPULAR;
    private MovieSortType currentSortType = MovieSortType.POPULARITY;
    private MovieSortOrder currentSortOrder = MovieSortOrder.DESC;
    private MovieFilterType currentFilterType = MovieFilterType.SINGLE_GENRE;
    private int currentGenre;
    private String currentMultipleGenre;
    private String currentText = "";
    private int currentPage = 1;
    private int value = 0;
    private final List<IResearchListener> researchListeners = new ArrayList<>();

    private ResearchHandler()
    {
        System.out.println("Instance of Research Handler created correctly");
    }
    public void addListener(IResearchListener listener)
    {
        researchListeners.add(listener);
    }
    public void search(boolean isList)
    {
        try
        {
            String value = String.valueOf(currentGenre);
            if(currentFilterType == MovieFilterType.MULTIPLE_GENRES)
                value = currentMultipleGenre;
            List<MovieDb> result = isList ? FilmHandler.getInstance().getMovies(currentPage,currentListType) : FilmHandler.getInstance().makeSearch(currentText == null || currentText.isEmpty() ? value : currentText,currentPage,currentSortType,currentFilterType,currentSortOrder);
            for(IResearchListener current : researchListeners)
                current.OnResearchCompleted(result);
        }
        catch (FilmNotFoundException exception)
        {
            for(IResearchListener current : researchListeners)
                current.OnResearchFailed();
        }
    }
    public void setCurrentListType(MovieListType currentListType) {
        this.currentListType = currentListType;
        this.updateValue(false,true);
    }
    public void setCurrentSortType(MovieSortType currentSortType) {
        if(!currentText.isEmpty())
            return;
        this.currentSortType = currentSortType;
        this.updateValue(true,true);
    }
    public void setCurrentSortOrder(MovieSortOrder currentSortOrder) {
        if(!currentText.isEmpty())
            return;
        this.currentSortOrder = currentSortOrder;
        this.updateValue(true,true);
    }
    public void setCurrentFilterType(MovieFilterType currentFilterType,boolean update) {
        this.currentFilterType = currentFilterType;
        if(update)
            this.updateValue(true,true);
    }
    public void setCurrentGenre(int currentGenre,boolean update) {
        this.currentGenre = currentGenre;
        if(update)
            this.updateValue(true,true);
    }
    public void setCurrentText(String currentText) {
        this.currentFilterType = MovieFilterType.NAME;
        this.currentText = currentText;
        this.updateValue(true,false);
    }
    public void setCurrentMultipleGenre(String value,boolean update)
    {
        this.currentMultipleGenre = value;
        if(update)
            this.updateValue(true,true);
    }
    public void updateValue(boolean resetListType,boolean resetText)
    {
        currentListType = resetListType ? null : currentListType;
        currentText = resetText ? "" : currentText;
        currentPage = 1;
        this.search(!resetListType);
    }
    public void updateCurrentPage(boolean positive)
    {
        currentPage = positive ? currentPage + 1 : currentPage - 1;
        this.search(currentListType != null);
    }
    public String getCurrentText() {return currentText;}
    public MovieSortType getCurrentSortType() {return currentSortType;}
    public MovieSortOrder getCurrentSortOrder() {return currentSortOrder;}
    public MovieFilterType getCurrentFilterType() {return currentFilterType;}
    public MovieListType getCurrentListType() {return currentListType;}
    public int getCurrentGenre() {return currentGenre;}
    public static ResearchHandler getInstance() {return instance;}
}
