package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.CacheHandler;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.view.SceneHandler;
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

    /**
     * Constructor of the FilmCard class
     * @param movieDb The movie we need to create the card for
     */
    public FilmCard(MovieDb movieDb)
    {
        this.movieDb = movieDb;
        this.init();
    }

    /**
     * Inits the components,finds the image of the movie,adds some layout constraints and adds css classes,creates an effect when going hover a card
     */
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
            if(SceneHandler.getInstance().getFilmStage().isIconified() && FilmHandler.getInstance().getCurrentSelectedFilm() == this.movieDb.getId())
            {
                SceneHandler.getInstance().centerStage(SceneHandler.getInstance().getFilmStage(), SceneHandler.getInstance().getFilmStage().getWidth(),SceneHandler.getInstance().getFilmStage().getHeight());
                SceneHandler.getInstance().getFilmStage().setIconified(false);
                return;
            }
            FilmHandler.getInstance().selectFilm(movieDb.getId());
            SceneHandler.getInstance().loadFilmScene();
        });
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
    public final ImageView getImageView() {return imageView;}
    public final Label getTitleLabel() {return titleLabel;}
    public final Label getReleaseDateLabel() {return releaseDateLabel;}
}
