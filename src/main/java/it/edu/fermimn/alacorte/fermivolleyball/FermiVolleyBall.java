package it.edu.fermimn.alacorte.fermivolleyball;

import it.edu.fermimn.alacorte.fermivolleyball.client.Client;
import it.edu.fermimn.alacorte.fermivolleyball.server.Server;
import it.italiandudes.idl.common.Logger;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.Arrays;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public final class FermiVolleyBall {

    //Attributes
    private static Connection dbConnection = null;

    //Main Method
    public static void main(String[] args) {

        int exitCode;

        //Check if the user wants to run the app even if the Logger initialization fails
        boolean logOnDefaultStreamIfLoggerFail = Arrays.stream(args).
                anyMatch(Predicate.isEqual(Defs.LaunchArgs.LOG_ON_DEFAULT_STREAM_IF_LOGGER_INIT_FAIL));
        if(logOnDefaultStreamIfLoggerFail)
            args = Arrays.stream(args).
                    filter(Predicate.isEqual(Defs.LaunchArgs.LOG_ON_DEFAULT_STREAM_IF_LOGGER_INIT_FAIL)).
                    toArray(String[]::new);

        try{ //Attempt to instantiate the Logger
            if(!Logger.init()){
                System.err.println("An unknown error has occurred during Logger initialization.");
                if(!logOnDefaultStreamIfLoggerFail)
                    System.exit(Defs.ReturnCodes.LOGGER_INIT_ERROR);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(Logger::close));
        }catch (Exception e) {
            System.err.println("An exception has occurred during Logger initialization.");
            e.printStackTrace();
            if(!logOnDefaultStreamIfLoggerFail)
                System.exit(Defs.ReturnCodes.LOGGER_INIT_ERROR);
        }

        if(Arrays.stream(args).anyMatch(Predicate.isEqual(Defs.LaunchArgs.START_SERVER))){ //Start the server
            args = Arrays.stream(args).
                    filter(Predicate.isEqual(Defs.LaunchArgs.START_SERVER)).
                    toArray(String[]::new);
            exitCode = Server.start(args);
        }else{ //Start the client in graphic-mode
            exitCode = Client.start(args);
        }

        Logger.log("Application terminating with code: "+exitCode);

        System.exit(exitCode);
    }

    //Methods
    @Nullable
    public static Connection getDbConnection() {
        return dbConnection;
    }
    public static boolean setDbConnection(@Nullable Connection dbConnection) {
        if(FermiVolleyBall.dbConnection != null) return false;
        FermiVolleyBall.dbConnection = dbConnection;
        return true;
    }

    //App Generic Constants
    public static class Defs {

        public static final String JAR_POSITION = FermiVolleyBall.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1);

        //Launch Arguments
        public static final class LaunchArgs {
            public static final String LOG_ON_DEFAULT_STREAM_IF_LOGGER_INIT_FAIL = "-LogOnDefaultStreamIfLoggerInitFail";
            public static final String START_SERVER = "-server";
        }

        //Return Codes
        public static final class ReturnCodes {

            //<0 -> Pre-Launch Errors
            //>0 -> Post-Launch Errors

            //Pre Launch Codes
            public static final int LOGGER_INIT_ERROR = -100;

            //Post-Launch Codes
        }

    }

}
