package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.handlers.ResearchHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;


public class GenreList extends ValueList<String>
{
    private List<CheckBox> checkBoxes;

    public GenreList(List<String> values)
    {
        super(values);
        this.setSpacing(5);
    }
    @Override
    protected void init()
    {
        this.checkBoxes = new ArrayList<>();
        for(String current : values)
        {
            CheckBox checkBox = new CheckBox(current);
            checkBox.setLineSpacing(10);
            checkBox.setAlignment(Pos.CENTER_LEFT);
            checkBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            checkBox.setWrapText(true);
            checkBox.setTooltip(new HelpTooltip("Add genre" + " " + current + " " + "to search", Duration.millis(0),Duration.millis(0),Duration.millis(0),false));
            checkBox.setOnAction((event) -> {
                ResearchHandler.getInstance().setCurrentFilterType(MovieFilterType.GENRE,false);
                System.out.println("Updating");
                ResearchHandler.getInstance().setCurrentGenre(getSelectedIndexes(),true);
            });
            checkBoxes.add(checkBox);
        }
        this.getChildren().addAll(checkBoxes);
    }
    public String getSelectedIndexes()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i < checkBoxes.size();i++)
        {
            if(!checkBoxes.get(i).isSelected())
                continue;
            String current = String.valueOf(i);
            stringBuilder.append(current).append(",");
        }
        return stringBuilder.toString();
    }
    public void clearList()
    {
        for(Node node : this.getChildren())
        {
            if(node instanceof CheckBox current)
            {
                current.setSelected(false);
            }
        }
    }
    public List<CheckBox> getCheckBoxes() {return checkBoxes;}
}