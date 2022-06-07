package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.handlers.ResearchHandler;
import com.progetto.progetto.view.StyleHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


//Class containing the list of all genres
public class GenreList extends VBox
{
    private final List<String> values;
    private List<CheckBox> checkBoxes;

    /**
     * Constructor of class GenreList
     * @param values Values to use as a text in the checkboxes
     */
    public GenreList(List<String> values)
    {
        this.values = values;
        this.setSpacing(5);
        this.init();
    }

    /**
     * Method used to init the component,for each value creates a new checkbox,applies some layout constraints to it and adds a tooltip
     */
    protected void init()
    {
        this.checkBoxes = new ArrayList<>();
        for(String current : values)
        {
            CheckBox checkBox = new CheckBox(current);
            checkBox.setLineSpacing(10);
            checkBox.setAlignment(Pos.CENTER_LEFT);
            checkBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            checkBox.setWrapText(false);
            checkBox.setTooltip(new Tooltip(StyleHandler.getInstance().getResourceBundle().getString("firstTool.name") + " " + current + " " + StyleHandler.getInstance().getResourceBundle().getString("secondTool.name")));
            checkBox.setOnAction((event) -> {
                ResearchHandler.getInstance().setCurrentFilterType(MovieFilterType.GENRE,false);
                ResearchHandler.getInstance().setCurrentGenre(getSelectedIndexes(),true);
            });
            checkBoxes.add(checkBox);
        }
        this.getChildren().addAll(checkBoxes);
        if(ResearchHandler.getInstance().getCurrentGenre() != null && !ResearchHandler.getInstance().getCurrentGenre().isEmpty())
        {
            String[] strings = ResearchHandler.getInstance().getCurrentGenre().split(",");
            for(String current : strings)
                this.getCheckBoxes().get(Integer.parseInt(current)).setSelected(true);
        }
    }

    /**
     * Method used to get the selected checkboxes
     * @return Returns a string with the indexes of the selected checkbox,each index is separated using a comma
     */
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

    /**
     * Method use to unselect every selected checkbox
     */
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
