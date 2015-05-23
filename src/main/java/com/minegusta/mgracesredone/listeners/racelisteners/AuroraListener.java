package com.minegusta.mgracesredone.listeners.racelisteners;

import com.google.common.collect.Lists;
import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.util.*;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

public class AuroraListener implements Listener
{
    private static final List<Material> snowBlocks = Lists.newArrayList(Material.SNOW_BLOCK, Material.SNOW, Material.ICE, Material.PACKED_ICE);

    @EventHandler
    public void onDrownOrFall(EntityDamageEvent e)
    {
        if(e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.DROWNING)
        {
            if(!WorldCheck.isEnabled(e.getEntity().getWorld()))return;
            Player p = (Player) e.getEntity();
            if(Races.getMGPlayer(p).getAbilityLevel(AbilityType.FEESH) > 1)
            {
                e.setCancelled(true);
            }
        }

        else if(e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL)
        {
            Player p = (Player) e.getEntity();
            if(Races.getMGPlayer(p).getAbilityLevel(AbilityType.GLACIOUS) > 2)
            {
                Material mat = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
                Material mat2 = p.getLocation().getBlock().getType();

                if(snowBlocks.contains(mat) || snowBlocks.contains(mat2))
                {
                    e.setDamage(0.0);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onAuroraSnowball(ProjectileHitEvent e)
    {
        if(!WorldCheck.isEnabled(e.getEntity().getWorld()))return;

        if(e.getEntity().getShooter() instanceof Player && e.getEntity() instanceof Snowball)
        {
            Snowball ball = (Snowball) e.getEntity();
            Player p = (Player) ball.getShooter();

            if(!Races.getMGPlayer(p).hasAbility(AbilityType.ICEBARRAGE))return;

            AbilityType.ICEBARRAGE.run(e);
        }
    }

    @EventHandler
    public void onAuroraInteract(PlayerInteractEvent e)
    {
        if(!WorldCheck.isEnabled(e.getPlayer().getWorld()))return;

        if(e.getAction() == Action.LEFT_CLICK_BLOCK && e.getClickedBlock().getY() < e.getPlayer().getLocation().getY() && ItemUtil.isSword(e.getPlayer().getItemInHand().getType()) && Races.getMGPlayer(e.getPlayer()).hasAbility(AbilityType.FROST))
        {
            AbilityType.FROST.run(e.getPlayer());
        }
        if(e.getAction() == Action.RIGHT_CLICK_AIR && PlayerUtil.isInWater(e.getPlayer()) && ItemUtil.isSword(e.getPlayer().getItemInHand().getType()) && Races.getMGPlayer(e.getPlayer()).hasAbility(AbilityType.DROWNINGPOOL))
        {
            AbilityType.DROWNINGPOOL.run(e.getPlayer());
        }

        if(e.getAction() == Action.LEFT_CLICK_AIR && e.getPlayer().getItemInHand().getType() == Material.SNOW_BALL && Races.getMGPlayer(e.getPlayer()).hasAbility(AbilityType.TIDALWAVE))
        {
            AbilityType.TIDALWAVE.run(e.getPlayer());
        }
    }

    @EventHandler
    public void onAuroraSneak(PlayerToggleSneakEvent e)
    {
        if(!WorldCheck.isEnabled(e.getPlayer().getWorld()))return;

        if(PlayerUtil.isInWater(e.getPlayer()) && Races.getMGPlayer(e.getPlayer()).hasAbility(AbilityType.AQUAMAN))
        {
            AbilityType.AQUAMAN.run(e.getPlayer());
        }
    }

    private static boolean isAurora(Player p)
    {
        return Races.getRace(p) == RaceType.AURORA;
    }
}
