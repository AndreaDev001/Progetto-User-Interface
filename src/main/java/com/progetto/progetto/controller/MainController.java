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

    private List<MovieDb> currentLoaded = new ArrayList<>();
    private Map<String,MovieDb> movieDbMap = new HashMap<>();
    private List<VBox> vBoxes = new ArrayList<>();
    private Label currentLabel;

    @FXML
    private void initialize()
    {
        currentLabel = (Label)homeBox.getChildren().get(0);
        currentLabel.setUnderline(true);
        quitBox.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> SceneHandler.getInstance().loadLoginScene());
        TmdbMovies movies = new TmdbApi("3837271101e801680438310f38a3feff").getMovies();;
        for(int i = 0;i < 5;i++)
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
            vBoxes.clear();
            if(filteredMovies.size() == 0)
                filteredMovies = currentLoaded;
            createFilms(filteredMovies);
        });
        initLabel(homeBox,true);
        initLabel(libraryBox,true);
        initLabel(settingsBox,true);
        initLabel(quitBox,true);
        initLabel(actionBox,false);
        initLabel(adventureBox,false);
        initLabel(warBox,false);
        initLabel(storyBox,false);
        initLabel(comedyBox,false);
        initLabel(drammaticBox,false);
        initLabel(apocalypticBox,false);
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
            createFilms(result);
        });
    }
    void initLabel(HBox current,boolean top)
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
                VBox vBox = new VBox();
                Image image = CacheHandler.getInstance().getImage(path);
                ImageView imageView = new ImageView(image);
                Label label = new Label(current.getTitle());
                vBox.setAlignment(Pos.CENTER);
                vBox.setPrefWidth(30);
                vBox.setPrefHeight(30);
                vBox.setMinWidth(Region.USE_COMPUTED_SIZE);
                vBox.setMinHeight(Region.USE_COMPUTED_SIZE);
                vBox.setMaxWidth(Region.USE_COMPUTED_SIZE);
                vBox.setMaxHeight(Region.USE_COMPUTED_SIZE);
                vBox.setFillWidth(true);
                label.getStyleClass().add("card-label");
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                vBox.setStyle("-fx-cursor: hand");
                vBox.getChildren().add(imageView);
                vBox.getChildren().add(label);
                vBoxes.add(vBox);
                flowPane.getChildren().add(vBox);
            }catch (NullPointerException | IndexOutOfBoundsException exception)
            {

            }
        }
    }
}

