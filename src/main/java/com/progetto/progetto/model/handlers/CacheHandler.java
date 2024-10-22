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
    private final int MAX_SIZE = 150;

    /**
     * Crea una nuova image se non è già presente nella CACHE
     * @param url L'url della immagine da creare
     * @return L'immagine che è appena stata creata
     */
    public Image getImage(String url)
    {
        if(IMAGE_CACHE.size() >= MAX_SIZE)
            IMAGE_CACHE.clear();
        Image image = IMAGE_CACHE.get(url);
        if(image == null)
        {
            image = new Image(url,true);
            IMAGE_CACHE.put(url,image);
        }
        return image;
    }

    /**
     * Crea una nuova carta se non e' già presente nella CACHE
     * @param id L'id del film di cui bisogna creare la carta
     * @return La carta del film
     */
    public FilmCard getFilmBox(MovieDb id)
    {
        if(CARD_CACHE.size() >= MAX_SIZE)
            CARD_CACHE.clear();
        FilmCard card = new FilmCard(id);
        CARD_CACHE.put(id,card);
        return card;
    }

    /**
     * Resetta sia la IMAGE_CACHE e la CARD_CACHE
     */
    public void reset()
    {
        IMAGE_CACHE.clear();
        CARD_CACHE.clear();
    }
}
