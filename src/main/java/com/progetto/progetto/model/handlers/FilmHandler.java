package com.progetto.progetto.model.handlers;

import com.progetto.progetto.MainApplication;
import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortOrder;
import com.progetto.progetto.model.enums.MovieSortType;
import com.progetto.progetto.model.exceptions.FilmNotFoundException;
import com.progetto.progetto.view.StyleHandler;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
    private final TmdbApi tmdbApi;
    private final TmdbMovies movies;
    private final String defaultPath;
    private int currentSelectedFilm = 0;
    private List<MovieDb> currentLoaded = new Vector<>();
    private Map<MovieDb,String> movieElementId = new HashMap<>();
    private boolean requiresUpdate = true;

    private MovieQueryService movieDbService = new MovieQueryService();

    private FilmHandler()
    {
        System.out.println("Instance of Film Handler created correctly");
        String apiKey = "3837271101e801680438310f38a3feff";
        tmdbApi = new TmdbApi(apiKey);
        movies = tmdbApi.getMovies();
        defaultPath = "https://image.tmdb.org/t/p/w500";
    }

    /**
     * Returns a MovieResultsPage containing a pre-made list of movies
     * @param page The number of the page to load,the movies are divided in multiple pages
     * @param movieListType The list type to use in the search
     * @return MovieResultsPage contains a List of movies
     * @throws FilmNotFoundException If the size of the result is equal to zero throws an Exception
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
     * Used to reload genres using the API,useful when language is changed
     */
    public void updateGenres()
    {
        List<Genre> genres = tmdbApi.getGenre().getGenreList(StyleHandler.getInstance().getCurrentLanguage().toString());
        this.genres = genres;
    }

    /**
     * Method used to sort a list of movies
     * @param values Movies to sort
     * @param movieSortType Sort Type used to sort the movies
     * @param movieSortOrder Sort Order used when sorting,ascended or descended
     * @return The sorted List
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
            case REVENUE -> result = values.stream().sorted(movieSortOrder == MovieSortOrder.DESC ? Comparator.comparing(MovieDb::getRevenue).reversed() : Comparator.comparing(MovieDb::getRevenue)).toList();
        }
        return result;
    }

    /**
     * Method used to filter a movie list by genre or by search
     * @param movies Movies to filter
     * @param movieFilterType Filter Type used to filter the movies,it's either by GENRE or by NAME
     * @param value The current Genre or the current Name
     * @return The filtered List
     * @throws FilmNotFoundException If the result size is equal to zero,throws an exception
     */
    public List<MovieDb> filterMovies(List<MovieDb> movies,MovieFilterType movieFilterType,String value) throws FilmNotFoundException
    {
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
                for(MovieDb current : movies)
                    if(current.getTitle().contains(value))
                        result.add(current);
            }
        }
        if(result.size() == 0)
            throw new FilmNotFoundException("Result Set is Empty");
        return result;
    }

    /**
     * Method used to perform a search using the MovieDB API Wrapper
     * @param value The current Genre or the current name
     * @param page The number of the page to look for
     * @param movieSortType The sort type to use in the search
     * @param movieFilterType The filter type to use in the search
     * @param movieSortOrder The sort order to use in the sorting
     * @return A MovieResultsPage containing the movie list
     * @throws FilmNotFoundException If the result size is equal to zero,return an exception
     */
    public MovieResultsPage makeSearch(String value,int page, MovieSortType movieSortType, MovieFilterType movieFilterType, MovieSortOrder movieSortOrder) throws FilmNotFoundException
    {
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
     * Method used to get the poster path of a film,defaultPath + moviePath
     * @param movieDb Which movie we need to find the poster for
     * @return A String containing the web URL of the poster
     */
    public String getPosterPath(MovieDb movieDb)
    {
        return (movieDb.getPosterPath() == null || movieDb.getPosterPath().isEmpty()) ? MainApplication.class.getResource("images" + "/" + "notfound.png").toExternalForm() : defaultPath + movieDb.getPosterPath();
    }

    /**
     * Method used to update current selected film,called when the user clicks on a film card
     * @param id The id of the movie
     */
    public void selectFilm(int id)
    {
        //this.currentSelectedFilm = movies.getMovie(id,StyleHandler.getInstance().getCurrentLanguage().toString(), TmdbMovies.MovieMethod.images, TmdbMovies.MovieMethod.credits);
        this.currentSelectedFilm = id;
    }
    public void filmQuery(int filmID, Consumer<Throwable> error, Consumer<MovieDb> success)
    {
        movieDbService.setOnFailed(workerStateEvent -> error.accept(workerStateEvent.getSource().getException()));
        movieDbService.setOnSucceeded(workerStateEvent -> success.accept((MovieDb) workerStateEvent.getSource().getValue()));
        movieDbService.setMovieId(filmID);
        movieDbService.restart();
    }

    /**
     * Loads movies from a json array,method used when loading the library of a user
     * @param jsonArray The JSONArray containg the movies
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
        this.requiresUpdate = false;
    }

    /**
     * Method used to update if the FilmHandlers need to reload the current loaded library
     * @param value If we need to reload the current loaded library
     */
    public void setRequiresUpdate(boolean value)
    {
        this.requiresUpdate = value;
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
    public final Map<MovieDb,String> getMovieElementId() {return movieElementId;}
    public final boolean RequiresUpdate() {return requiresUpdate;}
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

}
