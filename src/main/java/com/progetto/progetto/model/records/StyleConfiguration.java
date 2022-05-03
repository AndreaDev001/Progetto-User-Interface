package com.progetto.progetto.model.records;

import com.progetto.progetto.view.StyleMode;
import javafx.scene.paint.Color;

public class StyleConfiguration
{
    public StyleMode styleMode = StyleMode.DARK;
    public Color foregroundColor = Color.WHITE;
    public Color backgroundColor = Color.WHITE;
    public Color textColor = Color.BLACK;
    public boolean dyslexic = false;

    public StyleConfiguration(StyleMode styleMode, Color foregroundColor, Color backgroundColor, Color textColor, boolean dyslexic) {
        this.styleMode = styleMode;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.dyslexic = dyslexic;
    }

    public StyleConfiguration()
    {

    }
}
