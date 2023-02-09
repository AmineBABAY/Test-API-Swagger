package com.arender.utlis;

import org.testng.annotations.BeforeSuite;

public class Initialization extends Config
{

    @BeforeSuite
    public static void init()
    {
        if (prop.getProperty("server").equals("jenkins"))
        {
            Config.remoteConfig();
        }
        else if (prop.getProperty("server").equals("local"))
        {
            Config.localConfig();
        }
        else
        {
            System.err.println("Check your server config");
        }
    }

}
