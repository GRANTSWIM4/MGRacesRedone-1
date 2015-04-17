package com.minegusta.mgracesredone.listeners.racelisteners;

import com.google.common.collect.Lists;
import com.minegusta.mgracesredone.main.Main;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.util.*;
import com.minegusta.mgracesredone.main.Races;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

public class DemonListener implements Listener
{
    @EventHandler
    public void onDemonDamage(EntityDamageEvent e)
    {
        if(!WorldCheck.isEnabled(e.getEntity().getWorld()))return;

        if(e.getEntity() instanceof Player)
        {
            Player p = (Player) e.getEntity();
            if(!isDemon(p)) return;


            //Demon falls in hell
            if(e.getCause() == EntityDamageEvent.DamageCause.FALL && WeatherUtil.isHell(p.getLocation()))
            {
                if(Races.getMGPlayer(p).hasAbility(AbilityType.HELLSPAWN))
                {
                    AbilityType.HELLSPAWN.run(e);
                }

            }

            //Demon takes fire/lava/firetick damage
            else if(e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.LAVA)
            {
                if(Races.getMGPlayer(p).hasAbility(AbilityType.FIREPROOF))
                {
                    AbilityType.FIREPROOF.run(e);
                }
            }

        }
    }

    @EventHandler
    public void onDemonLazor(PlayerInteractEvent e)
    {
        if(!WorldCheck.isEnabled(e.getPlayer().getWorld()))return;

        if(!isDemon(e.getPlayer()) || e.getAction() != Action.RIGHT_CLICK_AIR)return;
        Player p = e.getPlayer();

        if(!p.isSneaking())return;

        Material hand = p.getItemInHand().getType();

        if(hand == Material.BLAZE_ROD)
        {
            String uuid = p.getUniqueId().toString();
            String name = "uhrain";
            if (Cooldown.isCooledDown(name, uuid)) {
                Missile.createMissile(p.getLocation(), p.getLocation().getDirection().multiply(1.1), new Effect[]{Effect.MOBSPAWNER_FLAMES, Effect.FLAME}, 30);
                Cooldown.newCoolDown(name, uuid, 180);
                EffectUtil.playParticle(p, Effect.MAGIC_CRIT);
                EffectUtil.playSound(p, Sound.AMBIENCE_THUNDER);
                p.sendMessage(ChatColor.DARK_RED + "You call an unholy rain on your location!");

                startRain(p.getLocation().add(0, 9, 0));
            } else {
                ChatUtil.sendString(p, "You have to wait another " + Cooldown.getRemaining(name, uuid) + " seconds to use Unholy Rain.");
            }
        }
    }

    @EventHandler
    public void onDemonMobtarget(EntityTargetLivingEntityEvent e)
    {
        if(!WorldCheck.isEnabled(e.getEntity().getWorld()))return;
        if(e.getTarget() instanceof Player)
        {
            Player p = (Player) e.getTarget();
            if(!isDemon(p))return;

            if(Races.getMGPlayer(p).hasAbility(AbilityType.HELLISHTRUCE))
            {
                AbilityType.HELLISHTRUCE.run(e);
            }
        }
    }

    @EventHandler
    public void onDemonSneak(PlayerToggleSneakEvent e)
    {
        if(!WorldCheck.isEnabled(e.getPlayer().getWorld()))return;

        if(!isDemon(e.getPlayer()))return;

        Player p = e.getPlayer();

        if(PlayerUtil.isInLava(p) && Races.getMGPlayer(p).hasAbility(AbilityType.LAVALOVER))
        {
            AbilityType.LAVALOVER.run(p);
        }
    }

    @EventHandler
    public void onDemonNearDeath(EntityDamageByEntityEvent e)
    {
        if(!WorldCheck.isEnabled(e.getEntity().getWorld()))return;

        //When demon hits something
        if(e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player)
        {
            Player p = (Player) e.getDamager();
            if(!isDemon(p))return;

            if(!e.isCancelled() && Races.getMGPlayer(p).hasAbility(AbilityType.HELLISHTRUCE))
            {
                AbilityType.HELLISHTRUCE.run(e);
            }
        }

        //Spawn hell minions.
        if(e.getEntity() instanceof Player)
        {
            Player p = (Player) e.getEntity();
            if(!isDemon(p))return;

            if(!(e.getDamager() instanceof LivingEntity) || !(p.getHealth() < 6))return;

            String name = "hellminion";
            String uuid = p.getUniqueId().toString();

            if(Cooldown.isCooledDown(name, uuid))
            {
                p.sendMessage(ChatColor.RED + "The minions of hell are here to help you!");
                final Location l = p.getLocation();
                Cooldown.newCoolDown(name, uuid, 300);
                for(int n = 0; n < 7; n++)
                {
                    Entity ent = l.getWorld().spawnEntity(l, EntityType.PIG_ZOMBIE);
                    PigZombie m = (PigZombie) ent;
                    ((Creature)m).setTarget((LivingEntity)e.getDamager());
                }

                for(int le = -5; le < 5; le++)
                {
                    for(int le2 = -5; le2 < 5; le2++)
                    {
                        if(Math.abs(le2) + Math.abs(le) > 3 && Math.abs(le2) + Math.abs(le) < 5)
                        {
                            final int loc1 = le2;
                            final int loc2 = le;
                            for(int i = 0; i < 20 * 6; i++)
                            {
                                final int k = i;
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable()
                                {
                                    @Override
                                    public void run() {

                                        l.getWorld().spigot().playEffect(l.getBlock().getRelative(loc1, 0, loc2).getLocation(), Effect.LAVADRIP, 1, 1, 0, k/30, 0, 1, 25, 30);

                                    }
                                },i);
                            }
                        }
                    }
                }
            }
        }
    }

    private void startRain(Location l)
    {
        UnholyRain rain = new UnholyRain(18, l);
        rain.start();
    }

    private static boolean isDemon(Player p)
    {
        return Races.getRace(p) == RaceType.DEMON;
    }
}
