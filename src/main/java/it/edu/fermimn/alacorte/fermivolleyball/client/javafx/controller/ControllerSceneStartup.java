package it.edu.fermimn.alacorte.fermivolleyball.client.javafx.controller;

import it.edu.fermimn.alacorte.fermivolleyball.FermiVolleyBall;
import it.edu.fermimn.alacorte.fermivolleyball.client.Client;
import it.edu.fermimn.alacorte.fermivolleyball.client.javafx.JFXDefs;
import it.edu.fermimn.alacorte.fermivolleyball.client.javafx.alert.ErrorAlert;
import it.edu.fermimn.alacorte.fermivolleyball.client.javafx.scene.SceneLoading;
import it.edu.fermimn.alacorte.fermivolleyball.client.javafx.scene.SceneMenu;
import it.italiandudes.idl.common.FileHandler;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.idl.common.SQLiteHandler;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;

@SuppressWarnings("unused")
public final class ControllerSceneStartup {

    //Attributes
    private boolean isDBLocal;

    //Graphic Elements
    @FXML private BorderPane mainPane;
    @FXML private TextField dbURLTextField;
    @FXML private Button fileChooserButton;
    @FXML private Button startButton;
    @FXML private Button newLocalDBButton;
    @FXML private CheckBox localDBCheckBox;

    //Initialize
    @FXML
    private void initialize(){
        isDBLocal = false;
        Client.getStage().setResizable(false);
        ImageView fileChooserView = new ImageView(JFXDefs.Resource.get(JFXDefs.Resource.Image.IMAGE_FILE_EXPLORER).toString());
        fileChooserView.setFitWidth(fileChooserButton.getPrefWidth());
        fileChooserView.setFitHeight(fileChooserButton.getHeight());
        fileChooserView.setPreserveRatio(true);
        fileChooserButton.setGraphic(fileChooserView);
    }

