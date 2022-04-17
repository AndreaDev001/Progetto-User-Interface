package com.progetto.progetto.model.handlers;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.*;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmHandler
{
    private static FilmHandler instance = new FilmHandler();
    private final Map<String,Genre> stringGenreMap = new HashMap<String,Genre>();
    private final Map<String,List<MovieDb>> movieDbMap = new HashMap<>();
    private final String apiKey;
    private final TmdbApi tmdbApi;
    private final TmdbMovies movies;
    private final String defaultPath;

    private FilmHandler()
    {
        System.out.println("Instance of Film Handler created correctly");
        apiKey = "3837271101e801680438310f38a3feff";
        tmdbApi = new TmdbApi(apiKey);
        movies = tmdbApi.getMovies();
        defaultPath = "https://image.tmdb.org/t/p/w500";
    }
    public List<MovieDb> getMovies(int page, MovieListType movieListType,String language,String region) throws NullPointerException
    {
        List<MovieDb> result = new ArrayList<MovieDb>();
        MovieResultsPage movieDbs = null;
        switch (movieListType)
        {
            case MOST_POPULAR ->  movieDbs = movies.getPopularMovies(language,page);
            case UPCOMING_MOVIES -> movieDbs = movies.getUpcoming(language,page,region);
            case TOP_RATED_MOVIES -> movieDbs = movies.getTopRatedMovies(language,page);
        }
        if(movieDbs == null)
            throw new NullPointerException();
        for(MovieDb current : movieDbs)
        {
            result.add(current);
            List<Genre> genres = getMovieGenres(current.getId(),language);
            for(Genre genre : genres)
            {
                stringGenreMap.put(genre.getName(),genre);
                List<MovieDb> list = movieDbMap.get(genre.getName());
                if(list == null)
                    list = new ArrayList<>();
                if(list.contains(current))
                    continue;
                list.add(current);
                movieDbMap.put(genre.getName(),list);
            }
        }
        return result;
    }
    public List<Genre> getMovieGenres(int movieId,String language) throws NullPointerException
    {
        return movies.getMovie(movieId,language).getGenres();
    }
    public List<Artwork> getMovieArtworks(int movieId,String language,ArtworkType artworkType) throws NullPointerException
    {
        List<Artwork> result = new ArrayList<>();
        MovieImages movieImages = movies.getImages(movieId,language);
        switch (artworkType)
        {
            case POSTER -> movieImages.getPosters();
            case PROFILE -> movieImages.getProfiles();
            case BACKDROP -> movieImages.getBackdrops();
        }
        return result;
    }
    public String getMovieImageURL(int movieId,String language,ArtworkType artworkType,int index) throws NullPointerException,IndexOutOfBoundsException
    {
        String result = "";
        MovieImages movieImages = movies.getImages(movieId,language);
        List<Artwork> artworks = new ArrayList<>();
        switch (artworkType)
        {
            case POSTER -> artworks = movieImages.getPosters();
            case PROFILE -> artworks = movieImages.getProfiles();
            case BACKDROP -> artworks = movieImages.getBackdrops();
        }
        if(artworks == null || artworks.size() == 0)
            throw new NullPointerException();
        if(index >= artworks.size())
            throw new IndexOutOfBoundsException();
        Artwork artwork = artworks.get(index);
        result = defaultPath + artwork.getFilePath();
        return result;
    }
    public List<MovieDb> filterMovies(String value, String language,List<MovieDb> movieDbs, MovieFilterType filterType) throws NullPointerException
    {
        List<MovieDb> result = new ArrayList<>();
        if(value == null)
            return result;
        switch (filterType)
        {
            case GENRE -> {
                result = movieDbMap.get(value);
            }
            case NAME -> {
                for(MovieDb current : movieDbs)
                {
                    String title = current.getTitle();
                    if(value.length() > 3)
                    {
                        if(title.contains(value))
                            result.add(current);
                    }
                    else
                    {
                        String x = title.substring(0,value.length());
                        if(value.equals(x))
                        {
                            result.add(current);
                            System.out.println(title);
                        }
                    }
                }
            }
        }
        return result;
    }
    public static FilmHandler getInstance() {return instance;}
    public final String getApiKey() {return apiKey;}
    public final Map<String,Genre> getStringGenreMap() {return stringGenreMap;}
    public final TmdbApi getTmdbApi() {return tmdbApi;}
    public final TmdbMovies getMovies() {return movies;}
    public final String getDefaultPath() {return defaultPath;}
}
