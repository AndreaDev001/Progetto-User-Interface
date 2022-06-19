package com.progetto.progetto.view.nodes;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

//Un Picker per raccogliere colori data una tonalità
public class ColorBarPicker extends Pane
{
    //the hue is double value between 0 and 360
    private final DoubleProperty hue = new SimpleDoubleProperty(0.0D);

    private static final double DISABLE_DURATION = 300D;
    private double scrollSpeed = 0;

    public ColorBarPicker()
    {
        this.getStyleClass().add("color-bar");
        this.setBackground(new Background(new BackgroundFill(createHueGradient(), CornerRadii.EMPTY, Insets.EMPTY)));

        Region colorBarIndicator = new Region();
        colorBarIndicator.setId("color-bar-indicator");
        colorBarIndicator.prefHeightProperty().bind(this.prefHeightProperty());
        colorBarIndicator.setMouseTransparent(true);
        colorBarIndicator.setCache(true);
        colorBarIndicator.layoutXProperty().bind(this.hue.divide(360).multiply(this.widthProperty()).subtract(colorBarIndicator.widthProperty().divide(2)));
        colorBarIndicator.visibleProperty().bind(disableProperty().not());

        minHeightProperty().bind(prefHeightProperty());

        EventHandler<MouseEvent> mouseHandler = (pos) -> {
            double x = pos.getX();
            this.hue.setValue(clamp(x / this.widthProperty().get(),1.0D) * 360.0D);
        };

        this.setOnKeyPressed(keyEvent ->
        {
            double value = hue.getValue();
            boolean left = keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.DOWN;
            boolean right = keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.UP;
            value += left ? -scrollSpeed : (right ? scrollSpeed : 0);
            value = clamp(value,360.0D);
            hue.setValue(value);
            scrollSpeed *= 1.1D;
        });
        this.setOnKeyReleased(keyEvent -> scrollSpeed = 1);


        this.setOnMouseDragged(mouseHandler);
        this.setOnMousePressed(mouseHandler);
        this.getChildren().setAll(colorBarIndicator);

        //ANIMATION SECTION
        this.disableProperty().addListener((observableValue, aBoolean, t1) ->
        {
            KeyFrame keyFrame = new KeyFrame(Duration.millis(DISABLE_DURATION),new KeyValue(prefHeightProperty(),t1 ? 0 : getMaxHeight(), Interpolator.EASE_IN));
            Timeline timeline = new Timeline(keyFrame);
            timeline.play();
        });

    }

    //javafx usa standard di formattazione per identificare delle property modificabili dal file fxml
    public DoubleProperty hueProperty() { return this.hue; }
    public final double getHue() { return this.hue.get(); }
    public final void setHue(double hue) { this.hue.setValue(hue);}

    //this method generates a gradient containing every Hue color
    private LinearGradient createHueGradient() {
        Stop[] var2 = new Stop[255];

        for(int var3 = 0; var3 < 255; ++var3) {
            double var0 = 1.0D - 0.00392156862745098D * (double)var3;
            int var4 = (int)((double)var3 / 255.0D * 360.0D);
            var2[var3] = new Stop(var0, Color.hsb(var4, 0.5D, 0.5D));
        }

        return new LinearGradient(1.0D, 0.0D, 0.0D, 0.0D, true, CycleMethod.NO_CYCLE, var2);
    }
    private double clamp(double value,double max)
    {
        return value < 0.0D ? 0.0D : Math.min(value, max);
    }
}
