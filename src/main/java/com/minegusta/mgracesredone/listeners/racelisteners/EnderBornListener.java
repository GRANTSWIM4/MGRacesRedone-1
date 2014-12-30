package com.minegusta.mgracesredone.listeners.racelisteners;

import com.google.common.collect.Maps;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.util.*;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ConcurrentMap;

public class EnderBornListener implements Listener
{

    private static ConcurrentMap<String, Boolean> pearlMap = Maps.newConcurrentMap();


    @EventHandler
    public void onEnderBornMeatEat(PlayerItemConsumeEvent e)
    {
        Player p = e.getPlayer();
        if(!WorldCheck.isEnabled(p.getWorld()))return;

        if(ItemUtil.isRawMeat(e.getItem().getType()))
        {
            PotionUtil.updatePotion(p, PotionEffectType.NIGHT_VISION, 0, 15);
            PotionUtil.updatePotion(p, PotionEffectType.INCREASE_DAMAGE, 0, 15);
            PotionUtil.updatePotion(p, PotionEffectType.SPEED, 1, 15);
            EffectUtil.playParticle(p, Effect.PORTAL, 1, 1, 1, 30);
        }
    }

    @EventHandler
    public void onBleed(EntityDamageByEntityEvent e)
    {
        if(!WorldCheck.isEnabled(e.getEntity().getWorld()))return;

        if(e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity)
        {
            Player p = (Player) e.getDamager();
            if(isEnderBorn(p) && WGUtil.canFightEachother(p, e.getEntity()))
            {
                if(RandomUtil.chance(15))
                {
                    EntityUtil.bleed((LivingEntity) e.getEntity(), 4);
                }
            }
        }
    }

    @EventHandler
    public void onPearlToggle(PlayerInteractEvent e)
    {
        if(!WorldCheck.isEnabled(e.getPlayer().getWorld()))return;
        Player p = e.getPlayer();
        if(isEnderBorn(p) && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK))
        {
            if(p.getItemInHand().getType() == Material.ENDER_PEARL)
            {
                boolean enabled = true;
                String uuid = p.getUniqueId().toString();

                if(pearlMap.containsKey(uuid)) enabled = !pearlMap.get(uuid);
                pearlMap.put(uuid, enabled);

                if(enabled)
                {
                    p.sendMessage(ChatColor.DARK_PURPLE + "Enderpearls now no longer teleport you, but summon minions.");
                }
                else
                {
                    p.sendMessage(ChatColor.DARK_PURPLE + "Enderpearls will now teleport you again.");
                }

            }
        }
    }

    @EventHandler
    public void onPearlThrow(ProjectileHitEvent e)
    {
        if(!WorldCheck.isEnabled(e.getEntity().getWorld()))return;
        if(e.getEntity() instanceof EnderPearl)
        {
            EnderPearl pearl = (EnderPearl) e.getEntity();
            if(pearl.getShooter() instanceof Player && isEnderBorn((Player) pearl.getShooter()))
            {
                String uuid = ((Player) pearl.getShooter()).getUniqueId().toString();
                if(pearlMap.containsKey(uuid) && pearlMap.get(uuid))
                {
                    Enderman man = (Enderman) pearl.getWorld().spawnEntity(pearl.getLocation(), EntityType.ENDERMAN);
                    man.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1, 20 * 60));
                    man.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 2, 20 * 60));
                    man.setCustomName(ChatColor.DARK_PURPLE + "END OF MAN");
                    man.setCustomNameVisible(true);

                    for(Entity ent : pearl.getNearbyEntities(7, 7, 7))
                    {
                        if(ent instanceof Player)
                        {
                            ((Creature)man).setTarget((Player) ent);
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEnderMobTarget(EntityTargetLivingEntityEvent e)
    {
        if(!WorldCheck.isEnabled(e.getEntity().getWorld()))return;
        if(e.getTarget() instanceof Player && e.getEntity() instanceof Enderman)
        {
            Player p = (Player) e.getTarget();
            if(!isEnderBorn(p))return;

            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onBlockTeleport(PlayerTeleportEvent e)
    {
        if(!WorldCheck.isEnabled(e.getPlayer().getWorld()))return;

        Player p = e.getPlayer();
        if(!isEnderBorn(p))return;
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
        {
            if(!pearlMap.containsKey(p.getUniqueId().toString()))return;
            if(!pearlMap.get(p.getUniqueId().toString()))return;
            e.setCancelled(true);
        }
    }

    private static boolean isEnderBorn(Player p)
    {
        return Races.getRace(p) == RaceType.ENDERBORN;
    }


}
