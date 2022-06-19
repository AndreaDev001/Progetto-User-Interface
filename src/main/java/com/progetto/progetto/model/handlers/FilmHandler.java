package com.progetto.progetto.model.handlers;

import com.progetto.progetto.MainApplication;
import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortOrder;
import com.progetto.progetto.model.enums.MovieSortType;
import com.progetto.progetto.model.exceptions.FilmNotFoundException;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

public class FilmHandler
{
    private static final FilmHandler instance = new FilmHandler();
    private List<Genre> genres = new ArrayList<>();
    private TmdbApi tmdbApi;
    private TmdbMovies movies;
    private String defaultPath;
    private int currentSelectedFilm = 0;
    private final List<MovieDb> currentLoaded = new Vector<>();
    private final Map<MovieDb,String> movieElementId = new HashMap<>();
    private final MovieQueryService movieDbService = new MovieQueryService();
    private final MovieGenreService movieGenreService = new MovieGenreService();

    private FilmHandler()
    {
        System.out.println("Instance of Film Handler created correctly");
        init();
    }
    private void init()
    {
        String apiKey = "3837271101e801680438310f38a3feff";
        try
        {
            tmdbApi = new TmdbApi(apiKey);
            movies = tmdbApi.getMovies();
            defaultPath = "https://image.tmdb.org/t/p/w500";
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
    /**
     * Legge i film delle liste predefinite fornite dalla API
     * @param page Il numero della pagina da cercare
     * @param movieListType Il tipo della lista
     * @return Una MovieResultsPage che contiene i risultati dei film
     * @throws FilmNotFoundException Viene generata questo errore se nessun film è stato trovato
     */
    public MovieResultsPage getMovies(int page, MovieListType movieListType) throws FilmNotFoundException
    {
        MovieResultsPage movieDbs = null;
        switch (movieListType)
        {
            case MOST_POPULAR ->  movieDbs = movies.getPopularMovies(StyleHandler.getInstance().getCurrentLanguage().toString(),page);
            case UPCOMING_MOVIES -> movieDbs = movies.getUpcoming(StyleHandler.getInstance().getCurrentLanguage().toString(),page,null);
            case TOP_RATED_MOVIES -> movieDbs = movies.getTopRatedMovies(StyleHandler.getInstance().getCurrentLanguage().toString(),page);
        }
        if(movieDbs == null || movieDbs.getResults().size() == 0)
            throw new FilmNotFoundException("An error has occured,result is empty");
        return movieDbs;
    }
    /**
     * Aggiorna i generi, leggendoli dalla API,necessario quando bisogna cambiare linguaggio
     * @param error Callback per gestire eventuali errori
     * @param success Callback per gestire il successo della operazione
     */
    public void updateGenres(Consumer<Throwable> error, Consumer<WorkerStateEvent> success)
    {
        if(tmdbApi == null)
            this.init();
        movieGenreService.setOnFailed(workerStateEvent -> error.accept(workerStateEvent.getSource().getException()));
        movieGenreService.setOnSucceeded(success::accept);
        movieGenreService.restart();
    }

    /**
     * Ordina una lista di film, seguendo un ordine specificato
     * @param values La lista dei film da ordinare
     * @param movieSortType Il tipo di ordinamento da utilizzare
     * @param movieSortOrder Come ordinare la lista, crescente o decrescente
     * @return La lista di film ordinata
     */
    public List<MovieDb> sortMovies(List<MovieDb> values, MovieSortType movieSortType,MovieSortOrder movieSortOrder)
    {
        List<MovieDb> result = new ArrayList<>();
        switch (movieSortType)
        {
            case ORIGINAL_TITLE -> result = values.stream().sorted(movieSortOrder == MovieSortOrder.DESC ? Comparator.comparing(MovieDb::getTitle).reversed() : Comparator.comparing(MovieDb::getTitle)).toList();
            case RELEASE_DATE -> result = values.stream().sorted((o1,o2) ->
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date firstDate = null;
                Date secondDate = null;
                try
                {
                    firstDate = format.parse(o1.getReleaseDate());
                    secondDate = format.parse(o2.getReleaseDate());
                    return movieSortOrder == MovieSortOrder.DESC ? secondDate.compareTo(firstDate) : firstDate.compareTo(secondDate);
                }
                catch (ParseException exception)
                {
                    if(firstDate == null)
                        return -1;
                }
                return 1;
            }).toList();
            case VOTE_AVERAGE -> result = values.stream().sorted(movieSortOrder == MovieSortOrder.DESC ? Comparator.comparing(MovieDb::getVoteAverage).reversed(): Comparator.comparing(MovieDb::getVoteAverage)).toList();
            case POPULARITY -> result = values.stream().sorted(movieSortOrder == MovieSortOrder.DESC ? Comparator.comparing(MovieDb::getPopularity).reversed() : Comparator.comparing(MovieDb::getPopularity)).toList();
            case VOTE_COUNT -> result = values.stream().sorted(movieSortOrder == MovieSortOrder.DESC ? Comparator.comparing(MovieDb::getVoteCount).reversed() : Comparator.comparing(MovieDb::getVoteCount)).toList();
        }
        return result;
    }
    /**
     * Filtra una lista di film, usando il tipo di filtro specificato come parametro
     * @param movies La lista contenente i film da filtrare
     * @param movieFilterType Il tipo di filtro da utilizzare
     * @param value Il genere o il nome utilizzato per filtrare
     * @return Una nuova lista contenente i risultati validi
     * @throws FilmNotFoundException Viene generato se la lista dei nuovi film è vuota
     */
    public List<MovieDb> filterMovies(List<MovieDb> movies,MovieFilterType movieFilterType,String value) throws FilmNotFoundException
    {
        if(movies == null || movies.isEmpty())
            throw  new FilmNotFoundException("Movies is empty");
        if(value == null || value.isEmpty())
            return movies;
        List<MovieDb> result = new ArrayList<>();
        switch (movieFilterType)
        {
            case GENRE -> {
                String[] values = value.split(",");
                List<Genre> selectedGenres = new ArrayList<>();
                for(String current : values)
                {
                    int id = Integer.parseInt(current);
                    selectedGenres.add(this.genres.get(id));
                }
                for(MovieDb current : movies)
                {
                    if(current.getGenres().containsAll(selectedGenres))
                        result.add(current);
                }
            }
            case NAME -> {
                value = value.replaceAll(" ","");
                for(MovieDb current : movies)
                    if(current.getTitle().replaceAll(" ","").toLowerCase().contains(value.toLowerCase()))
                        result.add(current);
            }
        }
        if(result.size() == 0)
            throw new FilmNotFoundException("Result Set is Empty");
        return result;
    }
    /**
     * Effettua una ricerca utilizzando la API
     * @param value Il genere o il nome da utilizzare nella ricerca
     * @param page La pagina da cercare
     * @param movieSortType Il tipo di sort da utilizzare nella ricerca
     * @param movieFilterType Il filtro da utilizzare nella ricerca
     * @param movieSortOrder Come ordinare i film, crescente o decrescente
     * @return Una MovieResultsPage contenente i risultati della ricerca
     * @throws FilmNotFoundException Viene generato se la lista dei risultati è vuota
     */
    public MovieResultsPage makeSearch(String value,int page, MovieSortType movieSortType, MovieFilterType movieFilterType, MovieSortOrder movieSortOrder) throws FilmNotFoundException
    {
        if(tmdbApi == null)
            this.init();
        MovieResultsPage result = new MovieResultsPage();
        switch (movieFilterType)
        {
            case NAME -> result = tmdbApi.getSearch().searchMovie(value,null,StyleHandler.getInstance().getCurrentLanguage().toString(),false,page);
            case GENRE-> result = tmdbApi.getDiscover().getDiscover(page,StyleHandler.getInstance().getCurrentLanguage().toString(),movieSortType != null ? movieSortType.name().toLowerCase() + "." + movieSortOrder.name().toLowerCase() : "",false,0,0,1000,0,value.isEmpty() ? "" : value,"","","","","");
        }
        if(result == null || result.getResults().isEmpty())
            throw new FilmNotFoundException("An error has occured,film not found");
        return result;
    }
    /**
     * Ottiene il path del poster completo del poster di un film
     * @param movieDb Il film di cui bisogna cercare il poster
     * @return Il path completo al poster
     */
    public String getPosterPath(MovieDb movieDb)
    {
        return (movieDb.getPosterPath() == null || movieDb.getPosterPath().isEmpty()) ? MainApplication.class.getResource("images" + "/" + "notfound.png").toExternalForm() : defaultPath + movieDb.getPosterPath();
    }
    /**
     * Cambia il valore del film selezionato attualmente
     * @param id Nuovo id del film da selezionare
     */
    public void selectFilm(int id)
    {
        //this.currentSelectedFilm = movies.getMovie(id,StyleHandler.getInstance().getCurrentLanguage().toString(), TmdbMovies.MovieMethod.images, TmdbMovies.MovieMethod.credits);
        this.currentSelectedFilm = id;
    }
    /**
     *
     * @param filmID L'id del film da cercare
     * @param error Callback per eventuali errori
     * @param success Callback per gestire il successo della operazione
     */
    public void filmQuery(int filmID, Consumer<Throwable> error, Consumer<MovieDb> success)
    {
        movieDbService.setOnFailed(workerStateEvent -> error.accept(workerStateEvent.getSource().getException()));
        movieDbService.setOnSucceeded(workerStateEvent -> success.accept((MovieDb) workerStateEvent.getSource().getValue()));
        movieDbService.setMovieId(filmID);
        movieDbService.restart();
    }
    /**
     * Aggiorna la libreria caricata attualmente usando una JSONArray
     * @param jsonArray JSONArray contenente i film
     */
    public void loadMovies(JSONArray jsonArray)
    {
        this.currentLoaded.clear();
        movieElementId.clear();
        for(int i = 0;i < jsonArray.length();i++)
        {
            JSONObject current = jsonArray.getJSONObject(i);
            int id = current.getInt("filmId");
            MovieDb movieDb = movies.getMovie(id,StyleHandler.getInstance().getCurrentLanguage().toString(), TmdbMovies.MovieMethod.credits, TmdbMovies.MovieMethod.images);
            movieElementId.put(movieDb,current.getString("element_id"));
            currentLoaded.add(movieDb);
        }
    }
    public final int getCurrentSelectedFilm() {return currentSelectedFilm;}
    public List<Genre> getValues(){
        return genres;
    }
    public List <String> getGenres() {
        List<String> values = new ArrayList<>();
        for(Genre current : genres)
            values.add(current.getName());
        return values;
    }
    public void setGenres(List<Genre> genres){
        this.genres = genres;
    }
    public final Map<MovieDb,String> getMovieElementId() {return movieElementId;}
    public final List<MovieDb> getCurrentLoaded() {return currentLoaded;}
    public static FilmHandler getInstance() {return instance;}

    private class MovieQueryService extends Service<MovieDb>
    {
        private int filmId;

        public void setMovieId(int filmId)
        {
            this.filmId = filmId;
        }
        @Override
        protected Task<MovieDb> createTask()
        {
            return new Task<>()
            {
                @Override
                protected MovieDb call()
                {
                    return movies.getMovie(filmId, StyleHandler.getInstance().getCurrentLanguage().toString(), TmdbMovies.MovieMethod.images, TmdbMovies.MovieMethod.credits);
                }
            };
        }
    }
    private class MovieGenreService extends Service<Void>
    {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {
                    FilmHandler.getInstance().setGenres(tmdbApi.getGenre().getGenreList(StyleHandler.getInstance().getCurrentLanguage().toString()));
                    return null;
                }
            };
        }
    }
}
