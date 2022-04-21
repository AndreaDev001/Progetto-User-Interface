package com.progetto.progetto.controller;

import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Reviews;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FilmController
{
    @FXML
    private ImageView filmImage;
    @FXML
    private Label filmNameTop;
    @FXML
    private Label filmNameLeft;
    @FXML
    private Label filmReleaseDate;
    @FXML
    private Label filmLanguage;
    @FXML
    private Label filmRating;
    @FXML
    private TextArea filmDescription;

    private String title;
    private String releaseDate;
    private String language;
    private String overview;
    private int rating;

    @FXML
    private void initialize()
    {
        MovieDb film = FilmHandler.getInstance().getCurrentSelectedFilm();
        title = film.getTitle();
        releaseDate = film.getReleaseDate();
        language = film.getOriginalLanguage();
        overview = film.getOverview();
        if(overview.isEmpty())
            overview = "No description found";
        float rating = film.getVoteAverage();
        String path = FilmHandler.getInstance().getDefaultPath() + film.getPosterPath();
        filmImage.setImage(CacheHandler.getInstance().getImage(path));
        filmNameTop.setText(title);
        filmNameLeft.setText(title);
        filmReleaseDate.setText(releaseDate);
        filmLanguage.setText(language);
        filmRating.setText(String.valueOf(rating));
        filmDescription.setText(overview);
    }
}
