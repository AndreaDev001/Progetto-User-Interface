package com.progetto.progetto.controller;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortOrder;
import com.progetto.progetto.model.enums.MovieSortType;
import com.progetto.progetto.model.handlers.*;
import com.progetto.progetto.model.records.Library;
import com.progetto.progetto.model.records.User;
import com.progetto.progetto.model.sql.SQLGetter;
import com.progetto.progetto.view.StyleHandler;
import com.progetto.progetto.view.nodes.FilmCard;
import com.progetto.progetto.view.nodes.FilmContainer;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.fxml.FXML;
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
import java.util.ResourceBundle;

public class MainController implements IResearchListener {
    @FXML
    private VBox listHolder;
    @FXML
    private VBox advancedHolder;
    @FXML
    private ScrollPane scrollPane;
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
    @FXML
    private TitledPane first;
    @FXML
    private TitledPane second;
    @FXML
    private Button loadPreviousPageButton;
    @FXML
    private Button loadNextPageButton;
    @FXML
    private VBox showCurrentHolder;
    @FXML
    private Label currentPageLabel;
    @FXML
    private Label maxPageLabel;

    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private static final BooleanProperty disableSorting = new SimpleBooleanProperty();

    @FXML
    private void initialize() {
        CacheHandler.getInstance().reset();
        FilmHandler.getInstance().updateGenres();
        sortComboBox.disableProperty().bind(disableSorting);
        sortOrderComboBox.disableProperty().bind(disableSorting);
        loadNextPageButton.setTooltip(createToolTip("Load Next Page"));
        loadNextPageButton.setGraphic(new FontIcon("fas-arrow-right"));
        loadNextPageButton.setOnAction(event -> loadNext(true));
        loadPreviousPageButton.setTooltip(createToolTip("Load Previous Page"));
        loadPreviousPageButton.setGraphic(new FontIcon("fas-arrow-left"));
        loadPreviousPageButton.setOnAction(event -> loadNext(false));
        advancedSearchButton.setOnAction((event) -> {
            String value = getMultipleGenres();
            if (value.isEmpty())
                return;
            searchField.setText("");
            searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
            genresComboBox.getSelectionModel().clearSelection();
            ResearchHandler.getInstance().setCurrentMultipleGenre(getMultipleGenres(),false);
            ResearchHandler.getInstance().setCurrentFilterType(MovieFilterType.MULTIPLE_GENRES, true);
        });
        ResearchHandler.getInstance().addListener(this);
        ResearchHandler.getInstance().search(ResearchHandler.getInstance().getCurrentListType() != null);
        if (ResearchHandler.getInstance().getCurrentText().isEmpty())
            this.searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
        else
            this.searchField.setText(ResearchHandler.getInstance().getCurrentText());
        this.searchField.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() != KeyCode.ENTER)
                return;
            clearCheckBoxes();
            genresComboBox.getSelectionModel().clearSelection();
            disableSorting.set(false);
            ResearchHandler.getInstance().setCurrentText(searchField.getText());
        });
        first.expandedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue().booleanValue()) second.setExpanded(false);
        });
        second.expandedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue().booleanValue()) first.setExpanded(false);
        });
        initBoxes();
    }

    private <T extends Enum<T>> void initDropdown(Enum<T>[] values, ComboBox<String> comboBox) {
        for (Enum<T> current : values) {
            String value = StyleHandler.getInstance().getResourceBundle().getString(current.toString() + ".name");
            comboBox.getItems().add(value);
        }
        this.searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
    }
    private void clearCheckBoxes() {
        for (CheckBox current : checkBoxes)
            current.setSelected(false);
    }
    private void initBoxes() {
        initDropdown(MovieSortType.values(), sortComboBox);
        initDropdown(MovieSortOrder.values(), sortOrderComboBox);
        sortComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortType(MovieSortType.values()[sortComboBox.getSelectionModel().getSelectedIndex()]));
        sortOrderComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortOrder(MovieSortOrder.values()[sortOrderComboBox.getSelectionModel().getSelectedIndex()]));
        sortComboBox.getSelectionModel().select(2);
        genresComboBox.getSelectionModel().select(0);
        genresComboBox.setOnAction((event) -> {
            if (genresComboBox.getValue() == null || genresComboBox.getValue().isEmpty())
                return;
            clearCheckBoxes();
            searchField.setText("");
            searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
            disableSorting.set(false);
            ResearchHandler.getInstance().setCurrentGenre(genresComboBox.getSelectionModel().getSelectedIndex(), false);
            ResearchHandler.getInstance().setCurrentFilterType(MovieFilterType.SINGLE_GENRE, true);
        });
        sortOrderComboBox.getSelectionModel().select(1);
        for (MovieListType movieListType : MovieListType.values()) {
            String value = movieListType.getLocalizedName();
            Label label = new Label(value);
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                disableSorting.set(false);
                genresComboBox.getSelectionModel().clearSelection();
                ResearchHandler.getInstance().setCurrentListType(movieListType);
            });
            listHolder.getChildren().add(label);
        }
        for (String current : FilmHandler.getInstance().getGenres()) {
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
        genresComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentFilterType() != MovieFilterType.MULTIPLE_GENRES ? FilmHandler.getInstance().getGenres().get(ResearchHandler.getInstance().getCurrentGenre()) : "");
    }

    private void createFilms(List<MovieDb> movieDbs) {
        FilmContainer filmContainer = new FilmContainer(movieDbs);
        scrollPane.setContent(filmContainer);
    }
    private String getMultipleGenres() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i < checkBoxes.size();i++)
        {
            CheckBox current = checkBoxes.get(i);
            if(current.isSelected())
                stringBuilder.append(i).append(",");
        }
        return stringBuilder.toString();
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
    private Tooltip createToolTip(String value) {
        Tooltip result = new Tooltip(value);
        result.setTextAlignment(TextAlignment.CENTER);
        result.setWrapText(true);
        Duration duration = Duration.seconds(0);
        result.setShowDelay(duration);
        result.setHideDelay(duration);
        return result;
    }
    private Label createCurrentLabel(String fieldName,String value)
    {
        Label result = new Label(fieldName + ":" + value);
        result.setAlignment(Pos.CENTER);
        result.setWrapText(true);
        result.getStyleClass().add("showCurrent");
        return result;
    }
    @Override
    public void OnResearchCompleted(List<MovieDb> result) {
        scrollPane.setContent(null);
        createFilms(result);
        currentPageLabel.setText(String.valueOf(ResearchHandler.getInstance().getCurrentPage()));
        maxPageLabel.setText(String.valueOf(ResearchHandler.getInstance().getCurrentMaxPage()));
        showCurrent();
    }
    private void showCurrent()
    {
        showCurrentHolder.getChildren().clear();
        if(ResearchHandler.getInstance().getCurrentListType() != null)
        {
            Label currentList = createCurrentLabel(StyleHandler.getInstance().getResourceBundle().getString("currentList.name"),ResearchHandler.getInstance().getCurrentListType().getLocalizedName());
            showCurrentHolder.getChildren().add(currentList);
            disableSorting.set(true);
        } else if(ResearchHandler.getInstance().getCurrentText() != null && ResearchHandler.getInstance().getCurrentText().isEmpty())
        {
            Label currentGenre = ResearchHandler.getInstance().getCurrentGenre() < 0 ? new Label("") : createCurrentLabel(StyleHandler.getInstance().getResourceBundle().getString("currentGenre.name"),FilmHandler.getInstance().getGenres().get(ResearchHandler.getInstance().getCurrentGenre()));
            Label currentSortType = ResearchHandler.getInstance().getCurrentSortType() == null ? new Label("") : createCurrentLabel(StyleHandler.getInstance().getResourceBundle().getString("currentSort.name"),StyleHandler.getInstance().getResourceBundle().getString(ResearchHandler.getInstance().getCurrentSortType().toString() + ".name"));
            Label currentSortOrder = ResearchHandler.getInstance().getCurrentSortOrder() == null ? new Label("") : createCurrentLabel(StyleHandler.getInstance().getResourceBundle().getString("currentOrder.name"),StyleHandler.getInstance().getResourceBundle().getString(ResearchHandler.getInstance().getCurrentSortOrder().toString() + ".name"));
            showCurrentHolder.getChildren().addAll(currentGenre, currentSortType, currentSortOrder);
        }
        else
        {
            Label currentText = createCurrentLabel(StyleHandler.getInstance().getResourceBundle().getString("currentSearch.name"),ResearchHandler.getInstance().getCurrentText());
            showCurrentHolder.getChildren().add(currentText);
        }
    }
    @Override
    public void OnResearchFailed()
    {
        scrollPane.setContent(null);
        showCurrent();
        VBox vBox = new VBox();
        vBox.getStyleClass().add("background");
        Label label = new Label("Empty Set");
        label.setStyle("-fx-font-size: 30px;-fx-font-weight: bold");
        Button button = new Button("Back to Home");
        button.setStyle("-fx-padding: 10px 10px");
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ResearchHandler.getInstance().setCurrentListType(MovieListType.MOST_POPULAR);
            disableSorting.set(false);
        });
        vBox.getChildren().add(label);
        vBox.getChildren().add(button);
        scrollPane.setContent(vBox);
    }
}