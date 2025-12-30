package me.pescafk;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.security.auth.login.Configuration;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PescaGUI {

    PescaAFK pl;

    public PescaGUI(PescaAFK pl) {
        this.pl = pl;
    }

    public Inventory getMainGUI() {
        Inventory gui = Bukkit.createInventory(new PescaGUIHolder(), 27, "PescaAFK");
        gui.setItem(12, getGameSkull("Peixes", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDlmMWYwN2UyYjFjMzJiYjY0YTEyOGU1MjlmM2FmMWU1Mjg2ZTUxODU0NGVkZjhjYmFhNmM0MDY1YjQ3NmIifX19"));
        List<String> lore = new ArrayList<>();
        TopFisher topFisher = new TopFisher(pl, (Collection<Player>) Bukkit.getOnlinePlayers());
        lore.add("");

        lore.add(topFisher.getTopPlayer() == null
                ? ChatColor.RED + "Nenhum player tem tempos salvos!"
                : ChatColor.WHITE + "▪ " + ChatColor.GRAY + "TopPlayer : " + ChatColor.YELLOW + topFisher.getTopPlayer().getDisplayName());
        lore.add(ChatColor.WHITE + "▪ " + ChatColor.GRAY + "Peixes pescados : " + ChatColor.YELLOW + topFisher.getFishes());
        lore.add(ChatColor.WHITE + "▪ " + ChatColor.GRAY + "Tempo pescando : " + ChatColor.YELLOW + SystemAFK.converterTempo(topFisher.getTime()));
        ItemStack topPlayer = getGameSkull("TopPlayer", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY4ZjRjZWY5NDlmMzJlMzNlYzVhZTg0NWY5YzU2OTgzY2JlMTMzNzVhNGRlYzQ2ZTViYmZiN2RjYjYifX19", lore);
        gui.setItem(14, topPlayer);
        return gui;
    }

    public Inventory getBagGUI(Player player) {
        Inventory gui = Bukkit.createInventory(player, 54, player.getDisplayName());
        PlayersConfiguration config = pl.getPlayerConfig(player);
        ConfigurationSection section = config.getConfig().getConfigurationSection("bag.RAW_FISH.type");
        for (String key : section.getKeys(false)) {
            Short type = Short.valueOf(key);
            int amount = section.getInt(type + ".amount");
            if (amount == 0) continue;
            ItemStack stack = new ItemStack(Material.RAW_FISH, amount, type);
            gui.addItem(stack);
        }
        return gui;
    }

    private ItemStack getGameSkull(String name, String base64) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("texture", base64));
        try {
            Field field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        meta.setDisplayName(name);
        skull.setItemMeta(meta);
        return skull;
    }

    private ItemStack getGameSkull(String name, String base64, List<String> lore) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("texture", base64));
        try {
            Field field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        meta.setDisplayName(name);
        meta.setLore(lore);
        skull.setItemMeta(meta);
        return skull;
    }

}
