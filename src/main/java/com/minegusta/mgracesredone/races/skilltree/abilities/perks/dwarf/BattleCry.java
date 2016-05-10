package com.minegusta.mgracesredone.races.skilltree.abilities.perks.dwarf;


import com.google.common.collect.Lists;
import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.playerdata.MGPlayer;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import com.minegusta.mgracesredone.util.ChatUtil;
import com.minegusta.mgracesredone.util.EffectUtil;
import com.minegusta.mgracesredone.util.PotionUtil;
import com.minegusta.mgracesredone.util.WGUtil;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class BattleCry implements IAbility {

    @Override
    public void run(Event event) {

    }

    @Override
    public boolean run(Player player) {
        //Standard data needed.
        MGPlayer mgp = Races.getMGPlayer(player);
        int level = mgp.getAbilityLevel(getType());

        //Worldguard?
        if (!WGUtil.canBuild(player)) {
            ChatUtil.sendString(player, "You cannot use " + getName() + " here.");
            return false;
        }

        //Effects
        EffectUtil.playParticle(player, Effect.VILLAGER_THUNDERCLOUD);
        EffectUtil.playSound(player, Sound.BLOCK_ANVIL_USE);

        //The variables
        boolean strength = level > 3;
        boolean weaken = level > 2;
        boolean stun = level > 1;
        double knockbackPower = 1.9;
        if (level > 4) knockbackPower = 2.5;

        //Run the ability
        if (strength) PotionUtil.updatePotion(player, PotionEffectType.INCREASE_DAMAGE, 1, 4);

        for (Entity ent : player.getNearbyEntities(5.0, 5.0, 5.0)) {
            if (!(ent instanceof LivingEntity)) continue;

            LivingEntity le = (LivingEntity) ent;

            if (!WGUtil.canFightEachother(player, ent)) continue;

            if (le instanceof Player) {
                le.sendMessage(ChatColor.RED + "You were knocked back by an angry dwarf!");
            }
            EffectUtil.playSound(le, Sound.BLOCK_ANVIL_USE);
            EffectUtil.playParticle(le, Effect.CRIT);

            //Launch the entity
            le.setVelocity(le.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(knockbackPower));

            //weaken
            if (weaken) PotionUtil.updatePotion(le, PotionEffectType.WEAKNESS, 1, 6);

            //stun
            if (stun) PotionUtil.updatePotion(le, PotionEffectType.SLOW, 10, 4);
        }
        return true;
    }

    @Override
    public String getName() {
        return "Battle Cry";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.BATTLECRY;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.IRON_AXE;
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
        if (level > 4) return 50;
        return 80;
    }

    @Override
    public List<RaceType> getRaces() {
        return Lists.newArrayList(RaceType.DWARF);
    }

    @Override
    public boolean canBind() {
        return true;
    }

    @Override
    public String getBindDescription() {
        return "Knock back enemies with your battlecry.";
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
                desc = new String[]{"Yell to intimidate enemies, knocking them back.", "Bind using /Bind."};
                break;
            case 2:
                desc = new String[]{"Affected enemies are now stunned for 4 seconds."};
                break;
            case 3:
                desc = new String[]{"Enemies are weakened for 6 seconds."};
                break;
            case 4:
                desc = new String[]{"You gain a strength boost for 4 seconds."};
                break;
            case 5:
                desc = new String[]{"The knock-back effect is 50% stronger.."};
                break;
            default:
                desc = new String[]{"This is an error!"};
                break;

        }
        return desc;
    }
}
