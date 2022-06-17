package com.progetto.progetto.controller;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.model.enums.*;
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
    private static final BooleanProperty firstExpanded = new SimpleBooleanProperty(true);
    private static final BooleanProperty secondExpanded = new SimpleBooleanProperty();
    private GenreList genreList;
    private CurrentSearch currentSearch;

    @FXML
    private void initialize()
    {
        ResearchHandler.getInstance().addViewListener((obs,oldValue,newValue) -> handleSwitchedView(),this.getClass().getSimpleName());
        this.handleLoading(true);
        initProperties();
        initComponents();
        initHandlers();
        FilmHandler.getInstance().updateGenres(error -> {
            this.handleLoading(false);
            first.setDisable(true);
            first.setExpanded(false);
            second.setExpanded(false);
            second.setDisable(true);
            handleError("connectionError.name","reloadButton.name",(event) -> SceneHandler.getInstance().reloadApplication(PageEnum.MAIN));},success -> {
            this.handleLoading(false);
            CacheHandler.getInstance().reset();
            ResearchHandler.getInstance().addListener(this,this.getClass().getSimpleName());
            genreList = new GenreList(FilmHandler.getInstance().getGenres());
            genreHolder.getChildren().add(genreList);
            ResearchHandler.getInstance().search(ResearchHandler.getInstance().getCurrentListType() != null);
            });
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
     * Inizializza tutte le proprietà
     */
    private void initProperties()
    {
        bottomHolder.managedProperty().bind(bottomHolder.visibleProperty());
        first.setExpanded(firstExpanded.getValue());
        second.setExpanded(secondExpanded.getValue());
        third.managedProperty().bind(third.visibleProperty());
        first.expandedProperty().addListener((observableValue, aBoolean, t1) -> second.setExpanded(!observableValue.getValue().booleanValue() && second.isExpanded()));
        second.expandedProperty().addListener((observableValue, aBoolean, t1) -> first.setExpanded(!observableValue.getValue().booleanValue() && first.isExpanded()));
        firstExpanded.bind(first.expandedProperty());
        secondExpanded.bind(second.expandedProperty());
        sortComboBox.disableProperty().bind(ResearchHandler.getInstance().getSortingAvailable());
        sortOrderComboBox.disableProperty().bind(ResearchHandler.getInstance().getSortingAvailable());
    }
    private void initHandlers()
    {
        loadNextPageButton.setOnAction(event -> loadNext(true));
        loadPreviousPageButton.setOnAction(event -> loadNext(false));
        first.addEventHandler(KeyEvent.KEY_PRESSED,(event) -> {
            if(event.getCode().equals(KeyCode.ENTER) && !searchField.isFocused())
                first.setExpanded(third.isFocused() || !first.isExpanded());
        });
        this.searchField.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() != KeyCode.ENTER)
                return;
            ResearchHandler.getInstance().setCurrentText(searchField.getText());
        });
        second.addEventHandler(KeyEvent.KEY_PRESSED,(event) -> {if(event.getCode().equals(KeyCode.ENTER)) second.setExpanded(!second.isExpanded());});
        third.addEventHandler(KeyEvent.KEY_PRESSED,(event) -> {if(event.getCode().equals(KeyCode.ENTER)) third.setExpanded(!third.isExpanded());});
        sortComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortType(MovieSortType.values()[sortComboBox.getSelectionModel().getSelectedIndex()]));
        sortOrderComboBox.setOnAction((event) -> ResearchHandler.getInstance().setCurrentSortOrder(MovieSortOrder.values()[sortOrderComboBox.getSelectionModel().getSelectedIndex()]));
    }
    /**
     Method used to init UI components
     **/
    private void initComponents()
    {
        initDropdown(MovieSortType.values(), sortComboBox);
        initDropdown(MovieSortOrder.values(), sortOrderComboBox);
        if (ResearchHandler.getInstance().getCurrentText().isEmpty())
            this.searchField.setPromptText(StyleHandler.getInstance().getLocalizedString("textPrompt.name"));
        else
            this.searchField.setText(ResearchHandler.getInstance().getCurrentText());
        this.bottomHolder.setVisible(ResearchHandler.getInstance().getCurrentViewMode() != MovieViewMode.LIBRARY);
        currentSearch = new CurrentSearch();
        boxHolder.getChildren().add(currentSearch);
        sortComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortType().ordinal());
        sortOrderComboBox.getSelectionModel().select(ResearchHandler.getInstance().getCurrentSortOrder().ordinal());
        for(MovieListType current : MovieListType.values())
        {
            Label label = new Label(current.getLocalizedName());
            label.setFocusTraversable(true);
            label.setWrapText(true);
            label.addEventHandler(MouseEvent.MOUSE_CLICKED,(event) -> ResearchHandler.getInstance().setCurrentListType(current));
            label.addEventHandler(KeyEvent.KEY_PRESSED,(event) -> {
                if(event.getCode() == KeyCode.ENTER)
                {
                    event.consume();
                    ResearchHandler.getInstance().setCurrentListType(current);
                }
            });
            this.listHolder.getChildren().add(label);
        }
    }
    /**
     * Method used to create a new film container to contain new loaded movies
     * @param movieDbs the movies in the new film container
     */
    private void createFilms(List<MovieDb> movieDbs) {
        FilmContainer filmContainer = new FilmContainer(movieDbs);
        filmContainer.getFilmCards().forEach(filmCard ->
        {
            filmCard.addEventHandler(MouseEvent.MOUSE_CLICKED,(event) -> openFilm(filmCard.getMovieDb()));
            filmCard.addEventHandler(KeyEvent.KEY_PRESSED,(event) -> {if(event.getCode().equals(KeyCode.ENTER)) openFilm(filmCard.getMovieDb());});
        });
        scrollPane.setContent(filmContainer);
        this.handleLoading(false);
        showCurrent();
    }
    /**
     * Apre la film view del film selezionato
     * @param movieDb Il film contenuto nella film view
     */
    private void openFilm(MovieDb movieDb)
    {
        FilmHandler.getInstance().selectFilm(movieDb.getId());
        SceneHandler.getInstance().loadFilmScene();
    }
    /**
     * Carica la prossima o la pagina precedente
     * @param positive Se bisogna aumentare o diminuire la currentPage di 1
     */
    private void loadNext(boolean positive) {
        ResearchHandler.getInstance().updateCurrentPage(positive);
    }
    @Override
    public void OnResearchStarted()
    {
        this.handleLoading(true);
    }
    /**
     * Gestisce l'evento OnResearchSuccessed
     * @param result I film trovati nella ricerca
     * @param isGenre Se la ricerca è effettuata solamente su generi e sorting
     */
    @Override
    public void OnResearchSuccessed(List<MovieDb> result,boolean isGenre)
    {
        if(!isGenre)
            genreList.clearList();
        else
        {
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
    /**
     * Gestisce la schermata di caricamento
     * @param value Se bisogna mostrare la schermata di caricamento
     */
    private void handleLoading(boolean value)
    {
        this.scrollPane.setDisable(value);
        this.bottomHolder.setDisable(value);
        this.filmsProgress.setDisable(!value);
    }
    /**
     * Gestisce un qualsiasi errore
     * @param errorName Testo da mostrare all'utente
     * @param errorButton Testo del pulsante da mostrare all'utente
     * @param eventHandler Come gestire l'interazione con il pulsante
     */
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
    /**
     * Gestisce l'evento OnResearchFailed
     */
    @Override
    public void OnResearchFailed(boolean connection)
    {
        showCurrent();
        EventHandler<ActionEvent> eventHandler = (event) -> {
            genreList.clearList();
            if(ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.HOME)
                ResearchHandler.getInstance().setCurrentListType(MovieListType.MOST_POPULAR);
            else
            {
                if(FilmHandler.getInstance().getCurrentLoaded().isEmpty())
                    ResearchHandler.getInstance().setCurrentViewMode(MovieViewMode.HOME,false,true,false);
                else
                    ResearchHandler.getInstance().setCurrentGenre(genreList.getSelectedIndexes(),true);
            }
        };
        if(connection)
        {
            handleError("connectionError.name","errorButton.name",eventHandler);
            return;
        }
        handleError(ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.LIBRARY && FilmHandler.getInstance().getCurrentLoaded().isEmpty() ? "emptyLibrary.name" : "errorText.name","errorButton.name",eventHandler);
    }
    //Gestisce il cambiamento tra home e libreria
    public void handleSwitchedView()
    {
        bottomHolder.setVisible(ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.HOME);
        searchField.setPromptText(StyleHandler.getInstance().getLocalizedString("textPrompt.name"));
        searchField.setText("");
        if(ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.LIBRARY)
        {
            genreList.clearList();
            third.setVisible(false);
            if(FilmHandler.getInstance().getCurrentLoaded().isEmpty())
                handleError("emptyLibrary.name","backHome.name",(event) -> ResearchHandler.getInstance().setCurrentViewMode(MovieViewMode.HOME,false,true,false));
            else
                createFilms(FilmHandler.getInstance().getCurrentLoaded());
        }
        else
        {
            third.setVisible(true);
            ResearchHandler.getInstance().setCurrentListType(MovieListType.MOST_POPULAR);
        }
    }
}