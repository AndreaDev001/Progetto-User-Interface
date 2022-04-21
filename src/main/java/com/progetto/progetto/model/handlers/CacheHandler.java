package com.progetto.progetto.model.handlers;

import com.progetto.progetto.view.SceneHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class CacheHandler {

    private static final CacheHandler handler = new CacheHandler();
    private CacheHandler(){}

    public static CacheHandler getInstance() {
        return handler;
    }

    //THIS IS THE MEMORY CACHE
    private final Map<String, Image> IMAGE_CACHE = new HashMap<>();
    private final Map<Integer, VBox> VBOX_CACHE = new HashMap<>();


    public Image getImage(String url)
    {
        Image image = IMAGE_CACHE.get(url);

        if(image == null)
        {
            image = new Image(url,true);
            IMAGE_CACHE.put(url,image);
        }
        return image;
    }
    public VBox getFilmBox(Integer id,String title,String language,String path)
    {
        VBox vBox = VBOX_CACHE.get(id);
        if(vBox == null)
        {
            Image image = getImage(path);
            ImageView imageView = new ImageView(image);
            Label label = new Label(title);
            vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setPrefWidth(30);
            vBox.setPrefHeight(30);
            vBox.setMinWidth(Region.USE_COMPUTED_SIZE);
            vBox.setMinHeight(Region.USE_COMPUTED_SIZE);
            vBox.setMaxWidth(Region.USE_COMPUTED_SIZE);
            vBox.setMaxHeight(Region.USE_COMPUTED_SIZE);
            vBox.setFillWidth(true);
            label.getStyleClass().add("card-label");
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            vBox.setStyle("-fx-cursor: hand");
            vBox.getChildren().add(imageView);
            vBox.getChildren().add(label);
            vBox.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> {
                FilmHandler.getInstance().selectFilm(id,language);
                SceneHandler.getInstance().loadFilmScene();
            });
            VBOX_CACHE.put(id,vBox);
        }
        return vBox;
    }
}
