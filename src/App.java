package iloslib;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App {

    public String getProperty(String property) {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            String propFileName = "config.properties";
            input = getClass().getClassLoader().getResourceAsStream(propFileName);

            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        };

        return prop.getProperty(property);
    }
}