package it.polimi.ingsw.Model.Utilities;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;

public class Config {

    private static JSONConfig instance = null;

    private Config() { }

    public static synchronized JSONConfig getInstance() {
        if ( instance == null){
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(Config.class.getResourceAsStream("/config.json"));
            instance = gson.fromJson(reader, JSONConfig.class);
        }
        return instance;
    }

}
