package com.progetto.progetto.view.nodes;

import com.progetto.progetto.view.StyleHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;


public class HelpTooltip extends Tooltip
{
    public HelpTooltip(String text,Duration showDelay,Duration showDuration, Duration hideDelay, boolean useResources)
    {
        this.setText(useResources ? StyleHandler.getInstance().getResourceBundle().getString(text) : text);
        this.setTextAlignment(TextAlignment.CENTER);
        this.setWrapText(true);
        this.setShowDelay(showDelay);
        this.setShowDuration(showDuration);
        this.setHideDelay(hideDelay);
    }
}
