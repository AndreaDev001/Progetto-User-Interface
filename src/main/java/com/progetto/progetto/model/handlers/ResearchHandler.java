package com.progetto.progetto.model.handlers;

import com.progetto.progetto.controller.MainController;
import com.progetto.progetto.model.enums.*;
import com.progetto.progetto.model.exceptions.FilmNotFoundException;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

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
    private IResearchListener researchListener;
    private final FilmsSearchService filmsSearchService = new FilmsSearchService();
    private ResearchHandler()
    {
        System.out.println("Instance of Research Handler created correctly");
    }
    public void setListener(IResearchListener listener)
    {
        researchListener = listener;
    }
    public void search(boolean isList)
    {
        MainController mainController = (MainController) researchListener;

        try
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
            if(movieViewMode == MovieViewMode.HOME)
            {
                mainController.scrollPane.setDisable(true);
                mainController.bottomHolder.setDisable(true);
                mainController.filmsProgress.setVisible(true);

                filmsSearchService.setup(isList,builder.toString());
                filmsSearchService.setOnSucceeded(workerStateEvent ->
                {
                    MovieResultsPage result = (MovieResultsPage) workerStateEvent.getSource().getValue();
                    currentMaxPage = Math.max(1,result.getTotalPages());
                    boolean value = (currentText == null || currentText.isEmpty()) && !isList;
                    researchListener.OnResearchCompleted(result.getResults(),value);
                    mainController.scrollPane.setDisable(false);
                    mainController.bottomHolder.setDisable(false);
                    mainController.filmsProgress.setVisible(false);
                });
                filmsSearchService.setOnFailed((worker) ->
                {
                    mainController.scrollPane.setDisable(false);
                    mainController.bottomHolder.setDisable(false);
                    mainController.filmsProgress.setVisible(false);
                    researchListener.OnResearchFailed();
                });
                filmsSearchService.restart();

            }
            else
            {
                List<MovieDb> movies = new ArrayList<>();
                movies = FilmHandler.getInstance().filterMovies(FilmHandler.getInstance().getCurrentLoaded(), currentFilterType,currentFilterType == MovieFilterType.GENRE ? currentGenre : currentText);
                movies = FilmHandler.getInstance().sortMovies((currentGenre != null && !currentGenre.isEmpty() && currentFilterType == MovieFilterType.GENRE) || (currentText != null && !currentText.isEmpty() && currentFilterType == MovieFilterType.NAME) ? movies : FilmHandler.getInstance().getCurrentLoaded(),currentSortType,currentSortOrder);
                researchListener.OnResearchCompleted(movies,false);
            }
        }
        catch (FilmNotFoundException exception)
        {
            this.currentMaxPage = 1;
            researchListener.OnResearchFailed();
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
    public void setCurrentGenre(String currentGenre,boolean update) {
        this.currentGenre = currentGenre;
        if(update)
            this.updateValue(true,true);
    }
    public void setCurrentText(String currentText) {
        this.currentFilterType = MovieFilterType.NAME;
        this.currentText = currentText;
        this.updateValue(true,false);
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
        currentPage = positive ? (currentPage + 1) % currentMaxPage : currentPage - 1;
        currentPage = Math.max(currentPage,1);
        this.search(currentListType != null);
    }
    public void setCurrentViewMode(MovieViewMode value,boolean force,boolean clear,boolean search)
    {
        if(this.movieViewMode != value || force)
        {
            this.movieViewMode = value;
            researchListener.OnViewChanged(movieViewMode,clear,search);
        }
    }
    public void clearSearch()
    {
        this.currentFilterType = MovieFilterType.GENRE;
        this.currentGenre = "";
        this.currentText = "";
        this.currentSortType = MovieSortType.POPULARITY;
        this.currentSortOrder = MovieSortOrder.DESC;
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
