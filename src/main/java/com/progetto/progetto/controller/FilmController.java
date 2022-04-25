package com.progetto.progetto.controller;

import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import info.movito.themoviedbapi.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class FilmController
{
    @FXML
    private ImageView filmImage;
    @FXML
    private HBox flagHolder;
    @FXML
    private Label filmNameTop;
    @FXML
    private Label filmNameLeft;
    @FXML
    private Label filmReleaseDate;
    @FXML
    private Label filmRating;
    @FXML
    private TextArea filmDescription;
    @FXML
    private Label filmBudget;
    @FXML
    private Label filmRevenue;
    @FXML
    private Label filmPopularity;
    @FXML
    private Label filmRuntime;

    private String title;
    private String releaseDate;
    private String language;
    private String overview;
    private long budget;
    private long revenue;
    private int rating;
    private float popularity;
    private int runtime;

    @FXML
    private void initialize()
    {
        MovieDb film = FilmHandler.getInstance().getCurrentSelectedFilm();
        title = film.getTitle();
        releaseDate = film.getReleaseDate().isEmpty() ? "Coming soon" : film.getReleaseDate();
        language = film.getOriginalLanguage();
        overview = film.getOverview().isEmpty() ? "No description found" : film.getOverview();
        float rating = film.getVoteAverage();
        budget = film.getBudget();
        revenue = film.getRevenue();
        popularity = film.getPopularity();
        runtime = film.getRuntime();
        String path = FilmHandler.getInstance().getPosterPath(film);
        filmImage.setImage(CacheHandler.getInstance().getImage(path));
        filmNameTop.setText(title);
        filmNameLeft.setText(title);
        filmReleaseDate.setText(releaseDate);
        filmRating.setText(String.valueOf(rating));
        filmDescription.setText(overview);
        filmBudget.setText("Budget:" + " " + (budget > 0 ? budget : "-"));
        filmRevenue.setText("Revenue:" + " " + (revenue > 0 ? revenue : "-"));
        filmPopularity.setText(String.valueOf(popularity));
        filmRuntime.setText("Runtime:" + " " + String.valueOf(runtime) + " " + "min");
        createFlags(film);
    }
    private void createFlags(MovieDb film)
    {
        //Si dovrebbe usare getTranslations(),ma anche quando si specifica MovieMethod.translations ritorna un 'mapping error',possibile fix fare la richiesta manualmente,questo è un placeholder"
        for(ProductionCountry current : film.getProductionCountries())
        {
            String isoCode = current.getIsoCode().toLowerCase();
            ImageView imageView = new ImageView(new Image("https://flagcdn.com/32x24/" + isoCode + ".png"));
            imageView.setFitWidth(30);
            imageView.setFitHeight(24);
            imageView.setSmooth(true);
            imageView.setPreserveRatio(true);
            flagHolder.getChildren().add(imageView);
        }
    }
}
