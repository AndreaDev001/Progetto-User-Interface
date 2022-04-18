package com.progetto.progetto.controller;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.records.Film;
import com.progetto.progetto.view.SceneHandler;
import info.movito.themoviedbapi.TmdbGenre;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import info.movito.themoviedbapi.TmdbApi;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private HBox actionBox;
    @FXML
    private HBox adventureBox;
    @FXML
    private HBox warBox;
    @FXML
    private HBox storyBox;
    @FXML
    private HBox comedyBox;
    @FXML
    private HBox drammaticBox;
    @FXML
    private HBox apocalypticBox;
    @FXML
    private FlowPane flowPane;
    @FXML
    private TextField searchField;

    private final List<MovieDb> currentLoaded = new ArrayList<>();
    private final List<Integer> integerList = new ArrayList<>();
    private Label currentLabel;

    @FXML
    private void initialize()
    {
        currentLabel = (Label)homeBox.getChildren().get(0);
        currentLabel.setUnderline(true);
        quitBox.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> SceneHandler.getInstance().loadLoginScene());
        for(int i = 0;i < 7;i++)
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
        searchField.setStyle("-fx-border-color: black");
        this.searchField.addEventHandler(KeyEvent.KEY_PRESSED,(e) -> {
            if(e.getCode() != KeyCode.ENTER)
                return;
            List<MovieDb> filteredMovies = FilmHandler.getInstance().filterMovies(this.searchField.getText(),"en",currentLoaded,MovieFilterType.NAME);
            flowPane.getChildren().clear();
            integerList.clear();
            createFilms(filteredMovies);
        });
        initLabelHolder(homeBox,true);
        initLabelHolder(libraryBox,true);
        initLabelHolder(settingsBox,true);
        initLabelHolder(quitBox,true);
        initLabelHolder(actionBox,false);
        initLabelHolder(adventureBox,false);
        initLabelHolder(warBox,false);
        initLabelHolder(storyBox,false);
        initLabelHolder(comedyBox,false);
        initLabelHolder(drammaticBox,false);
        initLabelHolder(apocalypticBox,false);
        initLeftLabel(actionBox,"Action");
        initLeftLabel(adventureBox,"Adventure");
        initLeftLabel(warBox,"War");
        initLeftLabel(storyBox,"History");
        initLeftLabel(comedyBox,"Comedy");
        initLeftLabel(apocalypticBox,"Apocalyptic");
    }
    void initLeftLabel(HBox current,String value)
    {
        current.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> {
            List<MovieDb> result = FilmHandler.getInstance().filterMovies(value,"en",currentLoaded,MovieFilterType.GENRE);
            flowPane.getChildren().clear();
            integerList.clear();
            createFilms(result);
        });
    }
    void initLabelHolder(HBox current,boolean top)
    {
        current.addEventHandler(MouseEvent.MOUSE_ENTERED,(e) -> {
            current.setStyle("-fx-border-width: 1px;-fx-border-color: white;-fx-cursor: hand");
        });
        if(top)
            current.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> {
                currentLabel.setUnderline(false);
                currentLabel = (Label)current.getChildren().get(0);
                currentLabel.setUnderline(true);
            });
        current.addEventHandler(MouseEvent.MOUSE_EXITED,(e) -> {
            current.setStyle("-fx-border-width: 0px;-fx-border-color: white;-fx-cursor: hand");
        });
    }
    private void createFilms(List<MovieDb> movieDbs)
    {
        for(MovieDb current : movieDbs)
        {
            try
            {
                String path = FilmHandler.getInstance().getMovieImageURL(current.getId(),"en",ArtworkType.POSTER,0);
                VBox vBox = CacheHandler.getInstance().getFilmBox(current.getId(),current.getTitle(),path);
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

