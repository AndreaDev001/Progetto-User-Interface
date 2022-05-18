package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.handlers.ResearchHandler;
import com.progetto.progetto.view.StyleHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CurrentSearch extends VBox
{
    public CurrentSearch()
    {
        this.setFillWidth(true);
        this.setAlignment(Pos.BOTTOM_LEFT);
        this.init();
    }
    private void init()
    {
        if(ResearchHandler.getInstance().getCurrentListType() != null)
        {
            Label currentList = createLabel(StyleHandler.getInstance().getResourceBundle().getString("currentList.name"),ResearchHandler.getInstance().getCurrentListType().getLocalizedName());
            this.getChildren().add(currentList);
        } else if(ResearchHandler.getInstance().getCurrentText() != null && ResearchHandler.getInstance().getCurrentText().isEmpty())
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
            Label currentSortType = ResearchHandler.getInstance().getCurrentSortType() == null ? new Label("") : createLabel(StyleHandler.getInstance().getResourceBundle().getString("currentSort.name"),StyleHandler.getInstance().getResourceBundle().getString(ResearchHandler.getInstance().getCurrentSortType().toString() + ".name"));
            Label currentSortOrder = ResearchHandler.getInstance().getCurrentSortOrder() == null ? new Label("") : createLabel(StyleHandler.getInstance().getResourceBundle().getString("currentOrder.name"),StyleHandler.getInstance().getResourceBundle().getString(ResearchHandler.getInstance().getCurrentSortOrder().toString() + ".name"));
            this.getChildren().addAll(currentGenre,currentSortType,currentSortOrder);
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
        result.setAlignment(Pos.CENTER);
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
