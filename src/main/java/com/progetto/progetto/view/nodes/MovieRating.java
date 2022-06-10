package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.StyleHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class MovieRating extends HBox
{
    private final float rating;

    public MovieRating(float rating)
    {
        this.rating = rating;
        this.setAlignment(Pos.CENTER);
        this.init();
    }
    private void init()
    {
        this.setSpacing(2);
        String value = String.valueOf(rating);
        String[] values = value.split("\\.");
        int firstPart = Integer.parseInt(values[0])/2;
        int secondPart = Integer.parseInt(values[1]);
        for (int i = 0; i < firstPart; i++)
            this.getChildren().add(StyleHandler.getInstance().createIcon("mdi2s-star",22));
        if (values[1].length() == 1 && Integer.parseInt(values[1]) >= 5 || values[1].length() == 2 && Integer.parseInt(values[1]) >= 50) {
            this.getChildren().add(StyleHandler.getInstance().createIcon("mdi2s-star-half-full",22));
        }
    }
    public final double getRating() {return rating;}
}
