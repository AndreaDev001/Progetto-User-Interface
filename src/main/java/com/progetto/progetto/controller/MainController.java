package com.progetto.progetto.controller;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.records.Film;
import com.progetto.progetto.view.SceneHandler;
import info.movito.themoviedbapi.TmdbGenre;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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
        TmdbMovies movies = new TmdbApi("3837271101e801680438310f38a3feff").getMovies();;
        for(int i = 0;i < 5;i++)
        {
            List<MovieDb> result = FilmHandler.getInstance().getMovies(i,MovieListType.MOST_POPULAR,"en","eu");
            List<MovieDb> x = FilmHandler.getInstance().filterMovies("Action","en",result,MovieFilterType.GENRE);
            createFilms(x);
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
    private void createFilms(List<MovieDb> movieDbs)
    {
        for(MovieDb current : movieDbs)
        {
            if(current.getGenres() != null)
                System.out.println(current.getGenres().get(0).getName());
            String path = FilmHandler.getInstance().getMovieImageURL(current.getId(),"en",ArtworkType.POSTER,0);
            if(path.equals(""))
                continue;
            VBox vBox = new VBox();
            ImageView imageView = new ImageView(new Image(path,true));
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
            vBox.setStyle("-fx-cursor: hand");
            vBox.getChildren().add(imageView);
            vBox.getChildren().add(label);
            vBoxes.add(vBox);
            flowPane.getChildren().add(vBox);
        }
    }
}

