package com.progetto.progetto.model.handlers;

import com.progetto.progetto.model.enums.*;
import com.progetto.progetto.model.exceptions.FilmNotFoundException;
import com.progetto.progetto.model.handlers.listeners.IResearchListener;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.*;

public class ResearchHandler
{
    private static final ResearchHandler instance = new ResearchHandler();
    private MovieViewMode movieViewMode = MovieViewMode.HOME;
    private MovieListType currentListType = MovieListType.MOST_POPULAR;
    private MovieSortType currentSortType = MovieSortType.POPULARITY;
    private MovieSortOrder currentSortOrder = MovieSortOrder.DESC;
    private MovieFilterType currentFilterType = MovieFilterType.GENRE;
    private String currentGenre;
    private String currentText = "";
    private int currentPage = 1;
    private int currentMaxPage = 1;
    private final Map<String,IResearchListener> researchListeners = new HashMap<>();
    private final Map<String,ChangeListener<MovieViewMode>> viewListeners = new HashMap<>();
    private final FilmsSearchService filmsSearchService = new FilmsSearchService();
    private final BooleanProperty sortingAvailable = new SimpleBooleanProperty();
    private final ObjectProperty<MovieViewMode> movieViewModeObjectProperty = new SimpleObjectProperty<>(MovieViewMode.HOME);

    private ResearchHandler()
    {
        System.out.println("Instance of Research Handler created correctly");
    }
    public void addListener(IResearchListener listener,String value)
    {
        researchListeners.remove(value);
        researchListeners.put(value,listener);
    }
    public void search(boolean isList)
    {
        try
        {
            sortingAvailable.set(isList || (currentText != null && !currentText.isEmpty()) || movieViewMode == MovieViewMode.HOME && currentGenre != null && currentGenre.isEmpty());
            String genre = getCalculatedGenre();
            for(IResearchListener current : researchListeners.values())
                current.OnResearchStarted();
            boolean value = (currentText == null || currentText.isEmpty()) && !isList;
            if(movieViewMode == MovieViewMode.HOME)
            {
                if(!isList && (currentGenre == null || currentGenre.isEmpty()) && (currentText == null || currentText.isEmpty())){
                    ResearchHandler.getInstance().setCurrentListType(MovieListType.MOST_POPULAR);
                    return;
                }
                filmsSearchService.setup(isList,genre);
                filmsSearchService.setOnSucceeded(workerStateEvent ->
                {
                    MovieResultsPage result = (MovieResultsPage) workerStateEvent.getSource().getValue();
                    currentMaxPage = Math.max(1,result.getTotalPages());
                    for(IResearchListener current : researchListeners.values())
                        current.OnResearchSuccessed(result.getResults(),value);
                });
                filmsSearchService.setOnFailed((worker) -> {
                    for(IResearchListener current : researchListeners.values())
                        current.OnResearchFailed();
                });
                filmsSearchService.restart();
            }
            else
            {
                List<MovieDb> movies = new ArrayList<>();
                movies = FilmHandler.getInstance().filterMovies(FilmHandler.getInstance().getCurrentLoaded(), currentFilterType,currentFilterType == MovieFilterType.GENRE ? currentGenre : currentText);
                movies = FilmHandler.getInstance().sortMovies((currentGenre != null && !currentGenre.isEmpty() && currentFilterType == MovieFilterType.GENRE) || (currentText != null && !currentText.isEmpty() && currentFilterType == MovieFilterType.NAME) ? movies : FilmHandler.getInstance().getCurrentLoaded(),currentSortType,currentSortOrder);
                for(IResearchListener current : researchListeners.values())
                    current.OnResearchSuccessed(movies,value);
            }
        }
        catch (FilmNotFoundException exception)
        {
            this.currentMaxPage = 1;
            for(IResearchListener current : researchListeners.values())
                current.OnResearchFailed();
        }
    }

    /**
     * Updates the current list Type
     * @param currentListType New List Type
     */
    public void setCurrentListType(MovieListType currentListType) {
        this.currentListType = currentListType;
        this.updateValue(false,true);
    }

    /**
     * Updates the current sort Type
     * @param currentSortType New Sort Type
     */
    public void setCurrentSortType(MovieSortType currentSortType) {
        if(!currentText.isEmpty())
            return;
        this.currentSortType = currentSortType;
        this.updateValue(true,true);
    }

    /**
     * Updates the current sort order
     * @param currentSortOrder New Sort order
     */
    public void setCurrentSortOrder(MovieSortOrder currentSortOrder) {
        if(!currentText.isEmpty())
            return;
        this.currentSortOrder = currentSortOrder;
        this.updateValue(true,true);
    }

    /**
     * Updates current filter type
     * @param currentFilterType New Filter Type
     * @param update If we need to reset the current list type and the current text
     */
    public void setCurrentFilterType(MovieFilterType currentFilterType,boolean update) {
        this.currentFilterType = currentFilterType;
        if(update)
            this.updateValue(true,true);
    }

