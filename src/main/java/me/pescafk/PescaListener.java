package me.pescafk;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class PescaListener implements Listener {

    PescaAFK pl;

    public PescaListener(PescaAFK pl) {
        this.pl = pl;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) return;
        if (!(event.getInventory().getHolder() instanceof PescaGUIHolder)) return;
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null || item.equals(Material.AIR)) return;
        if (!item.hasItemMeta()) return;
        switch (item.getItemMeta().getDisplayName()) {
            case "Peixes":

                if (pl.getAfkHashMap().get(player.getUniqueId()) != null &&  pl.getAfkHashMap().get(player.getUniqueId()).isStealFishing()){
                    player.sendMessage(ChatColor.RED + "Você não pode abrir a bolsa enquanto está no afk!");
                    player.closeInventory();
                    return;
                }
                player.openInventory(new PescaGUI(pl).getBagGUI(player));
                break;
            case "Top Player":

                break;

        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        pl.getPlayersConfigurations().add(new PlayersConfiguration(player.getUniqueId(),pl));
        pl.getAfkHashMap().put(player.getUniqueId(),new SystemAFK(player.getUniqueId(),pl));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        pl.getAfkHashMap().remove(player.getUniqueId());
    }

    @EventHandler
    public void onFish(PlayerFishEvent event){
        Player player = event.getPlayer();

        FishHook fishHook = event.getHook();
        fishHook.getLocation();

        if (event.getState().equals(PlayerFishEvent.State.FISHING)){
            pl.getPlayerFishing().put(player.getUniqueId(),event.getHook());
        }else {
            pl.getPlayerFishing().remove(player.getUniqueId());
        }



    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();

        if (inv.getTitle().equals(player.getDisplayName())) {
            e.setCancelled(true);

            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            player.getInventory().addItem(clicked);

            inv.remove(clicked);

            PlayersConfiguration config = pl.getPlayerConfig(player);
            short type = clicked.getDurability();
            int oldAmount = config.getConfig().getInt("bag.RAW_FISH.type." + type + ".amount");
            int newAmount = oldAmount - clicked.getAmount();
            if (newAmount <= 0) {
                config.getConfig().set("bag.RAW_FISH.type." + type + ".amount", null);
            } else {
                config.getConfig().set("bag.RAW_FISH.type." + type + ".amount", newAmount);
            }
            config.saveConfig();
        }
    }

}
