package com.minegusta.mgracesredone.races;

import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.playerdata.MGPlayer;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.util.ItemUtil;
import com.minegusta.mgracesredone.util.PotionUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Dwarf implements Race {
    @Override
    public double getHealth() {
        return 24;
    }

    @Override
    public String getName() {
        return "Dwarf";
    }

    @Override
    public String[] getInfectionInfo() {
        return new String[]
                {
                        "To become a dwarf, follow these steps:",
                        "Gather 2 each of the following ores: Diamond, Emerald, Lapis and Gold.",
                        "Place all these ores close together with a diamond ore centered.",
                        "Craft a Shiny Gem (/Race Recipes).",
                        "Right click the diamond ore with your gem.",
                        "The ore shall vaporize and change the structure of your blood.",
                        "You are now a dwarf."
                };
    }

    @Override
    public String[] getInfo() {
        return new String[]
                {
                        "Dwarves are the proud people of the mountain halls.",
                        "They prefer to live underground, and mine a lot.",
                        "Dwarves are weak to arrows.",
                        "Most Dwarven perks focus on mining and axes."

                };
    }

    @Override
    public String getColoredName() {
        return ChatColor.DARK_GRAY + getName();
    }

    @Override
    public int getPerkPointCap() {
        return 26;
    }

    @Override
    public void passiveBoost(Player p) {
        double height = p.getLocation().getY();
        MGPlayer mgp = Races.getMGPlayer(p);

        int tunnlerLevel = mgp.getAbilityLevel(AbilityType.TUNNLER);

        if (height < 50 && tunnlerLevel > 0) {
            if (height < 26) {
                if (tunnlerLevel > 1) {
                    PotionUtil.updatePotion(p, PotionEffectType.FIRE_RESISTANCE, 0, 5);
                }
            }
            PotionUtil.updatePotion(p, PotionEffectType.DAMAGE_RESISTANCE, 0, 5);
            if (tunnlerLevel > 2) PotionUtil.updatePotion(p, PotionEffectType.INCREASE_DAMAGE, 0, 5);
        }

        int minerLevel = mgp.getAbilityLevel(AbilityType.MINER);

        if (minerLevel > 0 && ItemUtil.isPickAxe(p.getInventory().getItemInMainHand().getType())) {
            PotionUtil.updatePotion(p, PotionEffectType.FAST_DIGGING, 2, 5);
        }
    }
}
