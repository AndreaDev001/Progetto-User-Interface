package com.progetto.progetto.controller;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortOrder;
import com.progetto.progetto.model.enums.MovieSortType;
import com.progetto.progetto.model.handlers.*;
import com.progetto.progetto.model.records.Library;
import com.progetto.progetto.model.records.User;
import com.progetto.progetto.model.sql.SQLGetter;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainController implements IResearchListener
{
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

    @FXML
    private void initialize()
    {
        advancedSearchButton.setOnAction((event) -> {
            ResearchHandler.getInstance().setCurrentGenre(getMultipleGenres(),false);
            ResearchHandler.getInstance().setCurrentFilterType(MovieFilterType.MULTIPLE_GENRES,true);
        });
        ResearchHandler.getInstance().addListener(this);
        ResearchHandler.getInstance().search(ResearchHandler.getInstance().getCurrentListType() != null);
        if(ResearchHandler.getInstance().getCurrentText().isEmpty())
            this.searchField.setPromptText("Write a name");
        else
            this.searchField.setText(ResearchHandler.getInstance().getCurrentText());
        this.searchField.addEventHandler(KeyEvent.KEY_PRESSED,(e) -> {
            if(e.getCode() != KeyCode.ENTER)
                return;
            ResearchHandler.getInstance().setCurrentText(searchField.getText());
        });
        initBoxes();
    }
    private <T extends Enum<T>> void initDropdown(Enum<T>[] values,ComboBox<String> comboBox)
    {
        for(Enum<T> current : values)
        {
            String value = current.toString().toLowerCase();
            comboBox.getItems().add(value);
        }
        this.searchField.setPromptText("Write a name");
    }
    private void initBoxes()
    {
        initDropdown(MovieSortType.values(),sortComboBox);
        initDropdown(MovieSortOrder.values(),sortOrderComboBox);
        sortComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortType(MovieSortType.valueOf(sortComboBox.getSelectionModel().getSelectedItem().toUpperCase())));
        sortOrderComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortOrder(MovieSortOrder.valueOf(sortOrderComboBox.getSelectionModel().getSelectedItem().toUpperCase())));
        sortComboBox.getSelectionModel().select(2);
        genresComboBox.getSelectionModel().select(0);
        genresComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentGenre(genresComboBox.getSelectionModel().getSelectedItem(),true));
        sortOrderComboBox.getSelectionModel().select(1);
        for(MovieListType movieListType : MovieListType.values())
        {
            String value = movieListType.name().toLowerCase();
            Label label = new Label(value);
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> ResearchHandler.getInstance().setCurrentListType(movieListType));
            listHolder.getChildren().add(label);
        }
        for(String current : FilmHandler.getInstance().getGenres())
        {
            genresComboBox.getItems().add(current);
            CheckBox checkBox = new CheckBox(current);
            checkBox.setLineSpacing(100);
            checkBox.setTooltip(createToolTip("Add filter by" + " " + current + " " + "to advanced research"));
            checkBox.setAlignment(Pos.CENTER);
            checkBoxes.add(checkBox);
            advancedHolder.getChildren().add(checkBox);
        }
        sortComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortType().ordinal());
        sortOrderComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortOrder().ordinal());
        genresComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentGenre());
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
        StringBuilder stringBuilder = new StringBuilder();
        for(CheckBox current : checkBoxes)
        {
            if(current.isSelected())
                stringBuilder.append(current.getText()).append(",");
        }
        return stringBuilder.toString();
    }
    private void loadLibrary()
    {
        User user = ProfileHandler.getInstance().getLoggedUser().get();
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
    @FXML
    private void loadPreviousPage()
    {
        ResearchHandler.getInstance().updateCurrentPage(false);
    }
    @FXML
    private void loadNextPage()
    {
        ResearchHandler.getInstance().updateCurrentPage(true);
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
    @Override
    public void OnResearchCompleted(List<MovieDb> result) {
        flowPane.setTranslateY(-1000);
        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(flowPane.translateYProperty(),0,Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(450),keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        integerList.clear();
        flowPane.getChildren().clear();
        currentLoaded = result;
        createFilms(currentLoaded);
    }
    @Override
    public void OnResearchFailed() {
        integerList.clear();
        flowPane.getChildren().clear();
        VBox vBox = new VBox();
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
}