    /**
     * Updates current Genre String
     * @param currentGenre New Current Genre
     * @param update If we need to reset the current list type and the current text
     */
    public void setCurrentGenre(String currentGenre,boolean update) {
        this.currentGenre = currentGenre;
        if(update)
            this.updateValue(true,true);
    }

    /**
     * Updates the current Text,used when performing a name search,always resets the current list type and the current genre
     * @param currentText The new current text
     */
    public void setCurrentText(String currentText) {
        this.currentFilterType = MovieFilterType.NAME;
        this.currentText = currentText;
        this.updateValue(true,false);
    }

    /**
     * Method used to reset or not the current List or the current Text
     * @param resetListType If we need to reset the current List
     * @param resetText If we need to reset the current Text
     */
    public void updateValue(boolean resetListType,boolean resetText)
    {
        currentListType = resetListType ? null : currentListType;
        currentText = resetText ? "" : currentText;
        currentPage = 1;
        this.search(!resetListType);
    }

    /**
     * Method used to update the current page of the search
     * @param positive If we need to subtract or add by one
     */
    public void updateCurrentPage(boolean positive)
    {
        currentPage = positive ? (currentPage + 1) % currentMaxPage : currentPage - 1;
        currentPage = Math.max(currentPage,1);
        this.search(currentListType != null);
    }

    /**
     * Method used to update the current View Mode,fires a ViewChanged Event
     * @param value New MovieViewMode value
     * @param force If we need to force the event,even if the view did not change
     * @param clear If we need to clear all search filters,this will performed by the listener if needed
     * @param search If we need to perform a search after the event,the search function will be called by the listener if needed
     */
    public void setCurrentViewMode(MovieViewMode value,boolean force,boolean clear,boolean search)
    {
        if(this.movieViewMode != value || force)
        {
            this.movieViewMode = value;
            sortingAvailable.set(value == MovieViewMode.HOME);
            if(clear)
                ResearchHandler.getInstance().clearSearch();
            movieViewModeObjectProperty.set(value);
            if(search && value == MovieViewMode.LIBRARY)
                this.search(currentListType != null);
        }
    }
    /**
     * Method used the reset the current search
     */
    public void clearSearch()
    {
        this.currentFilterType = MovieFilterType.GENRE;
        this.currentGenre = "";
        this.currentText = "";
        this.currentSortType = MovieSortType.POPULARITY;
        this.currentSortOrder = MovieSortOrder.DESC;
    }
    /**
     * Method used to get the corrected Genre to use in a search,the genre received is a list of indexes separated by commas,update the values with the correct genre ids
     * @return A String containing the genre ids to search
     */
    private String getCalculatedGenre()
    {
        StringBuilder builder = new StringBuilder();
        if(currentGenre == null)
            currentGenre = "";
        else {
            if (!currentGenre.isEmpty()) {
                String[] values = currentGenre.split(",");
                for (int i = 0; i < values.length; i++) {
                    int value = FilmHandler.getInstance().getValues().get(Integer.parseInt(values[i])).getId();
                    builder.append(value).append(i != values.length - 1 ? "," : "");
                }
            }
        }
        return builder.toString();
    }
    public void addViewListener(ChangeListener<MovieViewMode> movieViewModeChangeListener,String value)
    {
        if(viewListeners.containsKey(value))
            movieViewModeObjectProperty.removeListener(viewListeners.get(value));
        viewListeners.remove(value);
        this.viewListeners.put(value,movieViewModeChangeListener);
        this.movieViewModeObjectProperty.addListener(movieViewModeChangeListener);
    }
    public final String getCurrentText() {return currentText;}
    public final MovieViewMode getCurrentViewMode() {return movieViewMode;}
    public final MovieSortType getCurrentSortType() {return currentSortType;}
    public final MovieSortOrder getCurrentSortOrder() {return currentSortOrder;}
    public final MovieFilterType getCurrentFilterType() {return currentFilterType;}
    public final MovieListType getCurrentListType() {return currentListType;}
    public final String getCurrentGenre() {return currentGenre;}
    public final int getCurrentPage() {return currentPage;}
    public final int getCurrentMaxPage() {return Math.max(1,currentMaxPage - 1);}
    public final BooleanProperty getSortingAvailable() {return sortingAvailable;}
    public static ResearchHandler getInstance() {return instance;}


    private class FilmsSearchService extends Service<MovieResultsPage>
    {
        private boolean isList;
        private String searchValues;

        public void setup(boolean list,String searchValues)
        {
            this.isList = list;
            this.searchValues = searchValues;
        }
        @Override
        protected Task<MovieResultsPage> createTask()
        {
            return new Task<>()
            {
                @Override
                protected MovieResultsPage call() throws FilmNotFoundException
                {
                    return isList ? FilmHandler.getInstance().getMovies(currentPage, currentListType) : FilmHandler.getInstance().makeSearch(currentText == null || currentText.isEmpty() ? searchValues : currentText, currentPage, currentSortType, currentFilterType, currentSortOrder);
                }
            };
        }
    }
}
