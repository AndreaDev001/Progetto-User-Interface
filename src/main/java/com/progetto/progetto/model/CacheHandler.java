package com.progetto.progetto.model;

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






}