    //EDT
    @FXML
    private void handleOnDragOver(DragEvent event){
        if(event.getDragboard().hasFiles()){
            event.acceptTransferModes(TransferMode.COPY);
        }
    }
    @FXML
    private void handleOnDragDropped(DragEvent event){
        if(event.getDragboard().hasFiles()){
            String path = event.getDragboard().getFiles().get(0).getAbsolutePath();
            File fp = new File(path);
            newLocalDBButton.setDisable(!fp.exists() || !fp.isFile());
            dbURLTextField.setText(path);
            event.setDropCompleted(true);
        }else{
            event.setDropCompleted(false);
        }
    }
    @FXML
    private void dbFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona il Database");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fermi VolleyBall DB", "*."+JFXDefs.AppInfo.DB_FILE_EXTENSION));
        File jarDirectory = new File(FermiVolleyBall.Defs.JAR_POSITION).getParentFile();
        if(jarDirectory.isDirectory()){
            fileChooser.setInitialDirectory(jarDirectory);
        }
        File fileDB = fileChooser.showOpenDialog(fileChooserButton.getScene().getWindow());
        if(fileDB!=null) {
            newLocalDBButton.setDisable(!fileDB.exists() || !fileDB.isFile());
            dbURLTextField.setText(fileDB.getAbsolutePath());
        }
    }
    @FXML
    private void switchDBFilePosition() {
        isDBLocal = localDBCheckBox.isSelected();
        if(isDBLocal) {
            dbURLTextField.setPromptText("Inserisci il percorso completo al database");
            startButton.setText("Apri file esistente");
            fileChooserButton.setDisable(false);
        }else {
            dbURLTextField.setPromptText("Inserisci indirizzo e porta del server");
            startButton.setText("Connettiti al Server");
            fileChooserButton.setDisable(true);
        }
    }
    @FXML
    private void handleStartButton() {
        if(dbURLTextField.getText() == null || dbURLTextField.getText().equals("")) {
            new ErrorAlert("ERRORE", "Errore di Input", "La barra di testo e' vuota");
        }else {
            Client.setIsDBRemote(!isDBLocal);
            if (isDBLocal) openLocalDB();
            else connectToServer();
        }
    }
    private void connectToServer() {
        Scene thisScene = dbURLTextField.getScene();
        Client.getStage().setScene(SceneLoading.getScene());
        Service<Void> connectToServerService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        String domain;
                        int port;
                        String[] splitText = dbURLTextField.getText().split(":");
                        if(splitText.length!=2) {
                            Logger.log("The provided address doesn't respect the format <address>:<port>");
                            Platform.runLater(() -> {
                                Client.getStage().setScene(thisScene);
                                new ErrorAlert("ERRORE", "Errore di Input", "L'indirizzo inserito non rispetta il formato <indirizzo>:<porta>");
                            });
                            return null;
                        }

                        domain = splitText[0];
                        try {
                            port = Integer.parseInt(splitText[1]);
                            if(port<=0) throw new NumberFormatException();
                        }catch (NumberFormatException e){
                            Logger.log("The provided port isn't an integer between 1 and 65535");
                            Platform.runLater(() -> {
                                Client.getStage().setScene(thisScene);
                                new ErrorAlert("ERRORE", "Errore di Input", "La porta inserita non e' un valore numero intero compreso tra 1 e 65535");
                            });
                            return null;
                        }
                        Socket serverConnection = null;

                        try{
                            serverConnection = new Socket(domain, port);
                        }catch (UnknownHostException unknownHostException) {
                            Platform.runLater(() -> {
                                Client.getStage().setScene(thisScene);
                                new ErrorAlert("ERRORE", "Errore di Connessione", "L'indirizzo inserito non e' raggiungibile");
                            });
                        }catch (IOException ioException) {
                            Logger.log("An error has occurred during server connection");
                            Platform.runLater(() -> {
                                Client.getStage().setScene(thisScene);
                                new ErrorAlert("ERRORE", "Errore di Connessione", "Errore durante la connessione col server");
                            });
                        }

                        if(serverConnection == null) {
                            return null;
                        }

                        if(!Client.setServerConnection(serverConnection)) {
                            try {
                                serverConnection.close();
                            }catch (Exception ignored){}
                            Logger.log("There is an already open connection with the server");
                            Platform.runLater(() -> {
                                Client.getStage().setScene(thisScene);
                                new ErrorAlert("ERRORE", "Errore di Connessione", "E' gia' presente una connessione verso un server");
                            });
                            return null;
                        }

                        Platform.runLater(() -> Client.getStage().setScene(SceneMenu.getScene()));
                        return null;
                    }
                };
            }
        };
        connectToServerService.start();
    }
    private void openLocalDB() {
        Scene thisScene = dbURLTextField.getScene();
        Client.getStage().setScene(SceneLoading.getScene());
        Service<Void> attemptDBConnectionThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        String dbPath = dbURLTextField.getText();
                        assert dbPath!=null && dbPath.equals("");
                        File fileChecker = new File(dbPath);
                        if(!fileChecker.exists() || !fileChecker.isFile()){
                            Logger.log("The provided path doesn't exist or doesn't bring to an existing file");
                            Platform.runLater(() -> {
                                Client.getStage().setScene(thisScene);
                                new ErrorAlert("ERRORE", "Errore inserimento DB", "Il percorso inserito non esiste oppure non e' un file! Inserire un percorso a un file valido");
                            });
                            return null;
                        }
                        if(!FileHandler.getFileExtension(dbPath).equals(JFXDefs.AppInfo.DB_FILE_EXTENSION)){
                            Logger.log("The provided path doesn't respect the format "+JFXDefs.AppInfo.DB_FILE_EXTENSION);
                            Platform.runLater(() -> {
                                Client.getStage().setScene(thisScene);
                                new ErrorAlert("ERRORE", "Errore inserimento DB", "Il file inserito non e' di formato "+JFXDefs.AppInfo.DB_FILE_EXTENSION +"! Inserire un percorso a un file valido");
                            });
                            return null;
                        }
                        Connection dbConnection = SQLiteHandler.openConnection(dbURLTextField.getText());

                        if(dbConnection==null){
                            Logger.log("An error has occurred during database connection");
                            Platform.runLater(() -> {
                                Client.getStage().setScene(thisScene);
                                new ErrorAlert("ERRORE", "Errore connessione DB", "Si e' verificato un errore durante la connessione al database");
                            });
                            return null;
                        }

                        if(!Client.setDbConnection(dbConnection)){
                            Logger.log("There is an already open connection with the database");
                            Platform.runLater(() -> {
                                Client.getStage().setScene(thisScene);
                                new ErrorAlert("ERRORE", "Errore di connessione DB", "C'e' gia' una connessione aperta con il database");
                            });
                            return null;
                        }
                        Platform.runLater(() -> Client.getStage().setScene(SceneMenu.getScene()));
                        return null;
                    }
                };
            }
        };
        attemptDBConnectionThread.start();
    }
    @FXML
    private void createNewLocalDB() {} //TODO: createNewLocalDB()

}