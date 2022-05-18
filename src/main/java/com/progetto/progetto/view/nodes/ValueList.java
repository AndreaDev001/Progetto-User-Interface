package com.progetto.progetto.view.nodes;

import com.progetto.progetto.view.StyleHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ValueList<T> extends VBox
{
    protected List<T> values = new ArrayList<>();
    protected final List<Label> labels = new ArrayList<>();

    public ValueList(List<T> values)
    {
        this.values = values;
        this.setSpacing(5);
        this.init();
    }
    protected void init()
    {
        for(T current : values)
        {
            String value = StyleHandler.getInstance().getResourceBundle().getString(current.toString() + ".name");
            Label label = new Label(value);
            label.setStyle("-fx-font-weight: 300;-fx-font-size: 14px");
            label.setAlignment(Pos.CENTER);
            label.setWrapText(true);
            labels.add(label);
        }
        this.getChildren().addAll(labels);
    }
    public final List<Label> getLabels() {return labels;}
    public final List<T> getValues() {return values;}
}
