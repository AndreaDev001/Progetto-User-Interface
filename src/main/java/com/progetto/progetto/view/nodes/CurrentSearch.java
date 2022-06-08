package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.enums.MovieViewMode;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.handlers.ResearchHandler;
import com.progetto.progetto.model.handlers.StyleHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CurrentSearch extends VBox
{
    public CurrentSearch()
    {
        this.setAlignment(Pos.CENTER);
        this.init();
    }
    private void init()
    {
        if(ResearchHandler.getInstance().getCurrentViewMode() != MovieViewMode.LIBRARY && ResearchHandler.getInstance().getCurrentListType() != null)
        {
            Label currentList = createLabel(StyleHandler.getInstance().getLocalizedString("currentList.name"),ResearchHandler.getInstance().getCurrentListType().getLocalizedName());
            this.getChildren().add(currentList);
        }
        else if(ResearchHandler.getInstance().getCurrentText() != null && ResearchHandler.getInstance().getCurrentText().isEmpty())
        {
            StringBuilder stringBuilder = new StringBuilder();
            boolean value = ResearchHandler.getInstance().getCurrentGenre() != null && !ResearchHandler.getInstance().getCurrentGenre().isEmpty();
            if(value)
            {
                String[] values = ResearchHandler.getInstance().getCurrentGenre().split(",");
                for(int i = 0;i < values.length;i++)
                {
                    stringBuilder.append(FilmHandler.getInstance().getGenres().get(Integer.parseInt(values[i])));
                    if(i != values.length - 1)
                        stringBuilder.append(",");
                }
                Label currentGenre = createLabel(StyleHandler.getInstance().getLocalizedString("currentGenre.name"), stringBuilder.toString());
                this.getChildren().add(currentGenre);
            }
            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.setAlignment(Pos.CENTER);
            if(ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.LIBRARY || value)
            {
                Label currentSortType = ResearchHandler.getInstance().getCurrentSortType() == null ? new Label("") : createLabel(StyleHandler.getInstance().getLocalizedString("currentSort.name"),StyleHandler.getInstance().getLocalizedString(ResearchHandler.getInstance().getCurrentSortType().toString() + ".name"));
                Label currentSortOrder = ResearchHandler.getInstance().getCurrentSortOrder() == null ? new Label("") : createLabel(StyleHandler.getInstance().getLocalizedString("currentOrder.name"),StyleHandler.getInstance().getLocalizedString(ResearchHandler.getInstance().getCurrentSortOrder().toString() + ".name"));
                hBox.getChildren().addAll(currentSortType,currentSortOrder);
            }
            this.getChildren().add(hBox);
        }
        else
        {
            Label currentText = createLabel(StyleHandler.getInstance().getLocalizedString("currentSearch.name"),ResearchHandler.getInstance().getCurrentText());
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
