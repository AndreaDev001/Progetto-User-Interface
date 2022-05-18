package com.progetto.progetto.controller;

import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortOrder;
import com.progetto.progetto.model.enums.MovieSortType;
import com.progetto.progetto.model.handlers.*;
import com.progetto.progetto.model.records.Library;
import com.progetto.progetto.model.records.User;
import com.progetto.progetto.model.sql.SQLGetter;
import com.progetto.progetto.view.StyleHandler;
import com.progetto.progetto.view.nodes.*;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainController implements IResearchListener {
    @FXML
    private VBox listHolder;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ComboBox<String> sortOrderComboBox;
    @FXML
    private TitledPane first;
    @FXML
    private TitledPane second;
    @FXML
    private Button loadPreviousPageButton;
    @FXML
    private Button loadNextPageButton;
    @FXML
    private VBox leftHolder;
    @FXML
    private VBox genreHolder;
    @FXML
    private Label currentPageLabel;
    @FXML
    private Label maxPageLabel;

    private static final BooleanProperty firstExpanded = new SimpleBooleanProperty();
    private static final BooleanProperty secondExpanded = new SimpleBooleanProperty();

    private GenreList genreList;
    private CurrentSearch currentSearch;

    @FXML
    private void initialize()
    {
        CacheHandler.getInstance().reset();
        FilmHandler.getInstance().updateGenres();
        first.setExpanded(firstExpanded.getValue());
        second.setExpanded(secondExpanded.getValue());
        firstExpanded.bind(first.expandedProperty());
        secondExpanded.bind(second.expandedProperty());
        loadNextPageButton.setTooltip(new HelpTooltip("loadNext.name",Duration.millis(0),Duration.millis(0),Duration.millis(0),true));
        loadNextPageButton.setGraphic(new FontIcon("fas-arrow-right"));
        loadNextPageButton.setOnAction(event -> loadNext(true));
        loadPreviousPageButton.setTooltip(new HelpTooltip("loadPrevious.name",Duration.millis(0),Duration.millis(0),Duration.millis(0),true));
        loadPreviousPageButton.setGraphic(new FontIcon("fas-arrow-left"));
        loadPreviousPageButton.setOnAction(event -> loadNext(false));
        ResearchHandler.getInstance().addListener(this);
        if (ResearchHandler.getInstance().getCurrentText().isEmpty())
            this.searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
        else
            this.searchField.setText(ResearchHandler.getInstance().getCurrentText());
        this.searchField.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() != KeyCode.ENTER)
                return;
            ResearchHandler.getInstance().setCurrentText(searchField.getText());
        });
        first.expandedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue().booleanValue()) second.setExpanded(false);
        });
        second.expandedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue().booleanValue()) first.setExpanded(false);
        });
        genreList = new GenreList(FilmHandler.getInstance().getGenres());
        if(ResearchHandler.getInstance().getCurrentGenre() != null && !ResearchHandler.getInstance().getCurrentGenre().isEmpty())
        {
            String[] values = ResearchHandler.getInstance().getCurrentGenre().split(",");
            for(String current : values)
                genreList.getCheckBoxes().get(Integer.parseInt(current)).setSelected(true);
        }
        ResearchHandler.getInstance().search(ResearchHandler.getInstance().getCurrentListType() != null);
        genreHolder.getChildren().add(genreList);
        currentSearch = new CurrentSearch();
        leftHolder.getChildren().add(currentSearch);
        initBoxes();
        for(Node current : leftHolder.getChildren())
            VBox.setVgrow(current,Priority.ALWAYS);
    }

    private <T extends Enum<T>> void initDropdown(Enum<T>[] values, ComboBox<String> comboBox) {
        for (Enum<T> current : values) {
            String value = StyleHandler.getInstance().getResourceBundle().getString(current.toString() + ".name");
            comboBox.getItems().add(value);
        }
        this.searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
    }
    private void initBoxes() {
        initDropdown(MovieSortType.values(), sortComboBox);
        initDropdown(MovieSortOrder.values(), sortOrderComboBox);
        sortComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortType(MovieSortType.values()[sortComboBox.getSelectionModel().getSelectedIndex()]));
        sortOrderComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortOrder(MovieSortOrder.values()[sortOrderComboBox.getSelectionModel().getSelectedIndex()]));
        sortComboBox.getSelectionModel().select(2);
        sortOrderComboBox.getSelectionModel().select(1);
        ValueList<MovieListType> valueList = new ValueList<MovieListType>(Arrays.stream(MovieListType.values()).toList());
        for(int i = 0;i < valueList.getChildren().size();i++)
        {
            int finalI = i;
            valueList.getChildren().get(i).addEventHandler(MouseEvent.MOUSE_CLICKED,(event) -> {
                genreList.clearList();
                ResearchHandler.getInstance().setCurrentListType(valueList.getValues().get(finalI));
            });
        }
        listHolder.getChildren().add(valueList);
        sortComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortType().ordinal());
        sortOrderComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortOrder().ordinal());
    }

    private void createFilms(List<MovieDb> movieDbs) {
        FilmContainer filmContainer = new FilmContainer(movieDbs,true);
        scrollPane.setContent(filmContainer);
    }
    private void loadLibrary() {
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
                MovieDb current = FilmHandler.getInstance().getMovie(filmId);
                movieDbs.add(current);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        scrollPane.setContent(null);
        createFilms(movieDbs);
    }
    private void loadNext(boolean positive) {
        ResearchHandler.getInstance().updateCurrentPage(positive);
    }
    @Override
    public void OnResearchCompleted(List<MovieDb> result,boolean isText)
    {
        if(isText)
            genreList.clearList();
        else
        {
            searchField.clear();
            searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
        }
        scrollPane.setContent(null);
        createFilms(result);
        currentPageLabel.setText(String.valueOf(ResearchHandler.getInstance().getCurrentPage()));
        maxPageLabel.setText(String.valueOf(ResearchHandler.getInstance().getCurrentMaxPage()));
        showCurrent();
    }
    private void showCurrent()
    {
        if(currentSearch == null)
            return;
        currentSearch.update();
        VBox.setVgrow(currentSearch, Priority.ALWAYS);
    }
    @Override
    public void OnResearchFailed()
    {
        showCurrent();
        ErrorPage errorPage = new ErrorPage("errorText.name","errorButton.name",true);
        errorPage.getErrorButton().setOnAction((event) -> {
            ResearchHandler.getInstance().setCurrentListType(MovieListType.MOST_POPULAR);
            genreList.clearList();
        });
        scrollPane.setContent(errorPage);
    }
}