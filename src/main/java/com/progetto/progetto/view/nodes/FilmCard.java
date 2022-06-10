package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class FilmCard extends VBox
{
    private final MovieDb movieDb;
    private ImageView imageView;
    private Label titleLabel;
    private Label releaseDateLabel;

    public FilmCard(MovieDb movieDb)
    {
        this.movieDb = movieDb;
        this.init();
    }
    private void init()
    {
        Image image = CacheHandler.getInstance().getImage(FilmHandler.getInstance().getPosterPath(movieDb));
        imageView = new ImageView(image);
        titleLabel = new Label(movieDb.getTitle());
        releaseDateLabel = new Label(movieDb.getReleaseDate());
        this.setAlignment(Pos.CENTER);
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
        this.getChildren().add(new MovieRating(movieDb.getVoteAverage()));
        this.setFocusTraversable(true);
        this.addEventHandler(MouseEvent.MOUSE_ENTERED,(event) -> {
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(5.0);
            dropShadow.setOffsetX(3.0);
            dropShadow.setOffsetY(3.0);
            dropShadow.setColor(Color.valueOf("#ffa200"));
            this.setEffect(dropShadow);
        });
        this.addEventHandler(MouseEvent.MOUSE_EXITED,(event) -> this.setEffect(null));
    }

    public final MovieDb getMovieDb() { return this.movieDb; }
    public final ImageView getImageView() {return imageView;}
    public final Label getTitleLabel() {return titleLabel;}
    public final Label getReleaseDateLabel() {return releaseDateLabel;}
}
