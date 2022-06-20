package com.progetto.progetto.model.handlers;

import com.progetto.progetto.model.enums.*;
import com.progetto.progetto.model.exceptions.FilmNotFoundException;
import com.progetto.progetto.model.handlers.listeners.IResearchListener;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.tools.MovieDbException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }

    /**
     * Aggiunge un listener alla ricerca
     * @param listener Il Listener da aggiungere
     * @param value Il nome della classe
     */
    public void addListener(IResearchListener listener,String value)
    {
        researchListeners.remove(value);
        researchListeners.put(value,listener);
    }
    /**
     * Esegue una ricerca usando i parametri attuali
     * @param isList Se la ricerca è una lista predefinita
     */
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
                        current.OnResearchSucceeded(result.getResults(),value);
                });
                filmsSearchService.setOnFailed((worker) -> {
                    for(IResearchListener current : researchListeners.values())
                        current.OnResearchFailed(worker.getSource().getException() instanceof UnknownHostException || worker.getSource().getException() instanceof MovieDbException || worker.getSource().getException() instanceof NoRouteToHostException);
                });
                filmsSearchService.restart();
            }
            else
            {
                sortingAvailable.set(currentText != null && !currentText.isEmpty());
                List<MovieDb> movies = new ArrayList<>();
                movies = FilmHandler.getInstance().filterMovies(FilmHandler.getInstance().getCurrentLoaded(), currentFilterType,currentFilterType == MovieFilterType.GENRE ? currentGenre : currentText);
                movies = FilmHandler.getInstance().sortMovies((currentGenre != null && !currentGenre.isEmpty() && currentFilterType == MovieFilterType.GENRE) || (currentText != null && !currentText.isEmpty() && currentFilterType == MovieFilterType.NAME) ? movies : FilmHandler.getInstance().getCurrentLoaded(),currentSortType,currentSortOrder);
                for(IResearchListener current : researchListeners.values())
                    current.OnResearchSucceeded(movies,value);
            }
        }
        catch (FilmNotFoundException exception)
        {
            this.currentMaxPage = 1;
            for(IResearchListener current : researchListeners.values())
                current.OnResearchFailed(false);
        }
    }

    /**
     * Aggiorna la lista attuale
     * @param currentListType Nuovo tipo di lista
     */
    public void setCurrentListType(MovieListType currentListType) {
        this.currentListType = currentListType;
        this.updateValue(false,true);
    }

    /**
     * Aggiorna il tipo attuale di ordinamento
     * @param currentSortType Tipo attuale di ordinamento
     */
    public void setCurrentSortType(MovieSortType currentSortType) {
        if(!currentText.isEmpty())
            return;
        this.currentSortType = currentSortType;
        this.updateValue(true,true);
    }

    /**
     * Aggiorna l'ordine attuale di ordinamento
     * @param currentSortOrder Tipo attuale ordine di ordinamento
     */
    public void setCurrentSortOrder(MovieSortOrder currentSortOrder) {
        if(!currentText.isEmpty())
            return;
        this.currentSortOrder = currentSortOrder;
        this.updateValue(true,true);
    }

    /**
     * Aggiorna il tipo di filtro da utilizzare nella ricerca
     * @param currentFilterType Il tipo di filtro
     * @param update Se reimpostare la lista e il testo attuale, ed eseguire una ricerca subito dopo
     */
    public void setCurrentFilterType(MovieFilterType currentFilterType,boolean update) {
        this.currentFilterType = currentFilterType;
        if(update)
            this.updateValue(true,true);
    }

    /**
     * Aggiorna il genere attuale
     * @param currentGenre Genere attuale
     * @param update Se reimpostare il tipo di lista e il testo attuale e se eseguire una ricerca subito dopo
     */
    public void setCurrentGenre(String currentGenre,boolean update) {
        this.currentGenre = currentGenre;
        if(update)
            this.updateValue(true,true);
    }

    /**
     * Imposta il nome della ricerca attuale(ricerca per testo)
     * @param currentText Testo attuale
     */
    public void setCurrentText(String currentText) {
        this.currentFilterType = MovieFilterType.NAME;
        this.currentText = currentText;
        this.updateValue(true,false);
    }

    /**
     * Reimposta la lista e il testo, ed esegue una ricerca
     * @param resetListType Se reimpostare la lista attuale
     * @param resetText Se reimpostare il testo attuale
     */
    public void updateValue(boolean resetListType,boolean resetText)
    {
        currentListType = resetListType ? null : currentListType;
        currentText = resetText ? "" : currentText;
        currentPage = 1;
        this.search(!resetListType);
    }

    /**
     * Aggiorna la pagina attuale
     * @param positive Se incrementare o diminuire currentPage di 1
     */
    public void updateCurrentPage(boolean positive)
    {
        currentPage = positive ? (currentPage + 1) % currentMaxPage : currentPage - 1;
        currentPage = Math.max(currentPage,1);
        this.search(currentListType != null);
    }

    /**
     * Imposta la movie view attuale
     * @param value Nuovo tipo di valore
     * @param force Se forzare il cambiamento, anche se non c'è stata nessuna modifica
     * @param clear Se reimpostare tutti i filtri
     * @param search Se effettuare una ricerca subito dopo il cambiamento della movie view
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
     * Metodo utilizzato per reimpostare la ricerca
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
     * Metodo utilizzato per ottenere la stringa corretta per effettuare una ricerca usando il genere attuale
     * @return Ritorna una stringa contente l'id di ogni genere contenuto nella ricerca, ognuno separato da una virgola
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

    /**
     * Aggiunge un listener alla movieViewProperty, se un altro oggetto della stessa classe non è contenuto
     * @param movieViewModeChangeListener Il listener da aggiungere
     * @param value Il nome della classe
     */
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
