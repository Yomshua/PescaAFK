package me.pescafk;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class PescaCommand implements CommandExecutor {

    private PescaAFK pl;

    public PescaCommand(PescaAFK pl) {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) return true;
        Player player = ((Player) commandSender).getPlayer();
        Set<UUID> players = pl.getPlayersAFK();
        HashMap<UUID,SystemAFK> afkHashMap = pl.getAfkHashMap();
        SystemAFK afk = afkHashMap.get(player.getUniqueId());

        if (args.length == 0){
            player.sendMessage(ChatColor.BLUE +  "------------ PescaAFK ------------");
            player.sendMessage("");
            player.sendMessage(ChatColor.BLUE +  "/pesca iniciar - inicia a pesca afk");
            player.sendMessage(ChatColor.BLUE +  "/pesca parar - desativa a pesca afk");
            player.sendMessage(ChatColor.BLUE +  "/pesca menu - abre o menu de pesca");
            player.sendMessage("");
            player.sendMessage(ChatColor.BLUE +  "---------------------------------");
            return true;
        }

        String subcommand = args[0];
        switch (subcommand) {
            case "iniciar":
                    if (!players.contains(player.getUniqueId())) {
                        if (afk == null) {
                            afkHashMap.put(player.getUniqueId(), new SystemAFK(player.getUniqueId(), pl));
                            afk = afkHashMap.get(player.getUniqueId());
                        }
                        afk.enable();
                    } else {
                        player.sendMessage(ChatColor.RED + "Você já está na pesca afk!");
                    }
                    break;
            case "parar":
                if (players.contains(player.getUniqueId())) {
                    players.remove(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "Você saiu da pesca afk!");
                    afk.disable();
                    afkHashMap.remove(player.getUniqueId());
                } else {
                    player.sendMessage(ChatColor.RED + "Você não está na pesca afk! Digite /pesca iniciar para participar!");
                }
                break;

            case "menu":
                player.openInventory(new PescaGUI(pl).getMainGUI());
                break;
        }


        return true;
    }
}
