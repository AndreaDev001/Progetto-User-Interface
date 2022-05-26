package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.handlers.ResearchHandler;
import com.progetto.progetto.view.StyleHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class CurrentSearch extends VBox
{
    public CurrentSearch()
    {
        this.setAlignment(Pos.CENTER);
        this.init();
    }
    private void init()
    {
        if(ResearchHandler.getInstance().getCurrentListType() != null)
        {
            Label currentList = createLabel(StyleHandler.getInstance().getResourceBundle().getString("currentList.name"),ResearchHandler.getInstance().getCurrentListType().getLocalizedName());
            this.getChildren().add(currentList);
        }
        else if(ResearchHandler.getInstance().getCurrentText() != null && ResearchHandler.getInstance().getCurrentText().isEmpty())
        {
            StringBuilder stringBuilder = new StringBuilder();
            if(ResearchHandler.getInstance().getCurrentGenre().isEmpty())
                return;
            String[] values = ResearchHandler.getInstance().getCurrentGenre().split(",");
            for(int i = 0;i < values.length;i++)
            {
                stringBuilder.append(FilmHandler.getInstance().getGenres().get(Integer.parseInt(values[i])));
                if(i != values.length - 1)
                    stringBuilder.append(",");
            }
            Label currentGenre = createLabel(StyleHandler.getInstance().getResourceBundle().getString("currentGenre.name"), stringBuilder.toString());
            currentGenre.setTooltip(new HelpTooltip("Filter by" + " " + stringBuilder.toString(), Duration.millis(0),Duration.millis(0),Duration.millis(0),false));
            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.setAlignment(Pos.CENTER);
            this.getChildren().add(currentGenre);
            Label currentSortType = ResearchHandler.getInstance().getCurrentSortType() == null ? new Label("") : createLabel(StyleHandler.getInstance().getResourceBundle().getString("currentSort.name"),StyleHandler.getInstance().getResourceBundle().getString(ResearchHandler.getInstance().getCurrentSortType().toString() + ".name"));
            Label currentSortOrder = ResearchHandler.getInstance().getCurrentSortOrder() == null ? new Label("") : createLabel(StyleHandler.getInstance().getResourceBundle().getString("currentOrder.name"),StyleHandler.getInstance().getResourceBundle().getString(ResearchHandler.getInstance().getCurrentSortOrder().toString() + ".name"));
            hBox.getChildren().addAll(currentSortType,currentSortOrder);
            this.getChildren().add(hBox);
        }
        else
        {
            Label currentText = createLabel(StyleHandler.getInstance().getResourceBundle().getString("currentSearch.name"),ResearchHandler.getInstance().getCurrentText());
            this.getChildren().add(currentText);
        }
    }
    private Label createLabel(String fieldName,String value)
    {
        Label result = new Label(fieldName + ":" + value);
        result.setWrapText(true);
        result.getStyleClass().add("showCurrent");
        return result;
    }
    public void update()
    {
        this.getChildren().clear();
        this.init();
    }
}
