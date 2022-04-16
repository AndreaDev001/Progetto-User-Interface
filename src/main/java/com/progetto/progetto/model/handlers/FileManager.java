package com.progetto.progetto.model.handlers;

import com.progetto.progetto.MainApplication;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileManager
{
    private static final FileManager instance = new FileManager();

    private FileManager()
    {
        System.out.println("Instance of File Manager created");
    }
    public List<String> readFile(String fileName)
    {
        List<String> result = new ArrayList<String>();
        URL url = MainApplication.class.getResource(fileName);
        assert url != null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(url.toExternalForm()));
            while(bufferedReader.ready())
            {
                String current = bufferedReader.readLine();
                result.add(current);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public boolean writeFile(String fileName,List<String> content)
    {
        if(content.isEmpty())
            return true;
        URL url = MainApplication.class.getResource(fileName);
        assert url != null;
        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(url.toExternalForm()));
            for(String current : content)
                bufferedWriter.write(current);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public final static FileManager getInstance() {
        return instance;
    }
}
