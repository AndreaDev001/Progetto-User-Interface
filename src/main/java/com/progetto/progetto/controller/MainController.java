package com.progetto.progetto.controller;

import com.progetto.progetto.view.SceneHandler;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.MovieImages;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import info.movito.themoviedbapi.TmdbApi;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class MainController
{
    @FXML
    private Label filmsButton;
    @FXML
    private Label libraryButton;
    @FXML
    private Label settingsButton;
    @FXML
    private Label logoutButton;
    @FXML
    private FlowPane flowPane;
    @FXML
    private HBox firstHBox;
    @FXML
    private HBox secondHBox;
    @FXML
    private HBox thirdHBox;
    @FXML
    private HBox fourthHBox;

    private List<VBox> vBoxes = new ArrayList<>();

    @FXML
    private void initialize()
    {
        fourthHBox.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> SceneHandler.getInstance().loadLoginScene());
        TmdbMovies movies = new TmdbApi("3837271101e801680438310f38a3feff").getMovies();
        MovieResultsPage movieDbs = null;
        for(int i = 0;i < 10;i++)
        {
            movieDbs = movies.getPopularMovies("en",i);
            createFilms(movieDbs);
        }
        initLabelHolder(firstHBox);
        initLabelHolder(secondHBox);
        initLabelHolder(thirdHBox);
        initLabelHolder(fourthHBox);
    }
    void initLabelHolder(HBox current)
    {
        current.addEventHandler(MouseEvent.MOUSE_ENTERED,(e) -> {
            current.setStyle("-fx-border-width: 1px;-fx-border-color: white;-fx-cursor: hand");
        });
        current.addEventHandler(MouseEvent.MOUSE_EXITED,(e) -> {
            current.setStyle("-fx-border-width: 0px;-fx-border-color: white;-fx-cursor: hand");
        });
    }
    private void createFilms(MovieResultsPage movieDbs)
    {
        TmdbMovies movies = new TmdbApi("3837271101e801680438310f38a3feff").getMovies();
        for(MovieDb current : movieDbs.getResults())
        {
            VBox vBox = new VBox();
            MovieDb db = movies.getMovie(current.getId(),"en", TmdbMovies.MovieMethod.credits, TmdbMovies.MovieMethod.images);
            MovieImages movieImages = movies.getImages(db.getId(),"en");
            ImageView imageView = new ImageView();
            if(movieImages.getPosters().size() > 0)
            {
                Artwork artwork = movieImages.getPosters().get(0);
                imageView.setImage(new Image("https://image.tmdb.org/t/p/w500" + artwork.getFilePath(),true));
            }
            else
                continue;
            Label label = new Label(current.getTitle());
            vBox.setAlignment(Pos.CENTER);
            vBox.setPrefWidth(30);
            vBox.setPrefHeight(30);
            vBox.setMinWidth(Region.USE_COMPUTED_SIZE);
            vBox.setMinHeight(Region.USE_COMPUTED_SIZE);
            vBox.setMaxWidth(Region.USE_COMPUTED_SIZE);
            vBox.setMaxHeight(Region.USE_COMPUTED_SIZE);
            vBox.setFillWidth(true);
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imageView.setStyle("-fx-border-radius: 10px");
            vBox.setStyle("-fx-cursor: hand");
            vBox.getChildren().add(imageView);
            vBox.getChildren().add(label);
            vBoxes.add(vBox);
            flowPane.getChildren().add(vBox);
        }
    }
}

