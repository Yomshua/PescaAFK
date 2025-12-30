package me.pescafk;

import me.pescafk.fishs.Fish;
import me.pescafk.fishs.Trash;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class SystemAFK {

    private PescaAFK pl;
    private UUID uuid;
    private Random random;
    private Player player;
    private BukkitTask bukkitTask;
    private boolean stealFishing;
    private BukkitTask isFishingTask;
    private long whenStarted;
    private PlayersConfiguration playersConfiguration;
    private int fishes;
    private HashMap<Short, Integer> fishIntegerList;

    public SystemAFK(UUID uuid, PescaAFK pl) {
        this.uuid = uuid;
        this.pl = pl;
        player = Bukkit.getPlayer(uuid);
        random = new Random();
        playersConfiguration = pl.getPlayerConfig(player);
        fishIntegerList = new HashMap<>();
    }

    public void enable() {
        if (bukkitTask != null) return;
        HashMap<UUID, FishHook> fishHookHashMap = pl.getPlayerFishing();
        FishHook fishHook;
        if (fishHookHashMap.containsKey(player.getUniqueId())) {
            fishHook = fishHookHashMap.get(player.getUniqueId());
        } else {
            player.sendMessage("Você não está pescando!");
            return;
        }

        if (fishHook.getWorld().getBlockAt(fishHook.getLocation()).getType().equals(Material.STATIONARY_WATER)) {
            player.sendMessage(ChatColor.GREEN + "Você começou a pesca afk!");
            pl.getPlayersAFK().add(player.getUniqueId());
            whenStarted = System.currentTimeMillis();
            isFishingTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (pl.getPlayerFishing().containsKey(player.getUniqueId()) && player.getItemInHand().getType().equals(Material.FISHING_ROD)) {
                        stealFishing = true;
                    } else {
                        stealFishing = false;
                        pl.getAfkHashMap().remove(player.getUniqueId());
                        pl.getPlayersAFK().remove(player.getUniqueId());
                        player.sendMessage(ChatColor.RED + "Você não está mais pescando!");

                            PlayersConfiguration playersConfiguration = getPlayersConfiguration();
                            for (Map.Entry<Short, Integer> entry : getFishIntegerList().entrySet()) {
                                short type = entry.getKey();
                                int amount = entry.getValue();

                                String path = "bag.RAW_FISH.type." + type + ".amount";
                                playersConfiguration.getConfig().set(
                                        path,
                                        playersConfiguration.getConfig().getInt(path) + amount
                                );
                            }
                            playersConfiguration.saveConfig();

                        long now = System.currentTimeMillis();
                        long timeFishing = now - whenStarted;

                        long time = playersConfiguration.getConfig().getLong("time_fishing",1);
                        playersConfiguration.getConfig().set("time_fishing",timeFishing + time);

                        playersConfiguration.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Você pescou por: " + converterTempo(timeFishing));

                        cancel();
                    }
                }
            }.runTaskTimer(pl, 0, 5L);
            bukkitTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (stealFishing) {
                        double chance = random.nextDouble() * 100;
                        int id = getFish(player, chance).getTypeId();

                        int fishes = playersConfiguration.getConfig().getInt("fishes");
                        fishes++;
                        playersConfiguration.getConfig().set("fishes", fishes);


                        playersConfiguration.saveConfig();

                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(pl, 0, 20 * 10L);
        }
    }

    public void disable() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }
        isFishingTask.cancel();
        stealFishing = false;
        pl.getAfkHashMap().remove(player.getUniqueId());
        pl.getPlayersAFK().remove(player.getUniqueId());
        long now = System.currentTimeMillis();
            PlayersConfiguration playersConfiguration = getPlayersConfiguration();
            for (Map.Entry<Short, Integer> entry : getFishIntegerList().entrySet()) {
                short type = entry.getKey();
                int amount = entry.getValue();

                String path = "bag.RAW_FISH.type." + type + ".amount";
                playersConfiguration.getConfig().set(
                        path,
                        playersConfiguration.getConfig().getInt(path) + amount
                );
            }
            playersConfiguration.saveConfig();
        long timeFishing = now - whenStarted;

        long time = playersConfiguration.getConfig().getLong("time_fishing",1);
        playersConfiguration.getConfig().set("time_fishing",timeFishing + time);

        playersConfiguration.saveConfig();
        player.sendMessage(ChatColor.GREEN + "Você pescou por: " + converterTempo(timeFishing));

    }

    private void addItem(Player player, Material material) {
        player.getInventory().addItem(new ItemStack(material));
    }

    private void getTrash(Player player, double chance) {
        HashMap<Trash, Double> materialCount = new HashMap<>();
        double lastChance = 0.0;
        for (Trash trash : Trash.values()) {
            lastChance += trash.getChance();
            materialCount.put(trash, lastChance);
        }

        List<Trash> materials = new ArrayList<>();
        Arrays.stream(Trash.values()).forEach((trash -> {
            materials.add(trash);
        }));

        for (int i = 0; i < materialCount.size(); i++) {

            if (i == 0) {
                if (chance <= materialCount.get(materials.get(i))) {
                    player.getInventory().addItem(new ItemStack(materials.get(i).getMaterial()));
                    return;
                }
            } else {
                if (chance <= materialCount.get(materials.get(i)) && chance >= materialCount.get(materials.get(i - 1))) {
                    player.getInventory().addItem(new ItemStack(materials.get(i).getMaterial()));
                    return;
                }
            }


        }

    }

    private ItemStack getFish(Player player, double chance) {
        HashMap<Fish, Double> materialCount = new HashMap<>();
        double lastChance = 0.0;
        for (Fish fish : Fish.values()) {
            lastChance += fish.getChance();
            materialCount.put(fish, lastChance);
        }

        List<Fish> materials = new ArrayList<>();
        Arrays.stream(Fish.values()).forEach((fish -> {
            materials.add(fish);
        }));

        for (int i = 0; i < materialCount.size(); i++) {

            if (i == 0) {
                if (chance <= materialCount.get(materials.get(i))) {
                    short type = (short) materials.get(i).getType();

                    fishIntegerList.put(
                            type,
                            fishIntegerList.getOrDefault(type, 0) + 1
                    );

                    ItemStack item = new ItemStack(
                            materials.get(i).getMaterial(),
                            1,
                            (byte) type
                    );

                    return item;
                }
            } else {
                if (chance <= materialCount.get(materials.get(i)) && chance >= materialCount.get(materials.get(i - 1))) {

                    short type = (short) materials.get(i).getType();

                    fishIntegerList.put(
                            type,
                            fishIntegerList.getOrDefault(type, 0) + 1
                    );

                    ItemStack item = new ItemStack(
                            materials.get(i).getMaterial(),
                            1,
                            (byte) type
                    );

                    return item;
                }
            }


        }
        return null;
    }

    public static String converterTempo(long millis) {
        long segundos = millis / 1000;
        long minutos = segundos / 60;
        long horas = minutos / 60;
        long dias = horas / 24;

        horas = horas % 24;
        minutos = minutos % 60;
        segundos = segundos % 60;

        return dias + " dias, " +
                horas + " horas, " +
                minutos + " minutos e " +
                segundos + " segundos";
    }

    public UUID getUuid() {
        return uuid;
    }

    public HashMap<Short, Integer> getFishIntegerList() {
        return fishIntegerList;
    }

    public int getFishes() {
        return fishes;
    }

    public PlayersConfiguration getPlayersConfiguration() {
        return playersConfiguration;
    }

    public boolean isStealFishing() {
        return stealFishing;
    }
}
