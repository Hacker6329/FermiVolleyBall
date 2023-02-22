package it.edu.fermimn.alacorte.fermivolleyball.client.javafx.controller;

import it.edu.fermimn.alacorte.fermivolleyball.client.Client;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

@SuppressWarnings("unused")
public final class ControllerSceneMainMenu {

    //Attributes

    //FXML Elements
    @FXML
    private AnchorPane mainPane;


    //Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(false);
    }

}
