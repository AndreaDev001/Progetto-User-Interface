package com.progetto.progetto.model.handlers;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.*;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

import java.util.ArrayList;
import java.util.List;

public class FilmHandler
{
    private static FilmHandler instance = new FilmHandler();
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
            result.add(current);
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
        switch (filterType)
        {
            case GENRE -> {
                for(MovieDb current : movieDbs)
                {
                    List<Genre> genres = getMovieGenres(current.getId(),language);
                    if(genres == null)
                        throw new NullPointerException();
                    for(Genre genre : genres)
                        if(genre.getName().equals(value))
                        {
                            result.add(current);
                            break;
                        }
                }
            }
            case NAME -> {
                for(MovieDb current : movieDbs)
                {
                    if(current.getTitle().contains(value))
                        result.add(current);
                }
            }
        }
        return result;
    }
    public static FilmHandler getInstance() {return instance;}
    public final String getApiKey() {return apiKey;}
    public final TmdbApi getTmdbApi() {return tmdbApi;}
    public final TmdbMovies getMovies() {return movies;}
    public final String getDefaultPath() {return defaultPath;}
}
