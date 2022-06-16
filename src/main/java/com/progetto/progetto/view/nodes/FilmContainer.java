package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.CacheHandler;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

public class FilmContainer extends FlowPane
{
    private List<MovieDb> movies;
    private final List<FilmCard> filmCards = new ArrayList<>();

    /**
     * Costruttore della classe Film Container
     * @param movies Film contenuti nel container
     */
    public FilmContainer(List<MovieDb> movies)
    {
        this.movies = movies;
        this.setHgap(5);
        this.setVgap(5);
        this.getStyleClass().add("flowPane");
        this.init();
        this.setFocusTraversable(false);
    }

    /**
     * Inizializza il componente
     */
    private void init()
    {
        for(MovieDb current : movies)
        {
            FilmCard filmCard = CacheHandler.getInstance().getFilmBox(current);
            this.getChildren().add(filmCard);
            this.filmCards.add(filmCard);
        }
    }

    /**
     * Aggiorna la lista movies e inizializza il componente
     * @param movies I nuovi film
     */
    public void setMovies(List<MovieDb> movies)
    {
        this.movies = movies;
        this.init();
    }

    public List<FilmCard> getFilmCards()
    {
        return filmCards;
    }
    public final List<MovieDb> getMovies() {return movies;}
}