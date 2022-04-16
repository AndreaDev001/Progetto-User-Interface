package com.progetto.progetto.model.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHandler
{
    private static RegexHandler instance = new RegexHandler();

    private RegexHandler()
    {
        System.out.println("Instance of regex handler created correctly");
    }
    public boolean match(String value,String regex)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    public static RegexHandler getInstance() {return instance;}
}
