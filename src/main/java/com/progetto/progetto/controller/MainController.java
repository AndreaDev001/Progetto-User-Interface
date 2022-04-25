package com.progetto.progetto.controller;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortType;
import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.handlers.ProfileHandler;
import com.progetto.progetto.model.records.Library;
import com.progetto.progetto.model.records.User;
import com.progetto.progetto.model.sql.SQLGetter;
import com.progetto.progetto.view.SceneHandler;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainController
{
    @FXML
    private Label homeBox;
    @FXML
    private Label libraryBox;
    @FXML
    private Label settingsBox;
    @FXML
    private Label quitBox;
    @FXML
    private VBox genreHolder;
    @FXML
    private VBox sortingHolder;
    @FXML
    private FlowPane flowPane;
    @FXML
    private TextField searchField;
    @FXML
    private Label expandSorting;
    @FXML
    private Label expandCategories;

    private List<MovieDb> currentLoaded = new ArrayList<>();
    private final List<Integer> integerList = new ArrayList<>();
    private static final BooleanProperty categoriesVisible = new SimpleBooleanProperty(false);
    private static final BooleanProperty sortingVisible = new SimpleBooleanProperty(false);
    private Label currentLabel;

    @FXML
    private void initialize()
    {
        currentLabel = homeBox;
        currentLabel.setUnderline(true);
        libraryBox.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> loadLibrary());
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
            List<MovieDb> filteredMovies = FilmHandler.getInstance().filterMovies(this.searchField.getText(),"en",MovieFilterType.NAME,false);
            flowPane.getChildren().clear();
            integerList.clear();
            currentLoaded = filteredMovies;
            createFilms(filteredMovies);
        });
        initBoxes();
    }
    private void initBoxes()
    {
        initLabel(homeBox,true,false);
        initLabel(libraryBox,true,false);
        initLabel(settingsBox,true,false);
        initLabel(quitBox,true,false);
        for(MovieSortType current : MovieSortType.values())
        {
            Label label = createLeftLabel(current.name().toLowerCase());
            label.addEventHandler(MouseEvent.MOUSE_CLICKED,mouseEvent -> {
                integerList.clear();
                flowPane.getChildren().clear();
                List<MovieDb> result = FilmHandler.getInstance().sortMovies(currentLoaded,current);
                currentLoaded = result;
                createFilms(result);
            });
            label.visibleProperty().bind(sortingVisible);
            label.managedProperty().bind(sortingVisible);
            sortingHolder.getChildren().add(label);
            initLabel(label,false,false);
        }
        Set<String> genres = FilmHandler.getInstance().getGenres();
        for(String current : genres)
        {
            Label label = createLeftLabel(current);
            label.visibleProperty().bind(categoriesVisible);
            label.managedProperty().bind(categoriesVisible);
            genreHolder.getChildren().add(label);
            initLabel(label,false,true);
        }
        expandCategories.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> categoriesVisible.set(!categoriesVisible.get()));
        expandSorting.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> sortingVisible.set(!sortingVisible.get()));
    }
    private Label createLeftLabel(String value)
    {
        Label result = new Label(value);
        result.setWrapText(true);
        result.setAlignment(Pos.CENTER_RIGHT);
        result.setMinWidth(Region.USE_COMPUTED_SIZE);
        result.setMinHeight(Region.USE_COMPUTED_SIZE);
        result.setPrefWidth(Region.USE_COMPUTED_SIZE);
        result.setPrefHeight(10);
        result.setMaxWidth(Double.MAX_VALUE);
        result.setMaxHeight(Region.USE_PREF_SIZE);
        result.getStyleClass().add("leftLabels");
        return result;
    }
    private void initLabel(Label current,boolean top,boolean filter)
    {
        current.addEventHandler(MouseEvent.MOUSE_ENTERED,mouseEvent -> current.setUnderline(true));
        current.addEventHandler(MouseEvent.MOUSE_EXITED,mouseEvent -> current.setUnderline(false));
        if(top)
            current.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> {
                currentLabel.setUnderline(false);
                currentLabel = current;
                currentLabel.setUnderline(true);
            });
        else if(filter)
            current.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> {
                String value = current.getText();
                List<MovieDb> result = FilmHandler.getInstance().filterMovies(value,"en",MovieFilterType.GENRE,false);
                flowPane.getChildren().clear();
                integerList.clear();
                currentLoaded = result;
                createFilms(result);
            });
    }
    private void createFilms(List<MovieDb> movieDbs)
    {
        for(MovieDb current : movieDbs)
        {
            String path = FilmHandler.getInstance().getPosterPath(current);
            VBox vBox = CacheHandler.getInstance().getFilmBox(current.getId(),current.getTitle(),current.getReleaseDate(),"en",path);
            if(integerList.contains(current.getId()))
                continue;
            integerList.add(current.getId());
            flowPane.getChildren().add(vBox);
        }
    }
    //Da testare e modificare
    private void loadLibrary()
    {
        System.out.println("Loading library");
        User user = ProfileHandler.getInstance().getLoggedUser();
        Library library = null;
        List<MovieDb> movieDbs = new ArrayList<>();
        try {
            ResultSet resultSet = SQLGetter.getInstance().makeQuery("SELECT * FROM LIBRARY WHERE id=?", user.username());
            if (resultSet.next())
                library = new Library(resultSet.getInt(1));
            ResultSet query = SQLGetter.getInstance().makeQuery("SELECT filmId FROM LIBRARY-FILM WHERE libraryId=?", library.id());
            while (query.next()) {
                int filmId = query.getInt(1);
                MovieDb current = FilmHandler.getInstance().getMovie(filmId, "en");
                movieDbs.add(current);
            }
        }
        catch (SQLException | NullPointerException e)
        {
            e.printStackTrace();
        }
        flowPane.getChildren().clear();
        integerList.clear();
        createFilms(movieDbs);
    }
}