package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.StyleHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class EnumList <T extends Enum<T>> extends VBox
{
    private final T[] values;
    private final List<Label> labels = new ArrayList<>();

    public EnumList(T[] values)
    {
        this.setAlignment(Pos.TOP_LEFT);
        this.setSpacing(10);
        this.values = values;
        this.init();
    }
    private void init()
    {
        for(T current : values)
        {
            Label label = new Label(StyleHandler.getInstance().getLocalizedString(current.toString() + ".name"));
            label.setFocusTraversable(true);
            label.setWrapText(true);
            labels.add(label);
            this.getChildren().add(label);
        }
    }
    public List<Label> getLabels() {return labels;}
    public final T[] getValues() {return values;}
}
