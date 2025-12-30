package me.pescafk;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class TopFisher {

    private PescaAFK pl;
    private Collection<Player> players;
    private Player topPlayer;
    private long time;
    private int fishes;

    public TopFisher(PescaAFK pl,Collection<Player> players) {
        this.pl = pl;
        this.players = players;
        getTop(players);
    }

    public void getTop(Collection<Player> players){
        Player top = null;
        long topTime = 0;
        int topFishes = 0;

        for (Player player : players){
            PlayersConfiguration playersConfiguration = pl.getPlayerConfig(player);
            long newTime = playersConfiguration.getConfig().getLong("time_fishing");
            int fishes = playersConfiguration.getConfig().getInt("fishes");
            if (newTime > topTime){
                topTime = newTime;
                top = player;
                topFishes = fishes;
            }
        }
       this.time = topTime;
       this.topPlayer = top;
       this.fishes = topFishes;
    }

    public long getTime() {
        return time;
    }

    public Player getTopPlayer() {
        return topPlayer;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public int getFishes() {
        return fishes;
    }
}
