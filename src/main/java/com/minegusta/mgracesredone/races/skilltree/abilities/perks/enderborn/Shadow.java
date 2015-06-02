package com.minegusta.mgracesredone.races.skilltree.abilities.perks.enderborn;


import com.google.common.collect.Lists;
import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.playerdata.MGPlayer;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import com.minegusta.mgracesredone.util.ChatUtil;
import com.minegusta.mgracesredone.util.Cooldown;
import com.minegusta.mgracesredone.util.ShadowInvisibility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;

public class Shadow implements IAbility
{
    @Override
    public void run(Event event) {

    }

    @Override
    public void run(Player player)
    {
        String uuid = player.getUniqueId().toString();
        MGPlayer mgp = Races.getMGPlayer(player);
        int level = mgp.getAbilityLevel(getType());
        String name = "shadow";

        if(ShadowInvisibility.contains(uuid))
        {
            ShadowInvisibility.remove(uuid);
            ChatUtil.sendString(player, "You are no longer invisible!");
            return;
        }

        if(!Cooldown.isCooledDown(name, uuid))
        {
            ChatUtil.sendString(player, "You need to wait another " + Cooldown.getRemaining(name, uuid) + " seconds to use " + getName() + ".");
            return;
        }

        if(level < 2 && player.getLocation().getBlock().getLightLevel() > 6)
        {
            ChatUtil.sendString(player, "You cannot use invisibility in light areas.");
            return;
        }

        ChatUtil.sendString(player, "You are now invisible!");
        Cooldown.newCoolDown(name, uuid, getCooldown(level));

        ShadowInvisibility.add(uuid);




    }

    @Override
    public String getName() {
        return "Shadow";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.SHADOW;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.TORCH;
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
        if(level > 2) return 4;
        return 8;
    }

    @Override
    public List<RaceType> getRaces() {
        return Lists.newArrayList(RaceType.ENDERBORN);
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
                desc = new String[]{"You can toggle invisibility in dark areas.", "Activate by crouching once.", "You will leave a dark shadow on the floor.", "When hit, your invisibility ends.", "You can activate it once every 8 seconds."};
                break;
            case 2:
                desc = new String[]{"You can now toggle invisibility in lighter areas."};
                break;
            case 3:
                desc = new String[]{"Your cooldown is reduced to 4 seconds."};
                break;
            case 4:
                desc = new String[]{"When invisible, you gain a speed and jump boost."};
                break;
            default:
                desc = new String[]{"This is an error!"};
                break;

        }
        return desc;
    }
}