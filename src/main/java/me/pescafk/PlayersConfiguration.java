package me.pescafk;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayersConfiguration {
    private UUID uuid;
    private PescaAFK pl;
    private File file;
    private FileConfiguration config;

    public PlayersConfiguration(UUID uuid,PescaAFK pl) {
        this.uuid =  uuid;
        this.pl = pl;
        file = new File(pl.getDataFolder().getAbsolutePath() + File.separator + "players",uuid.toString()+".yml");
        notExists();
        config = YamlConfiguration.loadConfiguration(file);
        config.addDefault("time_fishing",0);
        config.addDefault("fishes",0);
        config.options().copyDefaults(true);
        saveConfig();
    }

    private void notExists(){
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveConfig() {
        try {
            config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public UUID getUuid() {
        return uuid;
    }
}
