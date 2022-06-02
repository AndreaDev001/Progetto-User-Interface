package com.progetto.progetto.view.nodes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.kordamp.ikonli.javafx.FontIcon;

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
        for (int i = 0; i < firstPart; i++) {
            FontIcon fontIcon = new FontIcon("mdi2s-star");
            fontIcon.setIconSize(22);
            this.getChildren().add(fontIcon);
        }
        if (values[1].length() == 1 && Integer.parseInt(values[1]) >= 5 || values[1].length() == 2 && Integer.parseInt(values[1]) >= 50) {
            FontIcon fontIcon = new FontIcon("mdi2s-star-half-full");
            fontIcon.setIconSize(22);
            this.getChildren().add(fontIcon);
        }
    }
    public final double getRating() {return rating;}
}
