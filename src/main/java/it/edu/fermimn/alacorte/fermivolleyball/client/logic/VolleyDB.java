package it.edu.fermimn.alacorte.fermivolleyball.client.logic;

import it.edu.fermimn.alacorte.fermivolleyball.client.Client;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.idl.common.RawSerializer;
import it.italiandudes.idl.common.SQLiteHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public final class VolleyDB {

    @Nullable
    public static ResultSet readFromDB(@NotNull String query) {
        if(Client.isDBRemote()) {
            if(Client.getServerConnection() == null) return null;
            try {
                RawSerializer.sendString(Client.getServerConnection().getOutputStream(), query);
                return (ResultSet) RawSerializer.receiveObject(Client.getServerConnection().getInputStream());
            }catch (IOException | ClassNotFoundException e) {
                Logger.log(e);
                return null;
            }
        }else {
            if(Client.getDbConnection() == null) return null;
            return SQLiteHandler.readDataFromDB(Client.getDbConnection(), query);
        }
    }
    @Nullable
    public static ResultSet writeIntoDB(@NotNull String query) {
        if(Client.isDBRemote()) {
            if(Client.getServerConnection() == null) return null;
            try {
                RawSerializer.sendString(Client.getServerConnection().getOutputStream(), query);
                return (ResultSet) RawSerializer.receiveObject(Client.getServerConnection().getInputStream());
            }catch (IOException | ClassNotFoundException e) {
                Logger.log(e);
                try {
                    Client.getServerConnection().close();
                }catch (Exception ignored) {}
                Client.setServerConnection(null);
                return null;
            }
        }else {
            if(Client.getDbConnection() == null) return null;
            try {
                return SQLiteHandler.prepareDataWriteIntoDB(Client.getDbConnection(), query).executeQuery();
            }catch (SQLException e) {
                Logger.log(e);
                try {
                    Client.getDbConnection().close();
                }catch (Exception ignored) {}
                Client.setDbConnection(null);
                return null;
            }
        }
    }

}
