package com.progetto.progetto.view.nodes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import org.kordamp.ikonli.javafx.FontIcon;

public class CheckLabel extends Label
{

    private static final String CHECK_ICON_TRUE = "mdi2c-checkbox-marked-circle";
    private static final String CHECK_ICON_FALSE = "fas-times-circle";

    private final BooleanProperty checked = new SimpleBooleanProperty();


    public CheckLabel() {
        setGraphic(new FontIcon());
        ((FontIcon)getGraphic()).setIconLiteral(CHECK_ICON_FALSE);
        checkedProperty().addListener((observableValue, aBoolean, t1) -> ((FontIcon)getGraphic()).setIconLiteral(t1 ? CHECK_ICON_TRUE : CHECK_ICON_FALSE));
    }

    public BooleanProperty checkedProperty() { return checked; }
    public final boolean getChecked() { return this.checkedProperty().get(); }
    public final void setChecked(boolean checked) { this.checkedProperty().setValue(checked);}

 }
