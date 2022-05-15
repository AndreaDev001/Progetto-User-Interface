package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.CacheHandler;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class FilmContainer extends FlowPane
{
    private List<MovieDb> movies;

    public FilmContainer(List<MovieDb> movies)
    {
        this.movies = movies;
        this.getStyleClass().add("test");
        this.setHgap(5);
        this.setVgap(5);
        this.init();
    }
    private void init()
    {
        for(MovieDb current : movies)
        {
            FilmCard filmCard = CacheHandler.getInstance().getFilmBox(current);
            this.getChildren().add(filmCard);
        }
    }
    public void setMovies(List<MovieDb> movies)
    {
        this.movies = movies;
        this.init();
    }
    public List<MovieDb> getMovies() {return movies;}
}
