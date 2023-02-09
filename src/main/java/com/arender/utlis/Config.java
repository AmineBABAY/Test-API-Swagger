package com.arender.utlis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class Config
{

    public static Properties prop;

    public static String file;

    public static String url;

    public static int numberOfUsers;

    public static long durationOfTest;

    public Config()
    {
        try
        {
            prop = new Properties();
            File src = new File("./src/main/resources/settings/config.properties");
            FileInputStream ip = new FileInputStream(src);
            prop.load(ip);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Properties file not found !");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void remoteConfig()
    {
        url = System.getProperty("URL");
        file = System.getProperty("file");
        numberOfUsers = Integer.parseInt(System.getProperty("number_of_users"));
        durationOfTest = Long.parseLong(System.getProperty("durationOfTest"));
    }

    public static void localConfig()
    {
        url = prop.getProperty("URL");
        file = prop.getProperty("file");
        numberOfUsers = Integer.parseInt(prop.getProperty("number_of_users"));
        durationOfTest = Long.parseLong(prop.getProperty("durationOfTest"));
    }

    public static JSONObject readJsonFile(String nameOfFile)
    {
        String filepath = System.getProperty("user.dir") + "/src/main/resources/jsonsbody/" + nameOfFile + ".json";
        String jsonString;
        JSONObject json = null;
        try
        {
            FileReader reader = new FileReader(filepath);
            BufferedReader bufferedReader = new BufferedReader(reader);
            jsonString = bufferedReader.lines().collect(Collectors.joining());
            bufferedReader.close();
            reader.close();
            json = new JSONObject(jsonString);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return json;
    }

}
