package com.progetto.progetto.model.handlers;

import com.progetto.progetto.MainApplication;
import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortOrder;
import com.progetto.progetto.model.enums.MovieSortType;
import com.progetto.progetto.model.exceptions.FilmNotFoundException;
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
    public List<MovieDb> getMovies(int page, MovieListType movieListType,String language,String region) throws FilmNotFoundException
    {
        MovieResultsPage movieDbs = null;
        switch (movieListType)
        {
            case MOST_POPULAR ->  movieDbs = movies.getPopularMovies(language,page);
            case UPCOMING_MOVIES -> movieDbs = movies.getUpcoming(language,page,region);
            case TOP_RATED_MOVIES -> movieDbs = movies.getTopRatedMovies(language,page);
        }
        if(movieDbs == null || movieDbs.getResults().size() == 0)
            throw new FilmNotFoundException("An error has occured,result is empty");
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
            case ORIGINAL_TITLE -> result = values.stream().sorted(Comparator.comparing(MovieDb::getTitle)).toList();
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
            case VOTE_AVERAGE -> result = values.stream().sorted(Comparator.comparing(MovieDb::getVoteAverage).reversed()).toList();
            case POPULARITY -> result = values.stream().sorted(Comparator.comparing(MovieDb::getPopularity).reversed()).toList();
            case VOTE_COUNT -> result = values.stream().sorted(Comparator.comparing(MovieDb::getVoteCount).reversed()).toList();
        }
        return result;
    }
    public List<MovieDb> makeSearch(String value, String language, MovieSortType movieSortType, MovieFilterType movieFilterType, MovieSortOrder movieSortOrder) throws FilmNotFoundException
    {
        List<MovieDb> result = new ArrayList<>();
        switch (movieFilterType)
        {
            case NAME -> result = tmdbApi.getSearch().searchMovie(value,null,language,false,1).getResults();
            case SINGLE_GENRE -> result = tmdbApi.getDiscover().getDiscover(1,language,movieSortType.name().toLowerCase() + "." + movieSortOrder.name().toLowerCase(),false,0,0,0,0,value.isEmpty() ? "" : String.valueOf(stringGenreMap.get(value).getId()),"","","","","").getResults();
            case MULTIPLE_GENRES -> {
                String[] values = value.split(",");
                StringBuilder builder = new StringBuilder();
                for(int i = 0;i < values.length;i++)
                {
                    int id = stringGenreMap.get(values[i]).getId();
                    builder.append(id);
                    if(i != values.length - 1)
                        builder.append(",");
                }
                result =  tmdbApi.getDiscover().getDiscover(1,language,movieSortType.name().toLowerCase() + "." + movieSortOrder.name().toLowerCase(),false,0,0,0,0,builder.toString(),"","","","","").getResults();
            }
        }
        if(result == null || result.isEmpty())
            throw new FilmNotFoundException("An error has occured,film not found");
        return result;
    }
    public String getPosterPath(MovieDb movieDb)
    {
        return (movieDb.getPosterPath() == null || movieDb.getPosterPath().isEmpty()) ? MainApplication.class.getResource("images" + "/" + "notfound.png").toExternalForm() : defaultPath + movieDb.getPosterPath();
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
}
