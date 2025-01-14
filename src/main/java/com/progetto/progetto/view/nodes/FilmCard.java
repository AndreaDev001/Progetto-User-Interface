package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.handlers.StyleHandler;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class FilmCard extends VBox
{
    private final MovieDb movieDb;
    private ImageView imageView;
    private Label titleLabel;
    private Label releaseDateLabel;

    /**
     * Costruttore della classe FilmCard
     * @param movieDb Il film della carta
     */
    public FilmCard(MovieDb movieDb)
    {
        this.movieDb = movieDb;
        this.init();
    }

    /**
     * Inizializza il componente
     */
    private void init()
    {
        Image image = CacheHandler.getInstance().getImage(FilmHandler.getInstance().getPosterPath(movieDb));
        imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        titleLabel = new Label(movieDb.getTitle());
        releaseDateLabel = new Label(movieDb.getReleaseDate() == null || movieDb.getReleaseDate().isEmpty() ? StyleHandler.getInstance().getLocalizedString("missingRelease.name") : movieDb.getReleaseDate());
        this.setAlignment(Pos.TOP_CENTER);
        this.setPrefSize(100,50);
        this.setMinSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
        this.setMaxSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
        titleLabel.getStyleClass().add("card-label");
        releaseDateLabel.getStyleClass().add("card-label");
        imageView.setFitWidth(135);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        imageView.setFitHeight(150);
        this.getStyleClass().add("card");
        this.getChildren().add(imageView);
        this.getChildren().add(titleLabel);
        this.getChildren().add(releaseDateLabel);
        if(this.movieDb.getReleaseDate() != null && !this.movieDb.getReleaseDate().isEmpty())
            this.getChildren().add(new MovieRating(movieDb.getVoteAverage()));
        this.setFocusTraversable(true);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200));
        scaleTransition.setFromX(1.0D);
        scaleTransition.setFromY(1.0D);
        scaleTransition.setToX(1.1D);
        scaleTransition.setToY(1.1D);
        scaleTransition.setNode(this);


        ChangeListener<Boolean> changeListener = (observableValue, aBoolean, newValue) ->
        {
            scaleTransition.setRate(newValue ? 1D : -1D);
            scaleTransition.play();
            setViewOrder(newValue ? -1 : 0);
        };

        this.hoverProperty().addListener((observableValue, aBoolean, newValue) ->
        {
            changeListener.changed(observableValue,aBoolean,newValue);
            if(newValue)
                requestFocus();
        });
        this.focusedProperty().addListener(changeListener);

    }


    public final MovieDb getMovieDb() { return this.movieDb; }
    public final ImageView getImageView() {return imageView;}
    public final Label getTitleLabel() {return titleLabel;}
    public final Label getReleaseDateLabel() {return releaseDateLabel;}

}
