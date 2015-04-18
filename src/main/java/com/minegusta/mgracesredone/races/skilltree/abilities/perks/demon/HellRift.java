package com.minegusta.mgracesredone.races.skilltree.abilities.perks.demon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minegusta.mgracesredone.main.Main;
import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.playerdata.MGPlayer;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import com.minegusta.mgracesredone.util.EffectUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;

import java.util.List;

public class HellRift implements IAbility {
    @Override
    public void run(Event event)
    {

    }

    @Override
    public void run(Player player)
    {
        MGPlayer mgp = Races.getMGPlayer(player);

        //Get the target a block above the floor.
        Block target = player.getTargetBlock(Sets.newHashSet(Material.AIR), 20).getRelative(0, 2, 0);

        int level = mgp.getAbilityLevel(getType());

        int duration = level * 6;

        if(level > 2)duration = 12;

        boolean explode = level > 2;

        runHellRift(target, duration, explode);


    }

    private void runHellRift(final Block target, int duration, boolean explode)
    {

        final Location l = target.getLocation();
        for(int i = 0; i <= 20 * duration; i++)
        {
            if(i%4 == 0)
            {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                    @Override
                    public void run()
                    {
                        //Effects lol
                        EffectUtil.playParticle(l, Effect.ENDER_SIGNAL);
                        EffectUtil.playParticle(l, Effect.LARGE_SMOKE);
                        EffectUtil.playParticle(l, Effect.LARGE_SMOKE);
                        EffectUtil.playParticle(l, Effect.FLAME);
                        EffectUtil.playParticle(l, Effect.FLYING_GLYPH);
                        EffectUtil.playParticle(l, Effect.LAVADRIP);
                        EffectUtil.playSound(l, Sound.PORTAL);

                        //The sucking people in effect
                        Entity dummy = l.getWorld().spawnEntity(l, EntityType.SMALL_FIREBALL);

                        for(Entity ent : dummy.getNearbyEntities(15,15,15))
                        {
                            if(ent instanceof LivingEntity || ent instanceof Item)
                            {
                                //Demons are immune
                                if(ent instanceof Player && Races.getRace((Player) ent) == RaceType.DEMON)
                                {
                                    continue;
                                }

                                //The closer to the center, the stronger the force.
                                double amplifier = 0.25 + 1/ent.getLocation().distance(l);

                                ent.getLocation().setDirection(l.getDirection());
                                ent.setVelocity(ent.getLocation().getDirection().multiply(amplifier));
                            }
                        }

                        dummy.remove();
                    }
                }, i);
            }
        }
        if(explode)
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                @Override
                public void run()
                {
                    l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 4, false, false);
                }
            }, 20 * duration);
        }
    }

    @Override
    public String getName() {
        return "Hell Rift";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.HELLRIFT;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.EYE_OF_ENDER;
    }

    @Override
    public int getPrice(int level) {
        return 2;
    }

    @Override
    public List<RaceType> getRaces() {
        return Lists.newArrayList(RaceType.DEMON);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public String[] getDescription(int level) {
        String[] desc;

        switch (level)
        {
            case 1: desc = new String[]{"Open a rift that sucks in loose entities.", "The rift stays open for 6 seconds.","Demons are immune."};
                break;
            case 2: desc = new String[]{"Your rift will now stay twice as long."};
                break;
            case 3: desc = new String[]{"Your rift will explode when closing."};
                break;
            default: desc = new String[]{"This is an error!"};
                break;

        }
        return desc;
    }
}