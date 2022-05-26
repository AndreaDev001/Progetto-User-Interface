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
import info.movito.themoviedbapi.model.*;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FilmHandler
{
    private static final FilmHandler instance = new FilmHandler();
    private List<Genre> genres = new ArrayList<>();
    private final TmdbApi tmdbApi;
    private final TmdbMovies movies;
    private final String defaultPath;
    private MovieDb currentSelectedFilm;

    private FilmHandler()
    {
        System.out.println("Instance of Film Handler created correctly");
        String apiKey = "3837271101e801680438310f38a3feff";
        tmdbApi = new TmdbApi(apiKey);
        movies = tmdbApi.getMovies();
        defaultPath = "https://image.tmdb.org/t/p/w500";
    }
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
    public void updateGenres()
    {
        List<Genre> genres = tmdbApi.getGenre().getGenreList(StyleHandler.getInstance().getCurrentLanguage().toString());
        this.genres = genres;
    }
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
                    return secondDate.compareTo(firstDate);
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
    public List<MovieDb> filterMovies(List<MovieDb> movies,MovieFilterType movieFilterType,String value) throws FilmNotFoundException
    {
        if(value.length() == 0)
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
                if(result.size() == 0)
                    throw new FilmNotFoundException("Result Set is Empty");
            }
            case NAME -> {
                for(MovieDb current : movies)
                    if(current.getTitle().contains(value))
                        result.add(current);
            }
        }
        return result;
    }
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
    public String getPosterPath(MovieDb movieDb)
    {
        return (movieDb.getPosterPath() == null || movieDb.getPosterPath().isEmpty()) ? MainApplication.class.getResource("images" + "/" + "notfound.png").toExternalForm() : defaultPath + movieDb.getPosterPath();
    }
    public MovieDb getMovie(int filmId) {
        return movies.getMovie(filmId,StyleHandler.getInstance().getCurrentLanguage().toString(), TmdbMovies.MovieMethod.credits, TmdbMovies.MovieMethod.images);
    }
    public void selectFilm(int id)
    {
        this.currentSelectedFilm = movies.getMovie(id,StyleHandler.getInstance().getCurrentLanguage().toString(), TmdbMovies.MovieMethod.images, TmdbMovies.MovieMethod.credits);
    }
    public final MovieDb getCurrentSelectedFilm() {return currentSelectedFilm;}
    public List<Genre> getValues(){
        return genres;
    }
    public List <String> getGenres() {
        List<String> values = new ArrayList<>();
        for(Genre current : genres)
            values.add(current.getName());
        return values;
    }
    public static FilmHandler getInstance() {return instance;}

}
