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
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

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
    private TitledPane third;
    @FXML
    private Button loadPreviousPageButton;
    @FXML
    private Button loadNextPageButton;
    @FXML
    private VBox genreHolder;
    @FXML
    private Label currentPageLabel;
    @FXML
    private Label maxPageLabel;
    @FXML
    private HBox boxHolder;
    @FXML
    private HBox bottomHolder;
    @FXML
    private VBox filmsProgress;

    private static final BooleanProperty firstExpanded = new SimpleBooleanProperty(true);
    private static final BooleanProperty secondExpanded = new SimpleBooleanProperty();

    private GenreList genreList;
    private CurrentSearch currentSearch;

    @FXML
    private void initialize()
    {
        bottomHolder.managedProperty().bind(bottomHolder.visibleProperty());
        CacheHandler.getInstance().reset();
        FilmHandler.getInstance().updateGenres();
        first.setExpanded(firstExpanded.getValue());
        second.setExpanded(secondExpanded.getValue());
        firstExpanded.bind(first.expandedProperty());
        secondExpanded.bind(second.expandedProperty());
        loadNextPageButton.setOnAction(event -> loadNext(true));
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
        third.managedProperty().bind(third.visibleProperty());
        genreList = new GenreList(FilmHandler.getInstance().getGenres());
        ResearchHandler.getInstance().search(ResearchHandler.getInstance().getCurrentListType() != null);
        genreHolder.getChildren().add(genreList);
        currentSearch = new CurrentSearch();
        boxHolder.getChildren().add(currentSearch);
        initComponents();
        sortComboBox.disableProperty().bind(ResearchHandler.getInstance().getSortingAvailable());
        sortOrderComboBox.disableProperty().bind(ResearchHandler.getInstance().getSortingAvailable());
    }

     /**
     * Method used to init a combo box using an enum
     * @param values The enum values used to initialize the combo box
     * @param comboBox The combobox to initialize
     * @param <T> The Enum Type
     */
    private <T extends Enum<T>> void initDropdown(Enum<T>[] values, ComboBox<String> comboBox) {
        for (Enum<T> current : values) {
            String value = StyleHandler.getInstance().getResourceBundle().getString(current.toString() + ".name");
            comboBox.getItems().add(value);
        }
        this.searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
    }
     /**
      Method used to init UI components
     **/
    private void initComponents() {
        initDropdown(MovieSortType.values(), sortComboBox);
        initDropdown(MovieSortOrder.values(), sortOrderComboBox);
        sortComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortType(MovieSortType.values()[sortComboBox.getSelectionModel().getSelectedIndex()]));
        sortOrderComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortOrder(MovieSortOrder.values()[sortOrderComboBox.getSelectionModel().getSelectedIndex()]));
        for(MovieListType current : MovieListType.values())
        {
            Label label = new Label(current.getLocalizedName());
            label.addEventHandler(MouseEvent.MOUSE_CLICKED,(event) -> {
                genreList.clearList();
                ResearchHandler.getInstance().setCurrentListType(current);
            });
            label.setWrapText(true);
            label.setTooltip(new Tooltip(StyleHandler.getInstance().getResourceBundle().getString("loadList.name") + " " + current.getLocalizedName().toLowerCase() + " " + (!current.getLocalizedName().toLowerCase().contains(StyleHandler.getInstance().getResourceBundle().getString("movies.name")) ? StyleHandler.getInstance().getResourceBundle().getString("movies.name") : "")));
            listHolder.getChildren().add(label);
        }
        sortComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortType().ordinal());
        sortOrderComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortOrder().ordinal());
    }

     /**
     * Method used to create a new film container to contain new loaded movies
     * @param movieDbs the movies in the new film container
     */
    private void createFilms(List<MovieDb> movieDbs) {
        FilmContainer filmContainer = new FilmContainer(movieDbs,true);
        scrollPane.setContent(filmContainer);
        showCurrent();
        this.handleLoading(false);
    }

     /**
     * Method used to update current page
     * @param positive If we need to subtract or add by one
     */
    private void loadNext(boolean positive) {
        ResearchHandler.getInstance().updateCurrentPage(positive);
    }

     /**
     *  Handles the Research Started Event
     */
    @Override
    public void OnResearchStarted()
    {
        this.handleLoading(true);
    }

     /**
     * Handles the Research Successed Event
     * @param result Movies Loaded
     * @param isGenre If the research is a list or a text search or a filter by genre search
     */
    @Override
    public void OnResearchSuccessed(List<MovieDb> result,boolean isGenre)
    {
        if(!isGenre)
            genreList.clearList();
        else
        {
            searchField.clear();
            searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
        }
        if(ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.LIBRARY)
        {
            createFilms(result);
            return;
        }
        createFilms(result);
        currentPageLabel.setText(String.valueOf(ResearchHandler.getInstance().getCurrentPage()));
        maxPageLabel.setText(String.valueOf(ResearchHandler.getInstance().getCurrentMaxPage()));
    }

     /**
     * Handles the visibility of components used to show or hide loading screen
     * @param value If we need to show the progress indicator or not
     */
    private void handleLoading(boolean value)
    {
        this.scrollPane.setDisable(value);
        this.bottomHolder.setDisable(value);
        this.filmsProgress.setDisable(!value);
    }

     /**
     * Method used to update the Current Search Component,used to show the last search filters used by the user
     */
    private void showCurrent()
    {
        if(currentSearch == null)
            return;
        currentSearch.update();
    }

    /**
     * Handles the Research Failed Event,creates a error page component with a button to reset search
     */
    @Override
    public void OnResearchFailed()
    {
        showCurrent();
        this.handleLoading(false);
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

     /**
     * Handles the View Changed Event,checks the current View Mode and handles the event based on it
     * @param movieViewMode New Selected View Mode
     * @param clear If we need to reset all filters currently active
     * @param search If it's needed to perform a search
     */
    @Override
    public void OnViewChanged(MovieViewMode movieViewMode,boolean clear,boolean search)
    {
        if(clear)
        {
            genreList.clearList();
            ResearchHandler.getInstance().clearSearch();
        }
        bottomHolder.setVisible(movieViewMode == MovieViewMode.HOME);
        if(movieViewMode == MovieViewMode.LIBRARY)
        {
            third.setVisible(false);
            searchField.setPromptText(StyleHandler.getInstance().getResourceBundle().getString("textPrompt.name"));
            this.handleLoading(true);
            if(FilmHandler.getInstance().RequiresUpdate())
            {
                Client.getInstance().get("films",(workerStateEvent) -> {
                    FilmHandler.getInstance().loadMovies(((JSONObject)workerStateEvent.getSource().getValue()).getJSONArray("films"));
                    if(search)
                        ResearchHandler.getInstance().search(false);
                    else
                        createFilms(FilmHandler.getInstance().sortMovies(FilmHandler.getInstance().getCurrentLoaded(), MovieSortType.POPULARITY,MovieSortOrder.DESC));
                },(workerStateEvent) -> this.handleLoading(false));
            }
            else
                createFilms(FilmHandler.getInstance().sortMovies(FilmHandler.getInstance().getCurrentLoaded(),MovieSortType.POPULARITY, MovieSortOrder.DESC));
        }
        else
        {
            third.setVisible(true);
            ResearchHandler.getInstance().setCurrentListType(MovieListType.MOST_POPULAR);
        }
    }
}