package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.enums.MovieFilterType;
import com.progetto.progetto.model.handlers.ResearchHandler;
import com.progetto.progetto.model.handlers.StyleHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


public class GenreList extends VBox
{
    private final List<String> values;
    private List<CheckBox> checkBoxes;

    /**
     * Costruttore della classe GenreList
     * @param values La lista dei nomi dei generi
     */
    public GenreList(List<String> values)
    {
        this.values = values;
        this.setSpacing(5);
        this.init();
    }

    /**
     * Inizializza il componente
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
            checkBox.setTooltip(new Tooltip(StyleHandler.getInstance().getLocalizedString("firstTool.name") + " " + current + " " + StyleHandler.getInstance().getLocalizedString("secondTool.name")));
            checkBox.setOnAction((event) -> {
                ResearchHandler.getInstance().setCurrentFilterType(MovieFilterType.GENRE,false);
                ResearchHandler.getInstance().setCurrentGenre(getSelectedIndexes(),true);
            });
            checkBoxes.add(checkBox);
        }
        if(ResearchHandler.getInstance().getCurrentGenre() != null && !ResearchHandler.getInstance().getCurrentGenre().isEmpty())
        {
            String[] strings = ResearchHandler.getInstance().getCurrentGenre().split(",");
            for(String current : strings)
                this.getCheckBoxes().get(Integer.parseInt(current)).setSelected(true);
        }
        this.getChildren().addAll(checkBoxes);
    }

    /**
     * Ottiene tutti gli indici delle checkbox selezionate
     * @return Una Stringa contenente tutti gli indici,separati da una virgola
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
     * Pulisce la lista
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
