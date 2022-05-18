package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.view.SceneHandler;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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
        this.addEventHandler(MouseEvent.MOUSE_CLICKED,event ->{
            FilmHandler.getInstance().selectFilm(movieDb.getId());
            SceneHandler.getInstance().loadFilmScene();
        });
        this.getStyleClass().add("card");
        this.getChildren().add(imageView);
        this.getChildren().add(titleLabel);
        this.getChildren().add(releaseDateLabel);
        this.setFocusTraversable(true);
    }
    public final ImageView getImageView() {return imageView;}
    public final Label getTitleLabel() {return titleLabel;}
    public final Label getReleaseDateLabel() {return releaseDateLabel;}
}
