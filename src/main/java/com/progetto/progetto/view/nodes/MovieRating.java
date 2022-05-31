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
        this.rating = rating / 2;
        this.setAlignment(Pos.CENTER);
        this.init();
    }
    private void init()
    {
        this.setSpacing(2);
        for(int i = 0;i < rating;i++)
        {
            Label label = new Label();
            label.setStyle("-fx-font-size: 18px;");
            label.setMinSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
            label.setMaxSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
            label.setPrefSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
            label.setWrapText(true);
            label.setGraphic(new FontIcon("mdi2s-star"));
            this.getChildren().add(label);
        }
    }
    public final double getRating() {return rating;}
}
