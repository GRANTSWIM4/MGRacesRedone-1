package com.minegusta.mgracesredone.races.skilltree.abilities.perks.dwarf;

import com.google.common.collect.Lists;
import com.minegusta.mgracesredone.main.Main;
import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.playerdata.MGPlayer;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import com.minegusta.mgracesredone.util.ChatUtil;
import com.minegusta.mgracesredone.util.RandomUtil;
import com.minegusta.mgracesredone.util.WGUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.List;

public class Earthquake implements IAbility {

    @Override
    public void run(Event event) {

    }

    @Override
    public boolean run(Player player) {
        //Standard data needed.
        MGPlayer mgp = Races.getMGPlayer(player);
        int level = mgp.getAbilityLevel(getType());
        int radius = 8;
        int strength = 1;
        if (level > 3) strength = 2;
        int duration = 10;
        if (level > 2) duration = 15;
        Location l = player.getLocation();

        //Worldguard?
        if (!WGUtil.canBuild(player)) {
            ChatUtil.sendString(player, "You cannot use " + getName() + " here.");
            return false;
        }

        //Run the ability here\
        task(l, duration, radius, strength);
        return true;
    }

    private void task(final Location l, int duration, final int radius, final double strength) {
        for (int i = 0; i <= 20 * duration; i++) {
            if (i % 4 == 0) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
                    shake(l, radius, strength);
                    if (RandomUtil.chance(30)) {
                        double x = RandomUtil.randomNumber(2 * radius) - radius;
                        double z = RandomUtil.randomNumber(2 * radius) - radius;
                        column(new Location(l.getWorld(), l.getX() + x, l.getY(), l.getZ() + z));
                    }
                }, i);
            }
        }
    }

    private void shake(final Location l, int radius, double strength) {
        l.getWorld().getEntitiesByClasses(LivingEntity.class, Item.class).stream().
                filter(ent -> ent.getLocation().distance(l) <= radius).
                filter(ent -> !(ent instanceof Player &&
                        Races.getMGPlayer((Player) ent).getRaceType().equals(RaceType.DWARF))).forEach(ent -> {
            double range = strength * 0.6;
            double min = range / 2;

            double x = RandomUtil.randomDouble(range, 0) - min;
            double z = RandomUtil.randomDouble(range, 0) - min;

            ent.setVelocity(ent.getVelocity().add(new Vector(x, ent.getVelocity().getY(), z)));
        });
    }

    private void column(final Location l) {
        final int duration = 6 * 20;
        final int delay = 6;

        final Block b = findAir(l);

        //Spawn them
        for (int i = 0; i < 5; i++) {
            final int up = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
                Block changed = b.getRelative(BlockFace.UP, up);
                if (changed.getType() == Material.AIR) {
                    changed.setType(Material.STONE);
                    removeBlock(changed, duration - up * 20);
                }
            }, i * delay);
        }
    }

    private void removeBlock(final Block b, int delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
            if (b.getType() == Material.STONE) {
                b.setType(Material.AIR);
            }
        }, delay);
    }

    private Block findAir(Location l) {
        if (l.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
            return l.getBlock();
        }
        for (int i = 2; i < 7; i++) {
            if (l.getBlock().getRelative(BlockFace.DOWN, i).getType() != Material.AIR) {
                return l.getBlock().getRelative(BlockFace.DOWN, i - 1);
            }
        }
        return l.getBlock().getRelative(BlockFace.DOWN, 3);
    }

    @Override
    public String getName() {
        return "Earthquake";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.EARTQUAKE;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.NETHER_STAR;
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
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public String[] getDescription(int level) {
        String[] desc;

        switch (level) {
            case 1:
                desc = new String[]{"Cause an earthquake, unbalancing entities around you.", "Bind to an item using /Bind.", "Lasts 10 seconds."};
                break;
            case 2:
                desc = new String[]{"The floor around enemies will distort."};
                break;
            case 3:
                desc = new String[]{"The duration is increased to 15 seconds."};
                break;
            case 4:
                desc = new String[]{"The disorienting effect is twice as strong."};
                break;
            default:
                desc = new String[]{"This is an error!"};
                break;

        }
        return desc;
    }
}
