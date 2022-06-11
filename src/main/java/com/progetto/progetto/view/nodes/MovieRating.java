package com.progetto.progetto.view.nodes;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

//Class used to show the rating of a movie on a scale of five using star icons
public class MovieRating extends HBox
{
    private final float rating;
    private final List<FontIcon> stars = new ArrayList<>();

    /**
     * Constructor of the MovieRating class
     * @param rating The rating of movie,on a scale of 10,will be divided by two
     */
    public MovieRating(float rating)
    {
        this.rating = rating;
        this.setAlignment(Pos.CENTER);
        this.init();
    }
    /**
     * Initialize the component by adding the needed stars,first creates five empty stars
     * then gets the integer part of the rating and adds the same amount of full stars
     * if needed adds a half star as well
     */
    private void init()
    {
        this.setSpacing(2);
        for(int i = 0;i < 5;i++)
        {
            FontIcon fontIcon = new FontIcon();
            fontIcon.setIconLiteral("mdi2s-star-outline");
            fontIcon.setIconSize(23);
            this.stars.add(fontIcon);
            this.getChildren().add(fontIcon);
        }
        String value = String.valueOf(rating);
        String[] values = value.split("\\.");
        int firstPart = Integer.parseInt(values[0])/2;
        for(int i = 0;i < firstPart;i++)
        {
            FontIcon current = this.stars.get(i);
            current.setIconLiteral("mdi2s-star");
        }
        int secondPart = Integer.parseInt(values[1]);
        if (values[1].length() == 1 && secondPart >= 5 || values[1].length() == 2 && secondPart >= 50)
            this.stars.get(firstPart).setIconLiteral("mdi2s-star-half-full");
        if(values[1].length() == 1 && secondPart >= 8 || values[1].length() == 2 && secondPart >= 80)
            this.stars.get(firstPart).setIconLiteral("mdi2s-star");
    }
    public final double getRating() {return rating;}
}
