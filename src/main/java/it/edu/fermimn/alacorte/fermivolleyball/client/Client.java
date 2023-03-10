package it.edu.fermimn.alacorte.fermivolleyball.client;

import it.edu.fermimn.alacorte.fermivolleyball.client.javafx.JFXDefs;
import it.edu.fermimn.alacorte.fermivolleyball.client.javafx.scene.SceneStartup;
import it.italiandudes.idl.common.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("unused")
public final class Client extends Application {

    //Attributes
    private static Stage stage = null;
    private static boolean isDBRemote = false;
    @Nullable private static Connection dbConnection = null;
    @Nullable private static Socket serverConnection = null;

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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(dbConnection != null) {
                try {
                    dbConnection.close();
                }catch (Exception ignored){}
            }
            if(serverConnection != null) {
                try {
                    serverConnection.close();
                }catch (Exception ignored){}
            }
            try {
                stage.close();
            }catch (Exception ignored){}
        }));
        launch(args);
        return 0;
    }

    //Methods
    @NotNull
    public static Stage getStage(){
        return stage;
    }
    @Nullable
    public static Connection getDbConnection() {
        return dbConnection;
    }
    public static boolean setDbConnection(@Nullable Connection dbConnection) {
        if(dbConnection == null) {
            if(Client.dbConnection == null) {
                return true;
            }
            try {
                if (!Client.dbConnection.isClosed()) {
                    return false;
                } else {
                    Client.dbConnection = null;
                    return true;
                }
            }catch (SQLException e) {
                Logger.log(e);
                try {
                    Client.dbConnection.close();
                }catch (Exception ignored){}
                Client.dbConnection = null;
                return false;
            }
        }
        if(Client.dbConnection == null) {
            Client.dbConnection = dbConnection;
            return true;
        }
        try {
            if (!Client.dbConnection.isClosed()) {
                return false;
            }
        }catch (SQLException e) {
            Logger.log(e);
            try {
                Client.dbConnection.close();
            }catch (Exception ignored){}
            Client.dbConnection = null;
            return false;
        }
        Client.dbConnection = dbConnection;
        return true;
    }
    public static boolean isDBRemote() {
        return isDBRemote;
    }
    public static void setIsDBRemote(boolean isDBRemote) {
        Client.isDBRemote = isDBRemote;
    }
    @Nullable
    public static Socket getServerConnection() {
        return serverConnection;
    }
    public static boolean setServerConnection(@Nullable Socket serverConnection) {
        if(serverConnection == null) {
            if(Client.serverConnection == null) {
                return true;
            }
            if(!Client.serverConnection.isClosed()) {
                return false;
            } else {
                Client.serverConnection = null;
                return true;
            }
        }
        if(Client.serverConnection == null) {
            Client.serverConnection = serverConnection;
            return true;
        }
        if(!Client.serverConnection.isClosed()) {
            return false;
        }
        Client.serverConnection = serverConnection;
        return true;
    }

}
