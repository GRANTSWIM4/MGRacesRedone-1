package com.minegusta.mgracesredone.races.skilltree.abilities.perks.angel;


import com.google.common.collect.Lists;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;

public class Holiness implements IAbility {
    @Override
    public void run(Event event) {

    }

    @Override
    public boolean run(Player player) {
        return true;
    }

    @Override
    public String getName() {
        return "Holiness";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.HOLINESS;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.POTION;
    }

    @Override
    public int getPrice(int level) {
        return 1;
    }

    @Override
    public AbilityGroup getGroup() {
        return AbilityGroup.PASSIVE;
    }

    @Override
    public int getCooldown(int level) {
        return 0;
    }

    @Override
    public List<RaceType> getRaces() {
        return Lists.newArrayList(RaceType.ANGEL);
    }

    @Override
    public boolean canBind() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public String[] getDescription(int level) {
        String[] desc;

        switch (level) {
            case 1:
                desc = new String[]{"You will no longer take fall damage."};
                break;
            case 2:
                desc = new String[]{"You will get a healing boost in light areas."};
                break;
            case 3:
                desc = new String[]{"You will no longer get de-buffs in the dark in high areas."};
                break;
            case 4:
                desc = new String[]{"When up in the sky, you will get a defence, speed and jump boost."};
                break;
            case 5:
                desc = new String[]{"When on low health, you gain a Speed and protection boost."};
                break;
            default:
                desc = new String[]{"This is an error!"};
                break;

        }
        return desc;
    }
}