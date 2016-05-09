package com.minegusta.mgracesredone.races.skilltree.abilities.perks.werewolf;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minegusta.mgracesredone.main.Main;
import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.playerdata.MGPlayer;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import com.minegusta.mgracesredone.util.ChatUtil;
import com.minegusta.mgracesredone.util.Cooldown;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class Dig implements IAbility {

    public static ConcurrentMap<String, Long> breaking = Maps.newConcurrentMap();
    public static ConcurrentMap<String, Boolean> players = Maps.newConcurrentMap();
    private Permission permission = Main.getPlugin().getPermissions();

    @Override
    public void run(Event event) {

        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player p = e.getPlayer();
        MGPlayer mgp = Races.getMGPlayer(p);
        String uuid = p.getUniqueId().toString();
        Block start = e.getClickedBlock();
        Action a = e.getAction();

        int level = mgp.getAbilityLevel(getType());
        int duration = 5;
        List<Material> materials = Lists.newArrayList(Material.DIRT, Material.SAND, Material.GRASS);

        if (level > 1) {
            duration = 7;
        }
        if (level > 2) {
            materials.add(Material.STONE);
            materials.add(Material.GRAVEL);
        }
        if (level > 3) {
            duration = 9;
        }

        if (a == Action.LEFT_CLICK_BLOCK && breaking.containsKey(uuid)) {
            if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - breaking.get(uuid)) > duration) {
                breaking.remove(uuid);
                return;
            }

            dig(start, p, materials);
        } else if (a == Action.RIGHT_CLICK_BLOCK) {
            if (Cooldown.isCooledDown(getName(), uuid)) {
                Cooldown.newCoolDown(getName(), uuid, getCooldown(level));
                breaking.put(uuid, System.currentTimeMillis());
                ChatUtil.sendString(p, "You can now dig really fast by hitting blocks with your claws!");
            } else {
                ChatUtil.sendString(p, "You have to wait another " + Cooldown.getRemaining(getName(), uuid) + " seconds to use " + getName() + ".");
            }
        }


    }


    private void dig(final Block start, final OfflinePlayer p, List<Material> materials) {

        if (players.containsKey(p.getUniqueId().toString())) {
            return;
        }

        players.put(p.getUniqueId().toString(), true);
        permission.playerAddTransient(p, "nocheatplus.checks.blockbreak.fastbreak");
        permission.playerAddTransient(p, "nocheatplus.checks.blockbreak.wrongblock");

        for (BlockFace face : BlockFace.values()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
                if (!p.isOnline()) {
                    players.remove(p.getUniqueId().toString());
                    permission.playerRemoveTransient(p, "nocheatplus.checks.blockbreak.fastbreak");
                    permission.playerRemoveTransient(p, "nocheatplus.checks.blockbreak.wrongblock");
                    return;
                }
                BlockBreakEvent event2 = getEvent(start.getRelative(face), p.getPlayer());
                if (!event2.isCancelled() && materials.contains(start.getRelative(face).getType())) {
                    event2.getBlock().setType(Material.AIR);
                }
                if (face == BlockFace.SELF) {
                    players.remove(p.getUniqueId().toString());
                    permission.playerRemoveTransient(p, "nocheatplus.checks.blockbreak.fastbreak");
                    permission.playerRemoveTransient(p, "nocheatplus.checks.blockbreak.wrongblock");

                }
            }, face.ordinal() + 1);
        }
    }

    private BlockBreakEvent getEvent(Block b, Player p) {
        BlockBreakEvent event = new BlockBreakEvent(b, p);
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }

    @Override
    public boolean run(Player player) {
        return false;
    }

    @Override
    public String getName() {
        return "Dig";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.DIG;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.IRON_SPADE;
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
        return 30;
    }

    @Override
    public List<RaceType> getRaces() {
        return Lists.newArrayList(RaceType.WEREWOLF);
    }

    @Override
    public boolean canBind() {
        return false;
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
                desc = new String[]{"You can dig really fast by hitting the ground with your hands.", "Activate by right clicking a block with your hands.", "Affects dirt, sand and grass.", "Lasts for 5 seconds."};
                break;
            case 2:
                desc = new String[]{"Your digging lasts for 7 seconds."};
                break;
            case 3:
                desc = new String[]{"Your digging affects stone and gravel."};
                break;
            case 4:
                desc = new String[]{"Your digging lasts for 9 seconds."};
                break;
            default:
                desc = new String[]{"This is an error!"};
                break;
        }
        return desc;
    }
}