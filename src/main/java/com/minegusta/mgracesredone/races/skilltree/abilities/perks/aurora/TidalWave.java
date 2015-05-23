package com.minegusta.mgracesredone.races.skilltree.abilities.perks.aurora;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minegusta.mgracesredone.main.Main;
import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.playerdata.MGPlayer;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import com.minegusta.mgracesredone.util.ChatUtil;
import com.minegusta.mgracesredone.util.Cooldown;
import com.minegusta.mgracesredone.util.PotionUtil;
import com.minegusta.mgracesredone.util.WGUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class TidalWave implements IAbility
{

    @Override
    public void run(Event event) {

    }

    @Override
    public void run(Player player)
    {
        if(!WGUtil.canBuild(player))
        {
            ChatUtil.sendString(player, "You cannot use TidalWave here!");
        }

        MGPlayer mgp = Races.getMGPlayer(player);
        int level = mgp.getAbilityLevel(getType());
        Location l = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String cooldownName = "wave";

        if(Cooldown.isCooledDown(cooldownName, uuid))
        {
            ChatUtil.sendString(player, "You use tidal wave on your location!");
            Cooldown.newCoolDown(cooldownName, uuid, getCooldown(level));

            int radius = 7;
            boolean damage = level > 1;
            if(level > 2)radius = 14;

            start(radius, damage, player);
        }
        else
        {
            ChatUtil.sendString(player, "You need to wait another " + Cooldown.getRemaining(cooldownName, uuid) + " seconds to use TidalWave.");
        }
    }

    private void start(int radius, final boolean damage, Player p)
    {
        //Add all blocks for the wave in a row here.
        final Block center = p.getTargetBlock(Sets.newHashSet(Material.AIR), 4);

        for(int i = 0; i <= radius; i++)
        {
            final int k = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                @Override
                public void run()
                {
                    final List<Block> blocks = Lists.newArrayList();
                    //Do the wave stuff
                    for(int x = 0; x < 6; x ++)
                    {
                        for(int y = 0; y < 6; y ++)
                        {
                            for(int z = 0; z < 6; z ++)
                            {
                                Block target = center.getWorld().getBlockAt((int)center.getX() + x, (int)center.getY() + y, (int) center.getZ() + z);

                                if(target.getLocation().distance(center.getLocation()) <= 5 && target.getType()== Material.AIR)
                                {
                                    blocks.add(target);
                                }
                            }
                        }
                    }

                    //Set to water and then task to make it air again
                    for(Block b : blocks)
                    {
                        if(b.getType() == Material.AIR)
                        {
                            b.setType(Material.STATIONARY_WATER);
                        }
                    }
                    //Apply the effects
                    Entity dummy = center.getWorld().spawnEntity(center.getLocation(), EntityType.SNOWBALL);
                    for(Entity ent : dummy.getNearbyEntities(5, 5, 5))
                    {
                        if(!(ent instanceof LivingEntity))continue;
                        double x = ent.getLocation().getX() - center.getX();
                        double y = ent.getLocation().getY() - center.getY();
                        double z = ent.getLocation().getZ() - center.getZ();
                        Vector v = new Vector(x, y, z);
                        v.normalize();
                        ent.setVelocity(ent.getVelocity().add(v.multiply(-2)));

                        if(damage && WGUtil.canGetDamage(ent))
                        {
                            ((LivingEntity) ent).damage(1);
                        }

                    }
                    dummy.remove();

                    //Return to normal after 2/3 second.
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                        @Override
                        public void run()
                        {
                            for(Block b : blocks)
                            {
                                if(b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER)
                                {
                                    b.setType(Material.AIR);
                                }
                            }
                        }
                    }, 16 * k);

                }
            }, 16 * i);
        }
    }

    @Override
    public String getName() {
        return "TidalWave";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.TIDALWAVE;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.POTION;
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
        return 60;
    }

    @Override
    public List<RaceType> getRaces() {
        return Lists.newArrayList(RaceType.AURORA);
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
            case 1: desc = new String[]{"Cast a wall of water in any of four directions.", "Activate by left-clicking a snowball."};
                break;
            case 2: desc = new String[]{"Your wave will cause drown damage to anyone in it."};
                break;
            case 3: desc = new String[]{"Your wave will reach twice as far."};
                break;
            default: desc = new String[]{"This is an error!"};
                break;

        }
        return desc;
    }
}
