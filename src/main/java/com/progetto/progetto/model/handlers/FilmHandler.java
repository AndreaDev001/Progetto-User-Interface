package com.progetto.progetto.model.handlers;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortType;
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
    private final Map<String,Genre> stringGenreMap = new HashMap<>();
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
    public List<MovieDb> getMovies(int page, MovieListType movieListType,String language,String region) throws NullPointerException
    {
        MovieResultsPage movieDbs = null;
        switch (movieListType)
        {
            case MOST_POPULAR ->  movieDbs = movies.getPopularMovies(language,page);
            case UPCOMING_MOVIES -> movieDbs = movies.getUpcoming(language,page,region);
            case TOP_RATED_MOVIES -> movieDbs = movies.getTopRatedMovies(language,page);
        }
        if(movieDbs == null)
            throw new NullPointerException();
        List<Genre> genres = tmdbApi.getGenre().getGenreList(language);
        List<MovieDb> result = new ArrayList<>(movieDbs.getResults());
        for(Genre current : genres)
            stringGenreMap.put(current.getName(),current);
        return result;
    }
    public List<MovieDb> sortMovies(List<MovieDb> values, MovieSortType movieSortType)
    {
        List<MovieDb> result = new ArrayList<>();
        switch (movieSortType)
        {
            case NAME -> result = values.stream().sorted(Comparator.comparing(MovieDb::getTitle)).toList();
            case RELEASE -> result = values.stream().sorted((o1,o2) ->
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try
                {
                    Date firstDate = format.parse(o1.getReleaseDate());
                    Date secondDate = format.parse(o2.getReleaseDate());
                    if(firstDate == null || secondDate == null)
                        throw new NullPointerException();
                    return secondDate.compareTo(firstDate);
                }
                catch (ParseException | NullPointerException exception)
                {
                    return -1;
                }
            }).toList();
            case RATING  -> result = values.stream().sorted(Comparator.comparing(MovieDb::getVoteAverage).reversed()).toList();
        }
        return result;
    }
    public List<MovieDb> filterMovies(String value, String language, MovieFilterType filterType,boolean includeAdult) throws NullPointerException
    {
        List<MovieDb> result = new ArrayList<>();
        if(value == null)
            return result;
        switch (filterType)
        {
            case GENRE -> {
                Genre genre = stringGenreMap.get(value);
                MovieResultsPage movieResultsPage = tmdbApi.getGenre().getGenreMovies(genre.getId(),language,1,includeAdult);
                return movieResultsPage.getResults();
            }
            case NAME -> {
                MovieResultsPage movieResultsPage = tmdbApi.getSearch().searchMovie(value,null,language,includeAdult,1);
                return movieResultsPage.getResults();
            }
        }
        return result;
    }
    public MovieDb getMovie(int id,String language)
    {
        return tmdbApi.getMovies().getMovie(id,language, TmdbMovies.MovieMethod.images);
    }
    public void selectFilm(int id,String language)
    {
        this.currentSelectedFilm = movies.getMovie(id,language, TmdbMovies.MovieMethod.images, TmdbMovies.MovieMethod.credits);
    }
    public final MovieDb getCurrentSelectedFilm() {return currentSelectedFilm;}
    public Set<String> getGenres() {return stringGenreMap.keySet();}
    public static FilmHandler getInstance() {return instance;}
    public final String getDefaultPath() {return defaultPath;}
}
