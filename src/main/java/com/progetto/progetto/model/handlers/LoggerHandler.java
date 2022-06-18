package com.progetto.progetto.model.handlers;


import com.progetto.progetto.model.Options;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

public class LoggerHandler
{
    private static Logger logger = Logger.getLogger("FILM LOGGER");

    static
    {
        try
        {
            //create folder directory if not existent
            Files.createDirectories(Path.of(Options.APP_FOLDER_LOCATION));
            logger.setUseParentHandlers(false);
            FileHandler fileHandler = new FileHandler(Options.APP_FOLDER_LOCATION + File.separator + "filmLog.log",true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e)
        {
            System.out.println("LOGGER INITIALISATION FAILED! NO ERRORS WILL BE LOGGED DURING THIS SESSION!");
            logger = null;
        }
    }
    public static void log(Level level,String message,Throwable throwable,Object... objects)
    {
        if(logger == null)
            return;

        if (!logger.isLoggable(Level.SEVERE)) {
            return;
        }
        for(Object o : objects)
            message = message.replaceFirst("\\{}",o.toString());

        LogRecord lr = new LogRecord(Level.SEVERE, message);
        lr.setParameters(objects);
        lr.setThrown(throwable);
        logger.log(lr);
    }

    public static void error(String message,Throwable throwable,Object... objects)
    {
        log(Level.SEVERE,message,throwable,objects);
    }

}
