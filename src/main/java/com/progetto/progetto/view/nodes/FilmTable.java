package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.StyleHandler;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ScrollEvent;

import java.util.List;

public class FilmTable extends TableView<MovieDb>
{
    private final List<MovieDb> movies;
    private final ObservableList<MovieDb> observableList;

    public FilmTable(List<MovieDb> movies)
    {
        this.movies = movies;
        observableList = FXCollections.observableArrayList();
        observableList.addAll(movies);
        init();
    }
    private void init()
    {
        this.setEditable(false);
        this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        this.setItems(observableList);
        TableColumn<MovieDb,String> titleColumn = new TableColumn<>(StyleHandler.getInstance().getLocalizedString("ORIGINAL_TITLE.name"));
        TableColumn<MovieDb,String> releaseColumn = new TableColumn<>(StyleHandler.getInstance().getLocalizedString("RELEASE_DATE.name"));
        TableColumn<MovieDb,String> languageColumn = new TableColumn<>("Original language");
        TableColumn<MovieDb,Float> popularityColumn = new TableColumn<>(StyleHandler.getInstance().getLocalizedString("POPULARITY.name"));
        TableColumn<MovieDb,Float> ratingColumn = new TableColumn<>(StyleHandler.getInstance().getLocalizedString("VOTE_AVERAGE.name"));
        TableColumn<MovieDb,Integer> voteCountColumn = new TableColumn<>(StyleHandler.getInstance().getLocalizedString("VOTE_COUNT.name"));
        this.getColumns().add(titleColumn);
        this.getColumns().add(releaseColumn);
        this.getColumns().add(languageColumn);
        this.getColumns().add(popularityColumn);
        this.getColumns().add(ratingColumn);
        this.getColumns().add(voteCountColumn);
        titleColumn.setCellValueFactory(new PropertyValueFactory<MovieDb,String>("title"));
        releaseColumn.setCellValueFactory(new PropertyValueFactory<MovieDb,String>("releaseDate"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<MovieDb,String>("originalLanguage"));
        popularityColumn.setCellValueFactory(new PropertyValueFactory<MovieDb,Float>("popularity"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<MovieDb,Float>("voteAverage"));
        voteCountColumn.setCellValueFactory(new PropertyValueFactory<MovieDb,Integer>("voteCount"));
        for(TableColumn<MovieDb,?> current : this.getColumns())
        {
            current.setSortable(false);
            current.setReorderable(false);
        }
        this.addEventFilter(ScrollEvent.ANY, Event::consume);

    }
    public final List<MovieDb> getMovies() {return movies;}
}
