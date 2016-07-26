package com.minegusta.mgracesredone.races.skilltree.abilities.perks.enderborn;

import com.google.common.collect.Lists;
import com.minegusta.mgracesredone.main.Races;
import com.minegusta.mgracesredone.playerdata.MGPlayer;
import com.minegusta.mgracesredone.races.RaceType;
import com.minegusta.mgracesredone.races.skilltree.abilities.AbilityType;
import com.minegusta.mgracesredone.races.skilltree.abilities.IAbility;
import com.minegusta.mgracesredone.tasks.BleedTask;
import com.minegusta.mgracesredone.util.EffectUtil;
import com.minegusta.mgracesredone.util.PotionUtil;
import com.minegusta.mgracesredone.util.RandomUtil;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Predator implements IAbility {
    //Bleeding for the predator ability
    @Override
    public void run(Event event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Player p = (Player) e.getDamager();
        LivingEntity target = (LivingEntity) e.getEntity();
        MGPlayer mgp = Races.getMGPlayer(p);
        int level = mgp.getAbilityLevel(getType());

        if (e.getCause() == EntityDamageEvent.DamageCause.CUSTOM) return;

        int chance = 10;
        if (level > 3) chance = 20;

        if (RandomUtil.chance(chance)) {
            BleedTask.addBleed(target, e.getDamager(), 4);
        }

    }

    //The food boosts
    @Override
    public boolean run(Player player) {
        MGPlayer mgp = Races.getMGPlayer(player);
        int level = mgp.getAbilityLevel(getType());

        PotionUtil.updatePotion(player, PotionEffectType.REGENERATION, 0, 4);
        if (level > 2) {
            PotionUtil.updatePotion(player, PotionEffectType.REGENERATION, 0, 8);
            PotionUtil.updatePotion(player, PotionEffectType.NIGHT_VISION, 0, 15);
            PotionUtil.updatePotion(player, PotionEffectType.INCREASE_DAMAGE, 0, 15);
            PotionUtil.updatePotion(player, PotionEffectType.SPEED, 1, 15);
            EffectUtil.playParticle(player, Effect.PORTAL, 1, 1, 1, 30);
        }

        int maxHealed = 20 - player.getFoodLevel();
        player.setFoodLevel(player.getFoodLevel() + maxHealed < 2 ? maxHealed : 2);

        return true;
    }

    @Override
    public String getName() {
        return "Predator";
    }

    @Override
    public AbilityType getType() {
        return AbilityType.PREDATOR;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public Material getDisplayItem() {
        return Material.RAW_BEEF;
    }

    @Override
    public int getPrice(int level) {
        return 2;
    }

    @Override
    public AbilityGroup getGroup() {
        return AbilityGroup.PASSIVE;
    }

    @Override
    public int getCooldown(int level) {
        return 0;
    }

    @Override
    public List<RaceType> getRaces() {
        return Lists.newArrayList(RaceType.ENDERBORN);
    }

    @Override
    public boolean canBind() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public String[] getDescription(int level) {
        String[] desc;

        switch (level) {
            case 1:
                desc = new String[]{"When eating raw food you gain a regeneration effect.", "Also heals more food bar."};
                break;
            case 2:
                desc = new String[]{"When hitting enemies they have a 10% chance to start bleeding.", "Bleeding lasts 4 seconds."};
                break;
            case 3:
                desc = new String[]{"Your raw food generation lasts twice as long.", "You also get a nightvision, strength and speed boost."};
                break;
            case 4:
                desc = new String[]{"Your chance to make enemies bleed is now 20%."};
                break;
            case 5:
                desc = new String[]{"Backstabbing does 1.2x damage."};
                break;
            default:
                desc = new String[]{"This is an error!"};
                break;
        }
        return desc;
    }
}
