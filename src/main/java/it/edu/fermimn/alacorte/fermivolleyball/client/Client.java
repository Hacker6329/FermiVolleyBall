package it.edu.fermimn.alacorte.fermivolleyball.client;

import it.edu.fermimn.alacorte.fermivolleyball.client.javafx.JFXDefs;
import it.edu.fermimn.alacorte.fermivolleyball.client.javafx.scene.SceneStartup;
import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public final class Client extends Application {

    //Attributes
    private static Stage stage;

    @Override
    public void start(Stage stage) {
        Client.stage = stage;
        stage.setTitle(JFXDefs.AppInfo.NAME);
        stage.getIcons().add(JFXDefs.AppInfo.LOGO);
        stage.setScene(SceneStartup.getScene());
        stage.show();
    }

    //Start Methods
    public static int start(String[] args){
        launch(args);
        return 0;
    }

    //Methods
    @NotNull
    public static Stage getStage(){
        return stage;
    }

}
