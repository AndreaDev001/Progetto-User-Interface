package com.progetto.progetto.controller;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortOrder;
import com.progetto.progetto.model.enums.MovieSortType;
import com.progetto.progetto.model.enums.MovieViewMode;
import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.handlers.IResearchListener;
import com.progetto.progetto.model.handlers.ResearchHandler;
import com.progetto.progetto.view.StyleHandler;
import com.progetto.progetto.view.nodes.CurrentSearch;
import com.progetto.progetto.view.nodes.ErrorPage;
import com.progetto.progetto.view.nodes.FilmContainer;
import com.progetto.progetto.view.nodes.GenreList;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class MainController implements IResearchListener {
    @FXML
    private VBox listHolder;
    @FXML
    public ScrollPane scrollPane;
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
    @FXML
    private HBox boxHolder;
    @FXML
    public HBox bottomHolder;
    @FXML
    public VBox filmsProgress;

    private static final BooleanProperty sortingDisabled = new SimpleBooleanProperty();
    private static final BooleanProperty firstExpanded = new SimpleBooleanProperty(true);
    private static final BooleanProperty secondExpanded = new SimpleBooleanProperty();

    private GenreList genreList;
    private CurrentSearch currentSearch;

    @FXML
    private void initialize()
    {
        FontIcon firstIcon = new FontIcon("mdi2m-magnify");
        FontIcon secondIcon = new FontIcon("mdi2m-movie-filter");
        firstIcon.setIconSize(25);
        secondIcon.setIconSize(25);
        first.setGraphic(firstIcon);
        second.setGraphic(secondIcon);
        bottomHolder.managedProperty().bind(bottomHolder.visibleProperty());
        CacheHandler.getInstance().reset();
        FilmHandler.getInstance().updateGenres();
        first.setExpanded(firstExpanded.getValue());
        second.setExpanded(secondExpanded.getValue());
        firstExpanded.bind(first.expandedProperty());
        secondExpanded.bind(second.expandedProperty());
        loadNextPageButton.setTooltip(new Tooltip(StyleHandler.getInstance().getResourceBundle().getString("loadNext.name")));
        loadNextPageButton.setGraphic(new FontIcon("fas-arrow-right"));
        loadNextPageButton.setOnAction(event -> loadNext(true));
        loadPreviousPageButton.setTooltip(new Tooltip(StyleHandler.getInstance().getResourceBundle().getString("loadPrevious.name")));
        loadPreviousPageButton.setGraphic(new FontIcon("fas-arrow-left"));
        loadPreviousPageButton.setOnAction(event -> loadNext(false));
        this.bottomHolder.setVisible(ResearchHandler.getInstance().getCurrentViewMode() != MovieViewMode.LIBRARY);
        ResearchHandler.getInstance().setListener(this);
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
        boxHolder.getChildren().add(currentSearch);
        initBoxes();
        sortComboBox.disableProperty().bind(sortingDisabled);
        sortOrderComboBox.disableProperty().bind(sortingDisabled);
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
        for(MovieListType current : MovieListType.values())
        {
            Label label = new Label(current.getLocalizedName());
            label.addEventHandler(MouseEvent.MOUSE_CLICKED,(event) -> {
                genreList.clearList();
                ResearchHandler.getInstance().setCurrentListType(current);
            });
            label.setWrapText(true);
            label.setTooltip(new Tooltip(StyleHandler.getInstance().getResourceBundle().getString("loadList.name") + " " + current.getLocalizedName().toLowerCase() + " " + (!current.getLocalizedName().toLowerCase().contains(StyleHandler.getInstance().getResourceBundle().getString("movies.name")) ? StyleHandler.getInstance().getResourceBundle().getString("movies.name") : "")));
            label.addEventHandler(MouseEvent.MOUSE_ENTERED,(event) -> label.setUnderline(true));
            label.addEventHandler(MouseEvent.MOUSE_EXITED,(event) -> label.setUnderline(false));
            listHolder.getChildren().add(label);
        }
        sortComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortType().ordinal());
        sortOrderComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortOrder().ordinal());
    }
    private void createFilms(List<MovieDb> movieDbs) {
        FilmContainer filmContainer = new FilmContainer(movieDbs,true);
        scrollPane.setContent(filmContainer);
    }
    private void loadNext(boolean positive) {
        ResearchHandler.getInstance().updateCurrentPage(positive);
    }
    @Override
    public void OnResearchStarted()
    {
        this.handleLoading(true);
        System.out.println("Research Started");
    }
    @Override
    public void OnResearchCompleted(List<MovieDb> result,boolean isGenre)
    {
        if(ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.LIBRARY)
        {
            createFilms(result);
            showCurrent();
            return;
        }
        else
            bottomHolder.setVisible(true);
        if(!isGenre)
        {
            sortingDisabled.set(true);
            genreList.clearList();
        }
        else
        {
            sortingDisabled.set(false);
            searchField.clear();
            searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
        }
        createFilms(result);
        currentPageLabel.setText(String.valueOf(ResearchHandler.getInstance().getCurrentPage()));
        maxPageLabel.setText(String.valueOf(ResearchHandler.getInstance().getCurrentMaxPage()));
        this.handleLoading(false);
        showCurrent();
    }
    private void handleLoading(boolean value)
    {
        this.scrollPane.setDisable(value);
        this.bottomHolder.setDisable(value);
        this.filmsProgress.setDisable(value);
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
        this.scrollPane.setDisable(false);
        this.bottomHolder.setDisable(false);
        this.filmsProgress.setVisible(false);
        bottomHolder.setVisible(false);
        ErrorPage errorPage = new ErrorPage("errorText.name","errorButton.name",true);
        errorPage.getErrorButton().setOnAction((event) -> {
            genreList.clearList();
            if (ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.HOME)
                ResearchHandler.getInstance().setCurrentListType(MovieListType.MOST_POPULAR);
            else
                ResearchHandler.getInstance().setCurrentGenre(genreList.getSelectedIndexes(), true);
        });
        scrollPane.setContent(errorPage);
    }
    @Override
    public void OnViewChanged(MovieViewMode movieViewMode,boolean clear,boolean search)
    {
        if(clear)
        {
            genreList.clearList();
            ResearchHandler.getInstance().clearSearch();
        }
        bottomHolder.setVisible(movieViewMode == MovieViewMode.HOME);
        VBox vBox = (VBox)first.getContent();
        Node node = vBox.getChildren().get(1);
        node.setVisible(movieViewMode == MovieViewMode.HOME);
        node.setManaged(movieViewMode == MovieViewMode.HOME);
        switch (movieViewMode)
        {
            case LIBRARY -> {
                searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
                sortingDisabled.set(false);
                if(FilmHandler.getInstance().RequiresUpdate())
                {
                    this.handleLoading(true);
                    Client.getInstance().get("films",(workerStateEvent) -> {
                        this.handleLoading(false);
                        JSONArray jsonArray = ((JSONObject)workerStateEvent.getSource().getValue()).getJSONArray("films");
                        FilmHandler.getInstance().loadMovies(jsonArray);
                        if(search)
                            ResearchHandler.getInstance().search(false);
                        else
                            createFilms(FilmHandler.getInstance().sortMovies(FilmHandler.getInstance().getCurrentLoaded(), MovieSortType.POPULARITY,MovieSortOrder.DESC));
                    },(workerStateEvent) -> this.handleLoading(false));
                }
                else
                {
                    showCurrent();
                    createFilms(FilmHandler.getInstance().sortMovies(FilmHandler.getInstance().getCurrentLoaded(),MovieSortType.POPULARITY, MovieSortOrder.DESC));
                }
            }
            case HOME -> {
                ResearchHandler.getInstance().setCurrentListType(MovieListType.MOST_POPULAR);
                sortingDisabled.set(true);
            }
        }
    }
}