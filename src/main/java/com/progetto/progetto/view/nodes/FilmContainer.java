package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.CacheHandler;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.scene.layout.FlowPane;

import java.util.List;

//Class used to contain multiple film cards,uses a flow Pane as layout
public class FilmContainer extends FlowPane
{
    private List<MovieDb> movies;
    private final boolean useCards;

    /**
     * Constructor of the FilmContainer class
     * @param movies The movies we need to create the cards for
     * @param useCards If we need to use the cards or a table view
     */
    public FilmContainer(List<MovieDb> movies,boolean useCards)
    {
        this.movies = movies;
        this.useCards = useCards;
        this.getStyleClass().add("flowPane");
        this.setHgap(5);
        this.setVgap(5);
        this.init();
    }

    /**
     * Method used to init the component
     */
    private void init()
    {
        if(useCards)
        {
            for(MovieDb current : movies)
            {
                FilmCard filmCard = CacheHandler.getInstance().getFilmBox(current);
                this.getChildren().add(filmCard);
            }
        }
    }

    /**
     * Method use to update the movies field
     * @param movies The new movies
     */
    public void setMovies(List<MovieDb> movies)
    {
        this.movies = movies;
        this.init();
    }
    public final boolean UsesCards() {return useCards;}
    public final List<MovieDb> getMovies() {return movies;}
}