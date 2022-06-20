package com.progetto.progetto.controller;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.client.util.JSONUtil;
import com.progetto.progetto.model.enums.ErrorType;
import com.progetto.progetto.model.enums.MovieViewMode;
import com.progetto.progetto.model.handlers.*;
import com.progetto.progetto.model.records.Film;
import com.progetto.progetto.view.SceneHandler;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.ProductionCountry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private Label filmOriginalLanguage;
    @FXML
    private Label filmRuntime;
    @FXML
    private Button addToLibrary;
    @FXML
    private ProgressIndicator filmProgress;
    @FXML
    private BorderPane borderPane;

    private MovieDb movie;
    private String title;
    private int id;
    private String elementId;

    @FXML
    private void initialize()
    {
        FilmHandler.getInstance().IsLibraryAvailable().addListener((obs,oldValue,newValue) -> Platform.runLater(this::initButton));
        String pattern = "###,###.###";
        id = FilmHandler.getInstance().getCurrentSelectedFilm();
        borderPane.setVisible(false);
        borderPane.addEventHandler(KeyEvent.KEY_PRESSED,(event) -> {
            if(event.getCode() == KeyCode.ESCAPE)
                SceneHandler.getInstance().getFilmStage().close();
        });
        FilmHandler.getInstance().filmQuery(id, error ->
        {
            LoggerHandler.error("Failed to retrive movie information using TMDB API, movie API: {}",error.getCause().fillInStackTrace(),id);
            SceneHandler.getInstance().createErrorMessage(ErrorType.CONNECTION);
            SceneHandler.getInstance().getFilmStage().close();
        }, film ->
        {
            this.movie = film;
            borderPane.setVisible(true);
            filmProgress.setVisible(false);
            title = film.getTitle();
            initButton();
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            String releaseDate = film.getReleaseDate() == null || film.getReleaseDate().isEmpty() || film.getStatus().equals("Planned") ? StyleHandler.getInstance().getLocalizedString("missingRelease.name") : film.getReleaseDate();
            String overview = film.getOverview().isEmpty() ? StyleHandler.getInstance().getLocalizedString("missingOverview.name") : film.getOverview();
            float rating = film.getVoteAverage();
            long budget = film.getBudget();
            long revenue = film.getRevenue();
            float popularity = film.getPopularity();
            int runtime = film.getRuntime();
            String path = FilmHandler.getInstance().getPosterPath(film);
            filmImage.setImage(CacheHandler.getInstance().getImage(path));
            filmNameTop.setText(title);
            filmNameLeft.setText(title);
            filmReleaseDate.setText(releaseDate);
            filmRating.setText(StyleHandler.getInstance().getLocalizedString("rating.name") + ":" + " " + String.valueOf(rating));
            filmDescription.setText(overview);
            filmBudget.setText((budget > 0 ? decimalFormat.format(budget) : "-"));
            filmRevenue.setText((revenue > 0 ? decimalFormat.format(revenue) : "-"));
            filmPopularity.setText(StyleHandler.getInstance().getLocalizedString("popularity.name") + ":" + " " + String.valueOf(popularity));
            filmRuntime.setText(StyleHandler.getInstance().getLocalizedString("filmRuntime.name") + ":" + " " + (runtime > 0 ? runtime + " " + "min" : "-"));
            filmOriginalLanguage.setText(StyleHandler.getInstance().getLocalizedString("filmOriginalLanguage.name")  + ":" + " " + film.getOriginalLanguage());
            createFlags(film);
        });
    }
    private void initButton()
    {
        if(FilmHandler.getInstance().IsLibraryAvailable().get())
            addToLibrary.setDisable(false);
        if(!Client.getInstance().isLogged().get())
            addToLibrary.setText(StyleHandler.getInstance().getLocalizedString("libraryError.name"));
        if(Client.getInstance().isLogged().get() && !FilmHandler.getInstance().IsLibraryAvailable().get())
            addToLibrary.setText(StyleHandler.getInstance().getLocalizedString("libraryLoading.name"));
        addToLibrary.disableProperty().addListener((observableValue, aBoolean, t1) -> addToLibrary.setText(observableValue.getValue().booleanValue() ? StyleHandler.getInstance().getLocalizedString("libraryError.name") : StyleHandler.getInstance().getLocalizedString("addToLibrary.name")));
        if(!addToLibrary.isDisable())
        {
            List<MovieDb> movies = FilmHandler.getInstance().getCurrentLoaded();
            boolean contains = movies.contains(movie);
            addToLibrary.setOnAction((event) ->
            {
                if(movies.contains(movie))
                {
                    elementId = FilmHandler.getInstance().getMovieElementId().get(movie);
                    RemoveFilm();
                }
                else
                    AddFilm();
                SceneHandler.getInstance().getFilmStage().close();
            });
            addToLibrary.setText(contains ? StyleHandler.getInstance().getLocalizedString("alreadyAdded.name") : StyleHandler.getInstance().getLocalizedString("addToLibrary.name"));
        }
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
        if(flagHolder.getChildren().isEmpty())
        {
            Label label = new Label(StyleHandler.getInstance().getLocalizedString("missingCountry.name"));
            label.setStyle("-fx-font-size: 14px;");
            flagHolder.getChildren().add(label);
        }
    }
    private void AddFilm()
    {
        Film film = new Film(id,title);
        try
        {
            JSONObject object = JSONUtil.toJSON(film);
            Client.getInstance().insert("films",object,success -> {
                FilmHandler.getInstance().getCurrentLoaded().add(movie);
                FilmHandler.getInstance().getMovieElementId().put(movie,success.result().getJSONObject("response").getString(

























































                        "element_id"));
            },error ->
            {
                LoggerHandler.error("Error library film {} couldn't be added to the library",error.fillInStackTrace(),film.title());
                SceneHandler.getInstance().createErrorMessage(ErrorType.CONNECTION);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void RemoveFilm()
    {
        Client.getInstance().remove("films",FilmHandler.getInstance().getMovieElementId().get(this.movie),workerStateEvent -> {
            FilmHandler.getInstance().getCurrentLoaded().remove(this.movie);
            FilmHandler.getInstance().getMovieElementId().remove(this.movie);
            if(ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.LIBRARY)
                 ResearchHandler.getInstance().search(false);
        },workerStateEvent -> {
            LoggerHandler.error("Failed to remove movie from {} library",workerStateEvent.getSource().getException().fillInStackTrace(),Client.getInstance().getEmail());
            SceneHandler.getInstance().createErrorMessage(ErrorType.CONNECTION);
        });
    }
}
