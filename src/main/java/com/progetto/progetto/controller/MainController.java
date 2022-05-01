package com.progetto.progetto.controller;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortOrder;
import com.progetto.progetto.model.enums.MovieSortType;
import com.progetto.progetto.model.exceptions.FilmNotFoundException;
import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.handlers.ProfileHandler;
import com.progetto.progetto.model.records.Library;
import com.progetto.progetto.model.records.User;
import com.progetto.progetto.model.sql.SQLGetter;
import com.progetto.progetto.view.SceneHandler;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    private VBox listHolder;
    @FXML
    private VBox advancedHolder;
    @FXML
    private FlowPane flowPane;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ComboBox<String> sortOrderComboBox;
    @FXML
    private ComboBox<String> genresComboBox = new ComboBox<>();
    @FXML
    private Button advancedSearchButton;


    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private List<MovieDb> currentLoaded = new ArrayList<>();
    private final List<Integer> integerList = new ArrayList<>();
    private Label currentLabel;
    private int currentPage = 0;

    @FXML
    private void initialize()
    {
        currentLabel = homeBox;
        currentLabel.setUnderline(true);
        libraryBox.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> loadLibrary());
        quitBox.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> SceneHandler.getInstance().loadLoginScene());
        settingsBox.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> SceneHandler.getInstance().loadSettingsScene());
        advancedSearchButton.setOnAction((event) -> search(currentPage,MovieFilterType.MULTIPLE_GENRES));
        for(int i = 0;i < 5;i++)
        {
            try
            {
                List<MovieDb> result = FilmHandler.getInstance().getMovies(i, MovieListType.MOST_POPULAR,"en");
                for(MovieDb current : result)
                {
                    if(currentLoaded.contains(current))
                        continue;
                    currentLoaded.add(current);
                }
            }
            catch (FilmNotFoundException e)
            {
                handleException();
            }
            createFilms(FilmHandler.getInstance().sortMovies(currentLoaded,MovieSortType.POPULARITY));
        }
        this.searchField.addEventHandler(KeyEvent.KEY_PRESSED,(e) -> {
            try
            {
                currentPage = 0;
                if(e.getCode() != KeyCode.ENTER)
                    return;
                currentLoaded = FilmHandler.getInstance().makeSearch(searchField.getText(),"en",0,MovieSortType.valueOf(sortComboBox.getSelectionModel().getSelectedItem().toUpperCase()),MovieFilterType.NAME,MovieSortOrder.valueOf(sortOrderComboBox.getSelectionModel().getSelectedItem().toUpperCase()));
                flowPane.getChildren().clear();
                integerList.clear();
                createFilms(currentLoaded);
            }
            catch (FilmNotFoundException exception)
            {
                handleException();
            }
        });
        initBoxes();
    }
    @FXML
    private void loadNextPage()
    {
        currentPage++;
        search(currentPage,MovieFilterType.SINGLE_GENRE);
    }
    private <T extends Enum<T>> void initDropdown(Enum<T>[] values,ComboBox<String> comboBox)
    {
        for(Enum<T> current : values)
        {
            String value = current.name().toLowerCase();
            comboBox.getItems().add(value);
        }
        comboBox.setOnAction((e) -> {
            currentPage = 0;
            search(currentPage,MovieFilterType.SINGLE_GENRE);
        });
    }
    private void search(int page,MovieFilterType movieFilterType) {
        try {
            MovieSortType movieSortType = MovieSortType.valueOf(sortComboBox.getSelectionModel().getSelectedItem().toUpperCase());
            MovieSortOrder movieSortOrder = MovieSortOrder.valueOf(sortOrderComboBox.getSelectionModel().getSelectedItem().toUpperCase());
            String genre = movieFilterType == MovieFilterType.SINGLE_GENRE ? genresComboBox.getSelectionModel().getSelectedItem() : getMultipleGenres();
            genre = genre == null ? "" : genre;
            currentLoaded = FilmHandler.getInstance().makeSearch(genre,"en",currentPage, movieSortType,movieFilterType, movieSortOrder);
            integerList.clear();
            flowPane.getChildren().clear();
            createFilms(currentLoaded);
        }
        catch (FilmNotFoundException exception)
        {
            handleException();
        }
    }
    private void initBoxes()
    {
        initLabel(homeBox);
        initLabel(libraryBox);
        initLabel(settingsBox);
        initLabel(quitBox);
        initDropdown(MovieSortType.values(),sortComboBox);
        initDropdown(MovieSortOrder.values(),sortOrderComboBox);
        sortComboBox.getSelectionModel().select(2);
        genresComboBox.getSelectionModel().select(0);
        genresComboBox.setOnAction((event) -> {
            currentPage = 0;
            search(currentPage,MovieFilterType.SINGLE_GENRE);
        });
        advancedSearchButton.setOnAction((event) -> {
            currentPage = 0;
            search(currentPage,MovieFilterType.MULTIPLE_GENRES);
        });
        sortOrderComboBox.getSelectionModel().select(1);
        for(MovieListType movieListType : MovieListType.values())
        {
            String value = movieListType.name().toLowerCase();
            Label label = new Label(value);
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                try
                {
                    currentPage = 0;
                    currentLoaded = FilmHandler.getInstance().getMovies(1,MovieListType.valueOf(value.toUpperCase()),"en");
                    integerList.clear();
                    flowPane.getChildren().clear();
                    createFilms(currentLoaded);
                } catch (FilmNotFoundException e) {
                    handleException();
                }
            });
            listHolder.getChildren().add(label);
        }
        for(String current : FilmHandler.getInstance().getGenres())
        {
            genresComboBox.getItems().add(current);
            CheckBox checkBox = new CheckBox(current);
            checkBox.setLineSpacing(100);
            checkBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            checkBox.setTooltip(createToolTip("Add filter by" + " " + current + " " + "to advanced research"));
            checkBox.setAlignment(Pos.CENTER);
            checkBoxes.add(checkBox);
            advancedHolder.getChildren().add(checkBox);
        }
    }
    private void initLabel(Label current)
    {
        current.addEventHandler(MouseEvent.MOUSE_ENTERED,mouseEvent -> current.setUnderline(true));
        current.addEventHandler(MouseEvent.MOUSE_EXITED,mouseEvent -> current.setUnderline(false));
        current.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> {
            currentLabel.setUnderline(false);
            currentLabel = current;
            currentLabel.setUnderline(true);
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
    private String getMultipleGenres()
    {
        String result = "";
        StringBuilder stringBuilder = new StringBuilder();
        for(CheckBox current : checkBoxes)
        {
            if(current.isSelected())
                stringBuilder.append(current.getText()).append(",");
        }
        result = stringBuilder.toString();
        return result;
    }
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
    private void handleException()
    {
        currentPage = 0;
        integerList.clear();
        flowPane.getChildren().clear();
        System.out.println(flowPane.getChildren().size());
        VBox vBox = new VBox();
        vBox.setMinWidth(Region.USE_PREF_SIZE);
        vBox.setMinHeight(Region.USE_PREF_SIZE);
        vBox.setMaxWidth(Region.USE_PREF_SIZE);
        vBox.setMaxHeight(Region.USE_PREF_SIZE);
        Label label = new Label("Empty Set");
        label.setStyle("-fx-font-size: 30px;-fx-font-weight: bold");
        Button button = new Button("Back to Home");
        button.setStyle("-fx-padding: 10px 10px");
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            flowPane.getChildren().clear();
            createFilms(currentLoaded);
        });
        vBox.getChildren().add(label);
        vBox.getChildren().add(button);
        flowPane.getChildren().add(vBox);
    }
    private Tooltip createToolTip(String value)
    {
        Tooltip result = new Tooltip(value);
        result.setTextAlignment(TextAlignment.CENTER);
        result.setWrapText(true);
        Duration duration = Duration.seconds(0);
        result.setShowDelay(duration);
        result.setHideDelay(duration);
        return result;
    }
}