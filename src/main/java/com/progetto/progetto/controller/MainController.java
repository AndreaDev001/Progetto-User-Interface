package com.progetto.progetto.controller;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.view.SceneHandler;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainController
{
    @FXML
    private HBox homeBox;
    @FXML
    private HBox libraryBox;
    @FXML
    private HBox settingsBox;
    @FXML
    private HBox quitBox;
    @FXML
    private VBox genreHolder;
    @FXML
    private FlowPane flowPane;
    @FXML
    private TextField searchField;

    private final List<MovieDb> currentLoaded = new ArrayList<>();
    private final List<Integer> integerList = new ArrayList<>();
    private final List<HBox> topBoxes = new ArrayList<>();
    private final List<HBox> leftBoxes = new ArrayList<>();
    private Label currentLabel;

    @FXML
    private void initialize()
    {
        currentLabel = (Label)homeBox.getChildren().get(0);
        currentLabel.setUnderline(true);
        quitBox.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> SceneHandler.getInstance().loadLoginScene());
        settingsBox.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> SceneHandler.getInstance().loadSettingsScene());

        for(int i = 0;i < 10;i++)
        {
            List<MovieDb> result = FilmHandler.getInstance().getMovies(i,MovieListType.MOST_POPULAR,"en","eu");
            for(MovieDb current : result)
            {
                if(currentLoaded.contains(current))
                    continue;
                currentLoaded.add(current);
            }
            createFilms(currentLoaded);
        }

        this.searchField.addEventHandler(KeyEvent.KEY_PRESSED,(e) -> {
            if(e.getCode() != KeyCode.ENTER)
                return;
            List<MovieDb> filteredMovies = FilmHandler.getInstance().filterMovies(this.searchField.getText(),"en",currentLoaded,MovieFilterType.NAME,true);
            flowPane.getChildren().clear();
            integerList.clear();
            createFilms(filteredMovies);
        });
        initBoxes();
        initLabelHolder(homeBox,true);
        initLabelHolder(libraryBox,true);
        initLabelHolder(settingsBox,true);
        initLabelHolder(quitBox,true);
    }
    private void initBoxes()
    {
        topBoxes.add(homeBox);
        topBoxes.add(libraryBox);
        topBoxes.add(settingsBox);
        topBoxes.add(quitBox);
        initLabelHolder(homeBox,true);
        initLabelHolder(libraryBox,true);
        initLabelHolder(settingsBox,true);
        initLabelHolder(quitBox,true);
        Set<String> genres = FilmHandler.getInstance().getGenres();
        for(String current : genres)
        {
            HBox box = createGenreBox(current);
            genreHolder.getChildren().add(box);
            leftBoxes.add(box);
        }
    }
    private HBox createGenreBox(String genreName)
    {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setMinWidth(Region.USE_COMPUTED_SIZE);
        box.setMinHeight(Region.USE_COMPUTED_SIZE);
        box.setPrefWidth(Region.USE_COMPUTED_SIZE);
        box.setPrefHeight(100);
        box.setMaxWidth(Region.USE_COMPUTED_SIZE);
        box.setMaxHeight(Region.USE_COMPUTED_SIZE);
        Label label = new Label(genreName);
        label.getStyleClass().add("leftLabels");
        initLabelHolder(box,false);
        box.getChildren().add(label);
        return box;
    }
    private void initLabelHolder(HBox current,boolean top)
    {
        current.addEventHandler(MouseEvent.MOUSE_ENTERED,(e) -> {
            current.setStyle("-fx-border-width: 1px;-fx-border-color: white;-fx-cursor: hand");
        });
        current.addEventHandler(MouseEvent.MOUSE_EXITED,(e) -> {
            current.setStyle("-fx-border-width: 0px;-fx-border-color: white;-fx-cursor: hand");
        });
        if(top)
            current.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> {
                currentLabel.setUnderline(false);
                currentLabel = (Label)current.getChildren().get(0);
                currentLabel.setUnderline(true);
            });
        else
            current.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> {
                String value = ((Label)current.getChildren().get(0)).getText();
                List<MovieDb> result = FilmHandler.getInstance().filterMovies(value,"en",currentLoaded,MovieFilterType.GENRE,true);
                flowPane.getChildren().clear();
                integerList.clear();
                createFilms(result);
            });
    }
    private void createFilms(List<MovieDb> movieDbs)
    {
        for(MovieDb current : movieDbs)
        {
            try
            {
                String path = FilmHandler.getInstance().getDefaultPath() +  current.getPosterPath();
                VBox vBox = CacheHandler.getInstance().getFilmBox(current.getId(),current.getTitle(),current.getReleaseDate(),"en",path);
                if(integerList.contains(current.getId()))
                    continue;
                integerList.add(current.getId());
                flowPane.getChildren().add(vBox);
            }catch (NullPointerException | IndexOutOfBoundsException exception)
            {

            }
        }
    }
}