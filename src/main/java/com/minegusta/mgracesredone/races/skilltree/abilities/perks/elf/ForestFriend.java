package com.minegusta.mgracesredone.races.skilltree.abilities.perks.elf;

import com.google.common.collect.Lists;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;

public class ForestFriend implements IAbility {
    @Override
    public void run(Event event) {

    }

    @Override
    public void run(Player player) {

    }

    @Override
    public String getName() {
        return "Forest Friend";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.FORESTFRIEND;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.YELLOW_FLOWER;
    }

    @Override
    public int getPrice(int level) {
        return 1;
    }

    @Override
    public List<RaceType> getRaces() {
        return Lists.newArrayList(RaceType.ELF);
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
                desc = new String[]{""};
                break;
            case 2:
                desc = new String[]{"BLAAAAA", "bla"};
                break;
            case 3:
                desc = new String[]{"In forest biomes, you will get a speed II and jump II boost during the day."};
                break;
            case 4:
                desc = new String[]{"unlockable shizzle woooo", " :D!!!!!! "};
                break;
            case 5:
                desc = new String[]{"Meow?", "blablabla"};
                break;
            default:
                desc = new String[]{"*-o", "YARR HARR"};
                break;

        }
        return desc;
    }
}
