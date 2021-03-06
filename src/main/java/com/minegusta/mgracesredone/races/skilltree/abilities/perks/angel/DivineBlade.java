package com.minegusta.mgracesredone.races.skilltree.abilities.perks.angel;

import com.google.common.collect.Lists;
import com.minegusta.mgracesredone.main.Main;
import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.playerdata.MGPlayer;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import com.minegusta.mgracesredone.util.EffectUtil;
import com.minegusta.mgracesredone.util.PotionUtil;
import com.minegusta.mgracesredone.util.WeaknessUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class DivineBlade implements IAbility {

    @Override
    public void run(Event event) {

    }

    @Override
    public boolean run(Player player) {
        MGPlayer mgp = Races.getMGPlayer(player);
        int level = mgp.getAbilityLevel(getType());
        int duration = 9;
        //Level 4
        if (level > 3) {
            duration = 18;
        }

        //Level 2
        if (level > 1) {
            PotionUtil.updatePotion(player, PotionEffectType.SPEED, 0, duration);
            PotionUtil.updatePotion(player, PotionEffectType.INCREASE_DAMAGE, 0, duration);
            PotionUtil.updatePotion(player, PotionEffectType.JUMP, 1, duration);
            //Level 3
            if (level > 2) {
                PotionUtil.updatePotion(player, PotionEffectType.SPEED, 0, duration);
                PotionUtil.updatePotion(player, PotionEffectType.INCREASE_DAMAGE, 1, duration);
                PotionUtil.updatePotion(player, PotionEffectType.JUMP, 2, duration);
            }
        }

        for (int i = 0; i < duration; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
                if (!player.isOnline()) return;

                player.getWorld().spigot().playEffect(player.getLocation(), Effect.VILLAGER_PLANT_GROW, 0, 0, 1, 1, 1, 1, 12, 40);
                EffectUtil.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                WeaknessUtil.removeWeakness(player);
                for (PotionEffect e : player.getActivePotionEffects()) {
                    if (PotionUtil.isNegativeForPlayer(e.getType())) {
                        player.removePotionEffect(e.getType());
                    }
                }

            }, i * 20);

        }

        return true;
    }

    @Override
    public String getName() {
        return "Divine Blade";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.DIVINEBLADE;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.GOLD_SWORD;
    }

    @Override
    public int getPrice(int level) {
        return 2;
    }

    @Override
    public AbilityGroup getGroup() {
        return AbilityGroup.ACTIVE;
    }

    @Override
    public int getCooldown(int level) {
        return 99;
    }

    @Override
    public List<RaceType> getRaces() {
        return Lists.newArrayList(RaceType.ANGEL);
    }

    @Override
    public boolean canBind() {
        return true;
    }

    @Override
    public String getBindDescription() {
        return "Remove any negative potion effects. Add good ones.";
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public String[] getDescription(int level) {
        String[] desc;

        switch (level) {
            case 1:
                desc = new String[]{"Cleanse yourself of any negative effects.", "Lasts for 9 seconds.", "Bind using /Bind."};
                break;
            case 2:
                desc = new String[]{"Adds Speed Jump and Strength buffs to you now."};
                break;
            case 3:
                desc = new String[]{"Your Jump and Strength boost become stronger."};
                break;
            case 4:
                desc = new String[]{"The duration is increased to 18 seconds."};
                break;
            default:
                desc = new String[]{"This is an error!"};
                break;

        }
        return desc;
    }
}
