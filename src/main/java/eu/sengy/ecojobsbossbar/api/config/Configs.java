package eu.sengy.ecojobsbossbar.api.config;

import eu.sengy.ecojobsbossbar.EcoJobsBossbar;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Configs {

    private static YamlConfiguration config;
    private static File configFile;
    private static YamlConfiguration messages;
    private static File messagesFile;

    public static void init(EcoJobsBossbar pl) {
        configFile = new File(pl.getDataFolder(), "config.yml");
        if (!configFile.exists()) pl.saveResource("config.yml", false);
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("Error loading config file: " + e.getMessage());
        }


        messagesFile = new File(pl.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) pl.saveResource("messages.yml", false);
        messages = new YamlConfiguration();
        try {
            messages.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("Error loading messages file: " + e.getMessage());
        }
    }

    public static YamlConfiguration getConfig(){
        return config;
    }

    public static void reloadConfig() throws InvalidConfigurationException, IOException {
        config.load(configFile);
    }

    public static void saveConfig() throws IOException {
        config.save(configFile);
    }

    public static YamlConfiguration getMessages(){
        return messages;
    }

    public static void reloadMessages() throws IOException, InvalidConfigurationException {
        messages.load(messagesFile);
    }

}
