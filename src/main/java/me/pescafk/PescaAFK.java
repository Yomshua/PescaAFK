package me.pescafk;


import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class PescaAFK extends JavaPlugin {

    private Set<UUID> playersAFK = new HashSet<>();
    private HashMap<UUID, SystemAFK> afkHashMap = new HashMap<>();
    private HashMap<UUID, FishHook> playerFishing = new HashMap<>();
    private HashMap<UUID, Inventory> bagsMap = new HashMap<>();
    private Set<PlayersConfiguration> playersConfigurations = new HashSet<>();

    @Override
    public void onEnable() {
        getCommand("pesca").setExecutor(new PescaCommand(this));
        getServer().getPluginManager().registerEvents(new PescaListener(this), this);
        createFiles();
        for (Player player : Bukkit.getOnlinePlayers()){
            playersConfigurations.add(new PlayersConfiguration(player.getUniqueId(),this));
        }
    }

    @Override
    public void onDisable() {
        for (SystemAFK systemAFK : afkHashMap.values()){
            PlayersConfiguration playersConfiguration = systemAFK.getPlayersConfiguration();
            for (Map.Entry<Short, Integer> entry : systemAFK.getFishIntegerList().entrySet()) {
                short type = entry.getKey();
                int amount = entry.getValue();

                String path = "bag.RAW_FISH.type." + type + ".amount";
                playersConfiguration.getConfig().set(
                        path,
                        playersConfiguration.getConfig().getInt(path) + amount
                );
            }
            playersConfiguration.saveConfig();

        }


    }

    public Set<UUID> getPlayersAFK() {
        return playersAFK;
    }

    public HashMap<UUID, SystemAFK> getAfkHashMap() {
        return afkHashMap;
    }

    public HashMap<UUID, FishHook> getPlayerFishing() {
        return playerFishing;
    }

    public HashMap<UUID, Inventory> getBagsMap() {
        return bagsMap;
    }

    public Set<PlayersConfiguration> getPlayersConfigurations() {
        return playersConfigurations;
    }

    private void createFiles() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder().getAbsolutePath() + File.separator + "players");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public PlayersConfiguration getPlayerConfig(Player player){
        UUID uuid = player.getUniqueId();
        for (PlayersConfiguration config : playersConfigurations){
            if (config.getUuid().equals(player.getUniqueId())){
                return config;
            }
        }
        return null;
    }

}
