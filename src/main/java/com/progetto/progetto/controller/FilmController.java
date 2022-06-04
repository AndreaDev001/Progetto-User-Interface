package com.progetto.progetto.controller;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.client.util.JSONUtil;
import com.progetto.progetto.model.enums.MovieViewMode;
import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.handlers.ProfileHandler;
import com.progetto.progetto.model.handlers.ResearchHandler;
import com.progetto.progetto.model.records.Film;
import com.progetto.progetto.view.SceneHandler;
import com.progetto.progetto.view.StyleHandler;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.ProductionCountry;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

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
    @FXML
    private Button addToLibrary;
    @FXML
    private ProgressIndicator filmProgress;
    @FXML
    private BorderPane borderPane;

    private String title;
    private int id;
    private String elementId;

    @FXML
    private void initialize()
    {
        String pattern = "###,###.###";
        id = FilmHandler.getInstance().getCurrentSelectedFilm();

        borderPane.setVisible(false);
        FilmHandler.getInstance().filmQuery(id, Throwable::printStackTrace, film ->
        {
            borderPane.setVisible(true);
            filmProgress.setVisible(false);

            title = film.getTitle();
            addToLibrary.setGraphic(StyleHandler.getInstance().createIcon("mdi2l-library-shelves",22));
            addToLibrary.disableProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNull());
            addToLibrary.setText(addToLibrary.isDisable() ? StyleHandler.getInstance().getResourceBundle().getString("libraryError.name") : StyleHandler.getInstance().getResourceBundle().getString("addToLibrary.name"));
            addToLibrary.disableProperty().addListener((observableValue, aBoolean, t1) -> addToLibrary.setText(observableValue.getValue().booleanValue() ? StyleHandler.getInstance().getResourceBundle().getString("libraryError.name") : StyleHandler.getInstance().getResourceBundle().getString("addToLibrary.name")));
            if(!addToLibrary.isDisable())
            {
                List<MovieDb> movies = FilmHandler.getInstance().getCurrentLoaded();
                boolean contains = movies.contains(film);
                addToLibrary.setOnAction((event) ->
                {
                    if(movies.contains(film))
                    {
                        elementId = FilmHandler.getInstance().getMovieElementId().get(film);
                        RemoveFilm();
                    }
                    else
                        AddFilm();
                    SceneHandler.getInstance().getFilmStage().close();
                });
                addToLibrary.setText(contains ? StyleHandler.getInstance().getResourceBundle().getString("alreadyAdded.name") : StyleHandler.getInstance().getResourceBundle().getString("addToLibrary.name"));
            }
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            String releaseDate = film.getReleaseDate().isEmpty() ? StyleHandler.getInstance().getResourceBundle().getString("missingRelease.name") : film.getReleaseDate();
            String overview = film.getOverview().isEmpty() ? StyleHandler.getInstance().getResourceBundle().getString("missingOverview.name") : film.getOverview();
            float rating = film.getVoteAverage();
            long budget = film.getBudget();
            long revenue = film.getRevenue();
            float popularity = film.getPopularity();
            int runtime = film.getRuntime();
            String path = FilmHandler.getInstance().getPosterPath(film);
            filmImage.setImage(CacheHandler.getInstance().getImage(path));
            filmPopularity.setGraphic(StyleHandler.getInstance().createIcon("mdi2a-account-group",22));
            filmNameTop.setGraphic(StyleHandler.getInstance().createIcon("mdi2m-movie",22));
            filmRuntime.setGraphic(StyleHandler.getInstance().createIcon("mdi2c-clock-time-three",22));
            filmReleaseDate.setGraphic(StyleHandler.getInstance().createIcon("mdi2c-calendar",22));
            filmRating.setGraphic(StyleHandler.getInstance().createIcon("mdi2v-vote",22));
            filmNameLeft.setGraphic(StyleHandler.getInstance().createIcon("mdi2m-movie",22));
            filmNameTop.setText(title);
            filmNameLeft.setText(title);
            filmReleaseDate.setText(releaseDate);
            filmRating.setText(StyleHandler.getInstance().getResourceBundle().getString("rating.name") + ":" + " " + String.valueOf(rating));
            filmDescription.setText(overview);
            filmBudget.setText(StyleHandler.getInstance().getResourceBundle().getString("filmBudget.name") + ":" + " " + (budget > 0 ? decimalFormat.format(budget) : "-"));
            filmRevenue.setText(StyleHandler.getInstance().getResourceBundle().getString("filmRevenue.name") + ":" + " " + (revenue > 0 ? decimalFormat.format(revenue) : "-"));
            filmPopularity.setText(StyleHandler.getInstance().getResourceBundle().getString("popularity.name") + ":" + " " + String.valueOf(popularity));
            filmRuntime.setText(StyleHandler.getInstance().getResourceBundle().getString("filmRuntime.name") + ":" + " " + (runtime > 0 ? runtime + " " + "min" : "-"));
            createFlags(film);
        });

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
    private void AddFilm()
    {
        Film film = new Film(id,title);
        try
        {
            FilmHandler.getInstance().setRequiresUpdate(true);
            JSONObject object = JSONUtil.toJSON(film);
            Client.getInstance().insert("films",object,success -> System.out.println("success"),error -> System.out.println("error"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void RemoveFilm()
    {
        Client.getInstance().remove("films",elementId,workerStateEvent -> {
            FilmHandler.getInstance().setRequiresUpdate(true);
            ResearchHandler.getInstance().setCurrentViewMode(MovieViewMode.LIBRARY,true,false,true);
        },workerStateEvent -> {
            System.out.println("Failed to remove movie");
        });
    }
}
