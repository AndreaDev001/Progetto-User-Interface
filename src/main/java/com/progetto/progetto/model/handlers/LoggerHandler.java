package com.progetto.progetto.model.handlers;


import java.io.IOException;
import java.util.logging.*;

public class LoggerHandler
{
    private static final Logger logger = Logger.getLogger("FILM LOGGER");

    static
    {
        try
        {
            logger.setUseParentHandlers(false);

            FileHandler fileHandler = new FileHandler("%h/.film_app/filmLog%u.log",true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e)
        {
            logger.severe("Error during logger initialisation");
        }
    }
    public static void log(Level level,String message,Throwable throwable,Object... objects)
    {
        if (!logger.isLoggable(Level.SEVERE)) {
            return;
        }
        for(Object o : objects)
            message = message.replace("{}",o.toString());

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
