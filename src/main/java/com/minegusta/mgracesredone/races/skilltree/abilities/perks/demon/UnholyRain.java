package com.minegusta.mgracesredone.races.skilltree.abilities.perks.demon;

import com.google.common.collect.Lists;
import com.minegusta.mgracesredone.main.Main;
import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.playerdata.MGPlayer;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import com.minegusta.mgracesredone.util.*;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class UnholyRain implements IAbility {

    @Override
    public void run(Event event) {
    }

    private void startRain(Location l, final boolean heal, int duration) {
        final World w = l.getWorld();
        final Location location = l;

        for (int i = 0; i <= duration * 20; i++) {
            if (i % 4 == 0) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        w.spigot().playEffect(location, Effect.LAVADRIP, 1, 1, 8, 0, 8, 1, 25, 20);
                    }
                }, i);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        runRain(location, w, heal);
                    }
                }, i);
            }
        }
    }

    private void runRain(Location location, World w, boolean heal) {
        if (location == null) return;
        Entity dummy = w.spawnEntity(location, EntityType.SNOWBALL);
        for (Entity ent : dummy.getNearbyEntities(8, 15, 8)) {
            if (!(ent instanceof LivingEntity)) continue;

            LivingEntity le = (LivingEntity) ent;

            if (ent instanceof Player) {
                Player p = (Player) ent;
                if (PlayerUtil.isUnholy(p)) {
                    if (heal) heal(p);
                } else {
                    damage(p);
                }
            } else {
                if (MonsterUtil.isUnholy(le.getType())) {
                    if (heal) heal(le);
                } else {
                    damage(le);
                }
            }
        }
        dummy.remove();
    }

    private void heal(LivingEntity ent) {
        if (ent.isDead()) return;

        boolean regen = true;
        for (PotionEffect e : ent.getActivePotionEffects()) {
            if (e.getType().equals(PotionEffectType.REGENERATION)) {
                regen = false;
                break;
            }
        }
        if (regen) PotionUtil.updatePotion(ent, PotionEffectType.REGENERATION, 0, 5);
        PotionUtil.updatePotion(ent, PotionEffectType.SPEED, 0, 5);
        PotionUtil.updatePotion(ent, PotionEffectType.DAMAGE_RESISTANCE, 0, 5);
    }

    private void damage(LivingEntity ent) {

        PotionUtil.updatePotion(ent, PotionEffectType.HUNGER, 0, 5);
        PotionUtil.updatePotion(ent, PotionEffectType.SLOW, 0, 5);
        ent.setFireTicks(20 * 5);
    }

    @Override
    public boolean run(Player player) {

        if (!WGUtil.canBuild(player)) {
            player.sendMessage(ChatColor.RED + "You cannot use this here!");
            return false;
        }

        Missile.createMissile(player.getLocation(), player.getLocation().getDirection().multiply(1.1), new Effect[]{Effect.MOBSPAWNER_FLAMES, Effect.FLAME}, 30);
        EffectUtil.playParticle(player, Effect.MAGIC_CRIT);
        EffectUtil.playSound(player, Sound.ENTITY_LIGHTNING_THUNDER);

        MGPlayer mgp = Races.getMGPlayer(player);
        int level = mgp.getAbilityLevel(getType());

        boolean heal = level > 1;
        int duration = 9;
        if (level > 2) duration = 18;

        startRain(player.getLocation().add(0, 9, 0), heal, duration);

        return true;
    }

    @Override
    public String getName() {
        return "Unholy Rain";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.UNHOLYRAIN;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.POISONOUS_POTATO;
    }

    @Override
    public int getPrice(int level) {
        return 1;
    }

    @Override
    public AbilityGroup getGroup() {
        return AbilityGroup.ACTIVE;
    }

    @Override
    public int getCooldown(int level) {
        return 90;
    }

    @Override
    public List<RaceType> getRaces() {
        return Lists.newArrayList(RaceType.DEMON);
    }

    @Override
    public boolean canBind() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public String[] getDescription(int level) {
        String[] desc;

        switch (level) {
            case 1:
                desc = new String[]{"Summon an unholy rain on your location for 9 seconds.", "Bind to an item using /Bind.", "This rain damages holy creatures."};
                break;
            case 2:
                desc = new String[]{"Your holy rain will heal unholy creatures."};
                break;
            case 3:
                desc = new String[]{"The duration of the rain is now 18 seconds."};
                break;
            default:
                desc = new String[]{"This is an error!"};
                break;

        }
        return desc;
    }
}
