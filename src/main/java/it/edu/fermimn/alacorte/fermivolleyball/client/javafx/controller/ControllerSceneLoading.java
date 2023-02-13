package it.edu.fermimn.alacorte.fermivolleyball.client.javafx.controller;

import it.edu.fermimn.alacorte.fermivolleyball.client.Client;
import it.edu.fermimn.alacorte.fermivolleyball.client.javafx.JFXDefs;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class ControllerSceneLoading {

    //Attributes
    @NotNull
    private static final Image GIF_LOADING = new Image(JFXDefs.Resource.get(JFXDefs.Resource.GIF.GIF_LOADING).toString());

    //FXML Elements
    @FXML private ImageView loadingView;


    //Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(false);
        loadingView.setImage(GIF_LOADING);
    }

}