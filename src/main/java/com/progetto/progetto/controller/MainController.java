package com.progetto.progetto.controller;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.model.enums.MovieListType;
import com.progetto.progetto.model.enums.MovieSortOrder;
import com.progetto.progetto.model.enums.MovieSortType;
import com.progetto.progetto.model.enums.MovieViewMode;
import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.handlers.listeners.IResearchListener;
import com.progetto.progetto.model.handlers.ResearchHandler;
import com.progetto.progetto.view.SceneHandler;
import com.progetto.progetto.model.handlers.StyleHandler;
import com.progetto.progetto.view.nodes.*;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.json.JSONObject;
import java.util.List;

public class MainController implements IResearchListener
{
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
    private VBox listHolder;
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
    @FXML
    private ToggleButton enableCards;
    @FXML
    private ToggleButton enableTable;

    private static final BooleanProperty firstExpanded = new SimpleBooleanProperty(true);
    private static final BooleanProperty secondExpanded = new SimpleBooleanProperty();

    private CostumListView listView;
    private GenreList genreList;
    private CurrentSearch currentSearch;
    private static boolean useCards = true;

    @FXML
    private void initialize()
    {
        if(useCards)
            enableCards.setSelected(true);
        else
            enableTable.setSelected(true);
        enableCards.selectedProperty().addListener((changeListener) -> {if(enableCards.isSelected()) update(true);});
        enableTable.selectedProperty().addListener((changeListener) -> {if(enableTable.isSelected()) update(false);});
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
            this.searchField.setPromptText(StyleHandler.getInstance().getLocalizedString("textPrompt.name"));
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
        first.addEventHandler(KeyEvent.KEY_PRESSED,(event) -> {
            if(event.getCode().equals(KeyCode.ENTER) && !searchField.isFocused())
                first.setExpanded(third.isFocused() || !first.isExpanded());
        });
        second.addEventHandler(KeyEvent.KEY_PRESSED,(event) -> {if(event.getCode().equals(KeyCode.ENTER)) second.setExpanded(!second.isExpanded());});
        third.addEventHandler(KeyEvent.KEY_PRESSED,(event) -> {if(event.getCode().equals(KeyCode.ENTER)) third.setExpanded(!third.isExpanded());});
    }
    /**
     * Method used to init a combo box using an enum
     * @param values The enum values used to initialize the combo box
     * @param comboBox The combobox to initialize
     * @param <T> The Enum Type
     */
    private <T extends Enum<T>> void initDropdown(Enum<T>[] values, ComboBox<String> comboBox) {
        for (Enum<T> current : values) {
            String value = StyleHandler.getInstance().getLocalizedString(current.toString() + ".name");
            comboBox.getItems().add(value);
        }
        this.searchField.setPromptText(StyleHandler.getInstance().getLocalizedString("textPrompt.name"));
    }
    /**
     Method used to init UI components
     **/
    private void initComponents() {
        initDropdown(MovieSortType.values(), sortComboBox);
        initDropdown(MovieSortOrder.values(), sortOrderComboBox);
        sortComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortType(MovieSortType.values()[sortComboBox.getSelectionModel().getSelectedIndex()]));
        sortOrderComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortOrder(MovieSortOrder.values()[sortOrderComboBox.getSelectionModel().getSelectedIndex()]));
        listView = new CostumListView();
        listHolder.getChildren().add(listView);
        listView.getSelectionModel().selectedIndexProperty().addListener((changeListener) -> {
            if(!listView.getSelectionModel().isEmpty())
                ResearchHandler.getInstance().setCurrentListType(MovieListType.values()[listView.getSelectionModel().getSelectedIndex()]);
        });
        sortComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortType().ordinal());
        sortOrderComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortOrder().ordinal());
    }
    /**
     * Method used to create a new film container to contain new loaded movies
     * @param movieDbs the movies in the new film container
     */
    private void createFilms(List<MovieDb> movieDbs) {
        if(useCards)
        {
            FilmContainer filmContainer = new FilmContainer(movieDbs);
            filmContainer.getFilmCards().forEach(filmCard ->
            {
                filmCard.addEventHandler(MouseEvent.MOUSE_CLICKED,(event) -> openFilm(filmCard.getMovieDb()));
                filmCard.addEventHandler(KeyEvent.KEY_PRESSED,(event) -> {if(event.getCode().equals(KeyCode.ENTER)) openFilm(filmCard.getMovieDb());});
            });
            scrollPane.setContent(filmContainer);
        }
        else
        {
            FilmTable filmTable = new FilmTable(movieDbs);
            filmTable.getSelectionModel().selectedItemProperty().addListener((changeListener) -> {
                if(filmTable.getSelectionModel().isEmpty())
                    return;
                openFilm(filmTable.getSelectionModel().getSelectedItem());
            });
            scrollPane.setContent(filmTable);
        }
        this.handleLoading(false);
        showCurrent();
    }
    private void openFilm(MovieDb movieDb)
    {
        if(SceneHandler.getInstance().getFilmStage().isIconified() && FilmHandler.getInstance().getCurrentSelectedFilm() == movieDb.getId())
        {
            SceneHandler.getInstance().centerStage(SceneHandler.getInstance().getFilmStage(), SceneHandler.getInstance().getFilmStage().getWidth(), SceneHandler.getInstance().getFilmStage().getHeight());
            SceneHandler.getInstance().getFilmStage().setIconified(false);
            return;
        }
        FilmHandler.getInstance().selectFilm(movieDb.getId());
        SceneHandler.getInstance().loadFilmScene();
    }
    private void loadNext(boolean positive) {
        ResearchHandler.getInstance().updateCurrentPage(positive);
    }
    @Override
    public void OnResearchStarted()
    {
        this.handleLoading(true);
    }
    @Override
    public void OnResearchSuccessed(List<MovieDb> result,boolean isGenre)
    {
        if(!isGenre)
        {
            genreList.clearList();
        }
        else
        {
            this.listView.getSelectionModel().clearSelection();
            searchField.clear();
            searchField.setPromptText(StyleHandler.getInstance().getLocalizedString("textPrompt.name"));
        }
        if(ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.LIBRARY)
        {
            createFilms(result);
            return;
        }
        this.bottomHolder.setVisible(true);
        createFilms(result);
        currentPageLabel.setText(String.valueOf(ResearchHandler.getInstance().getCurrentPage()));
        maxPageLabel.setText(String.valueOf(ResearchHandler.getInstance().getCurrentMaxPage()));
    }
    private void handleLoading(boolean value)
    {
        this.scrollPane.setDisable(value);
        this.bottomHolder.setDisable(value);
        this.filmsProgress.setDisable(!value);
    }
    private void handleError(String errorName, String errorButton, EventHandler<ActionEvent> eventHandler)
    {
        ErrorPage errorPage = new ErrorPage(errorName,errorButton,true);
        errorPage.getErrorButton().setOnAction(eventHandler);
        bottomHolder.setVisible(false);
        this.handleLoading(false);
        scrollPane.setContent(errorPage);
    }
    private void showCurrent()
    {
        if(currentSearch == null)
            return;
        VBox.setVgrow(currentSearch, Priority.ALWAYS);
        currentSearch.update();
    }
    @Override
    public void OnResearchFailed()
    {
        showCurrent();
        EventHandler<ActionEvent> eventHandler = (event) -> {
            genreList.clearList();
            if(ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.HOME)
                ResearchHandler.getInstance().setCurrentListType(MovieListType.MOST_POPULAR);
            else
                ResearchHandler.getInstance().setCurrentGenre(genreList.getSelectedIndexes(),true);
        };
        handleError("errorText.name","errorButton.name",eventHandler);
    }
    @Override
    public void OnViewChanged(MovieViewMode movieViewMode,boolean clear,boolean search)
    {
        if(clear)
        {
            genreList.clearList();
            sortComboBox.getSelectionModel().select(2);
            sortOrderComboBox.getSelectionModel().select(1);
            ResearchHandler.getInstance().clearSearch();
        }
        bottomHolder.setVisible(movieViewMode == MovieViewMode.HOME);
        if(movieViewMode == MovieViewMode.LIBRARY)
        {
            third.setVisible(false);
            searchField.setPromptText(StyleHandler.getInstance().getLocalizedString("textPrompt.name"));
            this.handleLoading(true);
            if(FilmHandler.getInstance().RequiresUpdate())
            {
                Client.getInstance().get("films",(workerStateEvent) -> {
                    FilmHandler.getInstance().loadMovies(((JSONObject)workerStateEvent.getSource().getValue()).getJSONArray("films"));
                    if(search)
                        ResearchHandler.getInstance().search(false);
                    else
                    {
                        if(FilmHandler.getInstance().getCurrentLoaded().size() == 0)
                            handleError("emptyLibrary.name","backHome.name",(event) -> ResearchHandler.getInstance().setCurrentViewMode(MovieViewMode.HOME, false, true, false));
                        else
                            createFilms(FilmHandler.getInstance().sortMovies(FilmHandler.getInstance().getCurrentLoaded(), MovieSortType.POPULARITY,MovieSortOrder.DESC));
                    }
                },(workerStateEvent) -> this.handleLoading(false));
            }
            if(FilmHandler.getInstance().getCurrentLoaded().size() == 0)
                handleError("emptyLibrary.name","backHome.name",(event) -> ResearchHandler.getInstance().setCurrentViewMode(MovieViewMode.LIBRARY,false,true,false));
            else
                createFilms(FilmHandler.getInstance().sortMovies(FilmHandler.getInstance().getCurrentLoaded(),MovieSortType.POPULARITY, MovieSortOrder.DESC));
        }
        else
        {
            third.setVisible(true);
            ResearchHandler.getInstance().setCurrentListType(MovieListType.MOST_POPULAR);
        }
        handleLoading(false);
    }
    private void update(boolean value)
    {
        useCards = value;
        ResearchHandler.getInstance().search(ResearchHandler.getInstance().getCurrentListType() != null);
    }
}