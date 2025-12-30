package me.pescafk.fishs;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Fish {

    RAW_FISH(Material.RAW_FISH, (short) 0, 60.0),
    SALMON(Material.RAW_FISH, (short) 1, 25.0),
    CLOWN(Material.RAW_FISH, (short)2, 2.0),
    PUFFER(Material.RAW_FISH, (short)3, 13.0);

    Material material;
    short type;
    double chance;

    Fish(Material material, short type, Double chance) {
        this.material = material;
        this.type = type;
        this.chance = chance;
    }

    public short getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public double getChance() {
        return chance;
    }
}
