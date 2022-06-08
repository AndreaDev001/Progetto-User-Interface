package com.progetto.progetto.view;

public enum PageEnum {
    MAIN("MainView.fxml"),
    SETTINGS("SettingsView.fxml"),
    LOGIN("LoginView.fxml");

    private final String fxml;

    PageEnum(String fxml) {
        this.fxml = fxml;
    }

    public String getFxml() {
        return fxml;
    }
}
