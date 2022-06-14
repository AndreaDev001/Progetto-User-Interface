package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.enums.MovieListType;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

public class CostumListView extends ListView<String>
{
    public CostumListView()
    {
        for(MovieListType current : MovieListType.values())
            this.getItems().add(current.getLocalizedName());
        this.init();
    }
    private void init()
    {
        this.setCellFactory(param -> new ListCell<>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(!empty && item != null)
                {
                    addEventHandler(MouseEvent.MOUSE_CLICKED,(event) -> {
                        getSelectionModel().clearSelection();
                        getSelectionModel().select(item);
                    });
                    setPrefHeight(Region.USE_COMPUTED_SIZE);
                    setMinWidth(param.getWidth());
                    setMaxWidth(param.getWidth());
                    setPrefWidth(param.getWidth());
                    setWrapText(true);
                    setText(item);
                }
            }
        });
        this.maxHeightProperty().bind(Bindings.size(this.itemsProperty().get()).multiply(55 + 2));
    }
}
