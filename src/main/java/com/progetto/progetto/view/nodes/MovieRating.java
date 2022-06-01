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
    private void init() {
        this.setSpacing(2);
        String value = String.valueOf(rating);
        String[] values = value.split("\\.");
        int firstPart = Integer.parseInt(values[0])/2;
        int secondPart = Integer.parseInt(values[1]);
        for (int i = 0; i < firstPart; i++) {
            Label label = createLabel(false);
            this.getChildren().add(label);
        }
        if (values[1].length() == 1 && Integer.parseInt(values[1]) >= 5 || values[1].length() == 2 && Integer.parseInt(values[1]) >= 50) {
            Label label = createLabel(true);
            this.getChildren().add(label);
        }
    }
    private Label createLabel(boolean half)
    {
        Label label = new Label();
        label.setStyle("-fx-font-size: 22px");
        label.setMinSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
        label.setMaxSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
        label.setPrefSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
        label.setWrapText(true);
        label.setGraphic(new FontIcon(half ? "mdi2s-star-half-full": "mdi2s-star"));
        return label;
    }
    public final double getRating() {return rating;}
}
