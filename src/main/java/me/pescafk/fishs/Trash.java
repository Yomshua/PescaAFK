package me.pescafk.fishs;

import org.bukkit.Material;

public enum Trash{

    BOWL(Material.BOWL,12.0),
    FISHINGH_ROD(Material.FISHING_ROD,2.4),
    LEATHER(Material.LEATHER,12.0),
    ROTTEN_FLESH(Material.ROTTEN_FLESH,12.0),
    STICK(Material.STICK,6.0),
    STRING(Material.STRING,6.0),
    BOTTLE(Material.GLASS_BOTTLE,12.0),
    BONE(Material.BONE,12.0),
    INK_SACK(Material.INK_SACK,1.6),
    TRIPWIRE_HOOK(Material.TRIPWIRE_HOOK,12.0),
    LEATHER_BOOTS(Material.LEATHER_BOOTS,12.0);

    Material material;
    double chance;

    Trash(Material material, double chance){
        this.material = material;
        this.chance = chance;
    }

    public Material getMaterial() {
        return material;
    }

    public double getChance() {
        return chance;
    }
}
