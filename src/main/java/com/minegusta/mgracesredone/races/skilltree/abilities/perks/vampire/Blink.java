package com.minegusta.mgracesredone.races.skilltree.abilities.perks.vampire;

import com.google.common.collect.Lists;
import com.minegusta.mgracesredone.listeners.general.FallDamageManager;
import com.minegusta.mgracesredone.main.Main;
import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.playerdata.MGPlayer;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import com.minegusta.mgracesredone.util.EffectUtil;
import com.minegusta.mgracesredone.util.InvisibilityUtil;
import com.minegusta.mgracesredone.util.VampireFoodUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.List;

public class Blink implements IAbility {
    @Override
    public void run(Event event) {

    }

    @Override
    public boolean run(Player player) {
        MGPlayer mgp = Races.getMGPlayer(player);
        int level = mgp.getAbilityLevel(getType());
        String uuid = player.getUniqueId().toString();
        double duration = 20; //duration in ticks. 20 ticks is one second. Will multiply by 2 later in the loop.
        if (level > 1) {
            duration = 30; //duration is now 1.5 seconds.
        }
        if (level > 2) {
            duration = 40; //Duration is 2 seconds now.
        }

        if (player.getFoodLevel() < 2) {
            player.sendMessage(ChatColor.RED + "You need more blood to use this.");
            return false;
        }

        //Drain 2 food.
        VampireFoodUtil.setCanChangeFood(player);
        player.setFoodLevel(player.getFoodLevel() - 2);

        //Turn invisible
        InvisibilityUtil.add(uuid);


        for (int i = 0; i <= duration; i++) {
            if (player.isOnline()) FallDamageManager.addToFallMap(player);
            if (i % 2 == 0) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    //Flying
                    Vector victor = ((player.getPassenger() != null) && (player.getLocation().getDirection().getY() > 0.0D) ? player.getLocation().getDirection().clone().setY(0) : player.getLocation().getDirection()).normalize().multiply(1.30D);
                    player.setVelocity(victor);

                    //Particle

                    for (int i2 = 0; i2 < 4; i2++) {
                        Entity bat = player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
                        EffectUtil.playParticle(player, Effect.SMOKE);
                        //Bat
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () ->
                        {
                            if (bat.isValid()) bat.remove();
                        }, 14);
                    }

                }, i);
            }

            if (i == duration) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () ->
                {
                    InvisibilityUtil.remove(uuid);
                    if (player.isOnline()) FallDamageManager.addToFallMap(player);
                }, i * 2);
            }
        }

        return true;
    }

    @Override
    public String getName() {
        return "Blink";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.BLINK;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.FEATHER;
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
        return 50;
    }

    @Override
    public List<RaceType> getRaces() {
        return Lists.newArrayList(RaceType.VAMPIRE);
    }

    @Override
    public boolean canBind() {
        return true;
    }

    @Override
    public String getBindDescription() {
        return "Become a swarm of bats and fly short distances.";
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
                desc = new String[]{"Become invincible for 1 seconds while being able to fly quickly.", "Bind to an item using /Bind.", "Will use a little of your blood bar."};
                break;
            case 2:
                desc = new String[]{"Blinking lasts 1.5 seconds."};
                break;
            case 3:
                desc = new String[]{"Blinking lasts 2 seconds."};
                break;
            default:
                desc = new String[]{"This is an error!"};
                break;

        }
        return desc;
    }
}
