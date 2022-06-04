package com.progetto.progetto.model.handlers;

import com.progetto.progetto.view.nodes.FilmCard;
import info.movito.themoviedbapi.model.MovieDb;
import javafx.scene.image.Image;

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
    private final Map<MovieDb, FilmCard> CARD_CACHE = new HashMap<>();
    private final int MAX_SIZE = 250;


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
    public FilmCard getFilmBox(MovieDb id)
    {
        /*
        if(CARD_CACHE.size() >= MAX_SIZE)
            CARD_CACHE.clear();
        FilmCard card = CARD_CACHE.get(id);
        if(card == null)
        {

         */
        FilmCard card = new FilmCard(id);
        CARD_CACHE.put(id,card);

        return card;
    }
    public void reset()
    {
        IMAGE_CACHE.clear();
        CARD_CACHE.clear();
    }
}
