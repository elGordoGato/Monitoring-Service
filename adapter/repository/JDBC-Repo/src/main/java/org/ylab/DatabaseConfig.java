package org.ylab;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    private static String DRIVER;
    private static String URL;
    private static String USER_NAME;
    private static String PASSWORD;

    static {
        try {
            String configFilePath = "../webapps/ServletApp/WEB-INF/classes/application.yml";
            FileInputStream propsInput = new FileInputStream(configFilePath);
            Properties prop = new Properties();
            prop.load(propsInput);

            DRIVER = prop.getProperty("DB_DRIVER");
            URL = prop.getProperty("DATASOURCE_URL");
            USER_NAME = prop.getProperty("DB_USER");
            PASSWORD = prop.getProperty("DB_PASSWORD");
            System.out.println("Retrieved properties from application.yml");
        } catch (IOException e) {
            DRIVER = "org.postgresql.Driver";
            URL = "jdbc:postgresql://meter-db:5432/meterdb";
            USER_NAME = "admin";
            PASSWORD = "admin";
        }
    }

    public static String getDriver() {
        return DRIVER;
    }

    public static String getURL() {
        return URL;
    }

    public static String getUserName() {
        return USER_NAME;
    }

    public static String getPassword() {
        return PASSWORD;
    }
}
