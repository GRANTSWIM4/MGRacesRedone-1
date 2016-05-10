package com.minegusta.mgracesredone.races.skilltree.abilities;

import com.minegusta.mgracesredone.races.RaceType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;

public interface IAbility {
    /**
     * Run the ability at an event.
     *
     * @param event The event to run the ability for.
     */
    void run(Event event);

    /**
     * Run the ability for a specific player without an event involved.
     *
     * @param player The player to run the event for.
     */
    boolean run(Player player);

    /**
     * Get the string for this perk. This is used in the perk shop.
     *
     * @return
     */
    String getName();

    /**
     * Get the AbilityType representing this perk.
     *
     * @return
     */
    AbilityType getType();

    /**
     * Get the ID for this ability. This is currently never used but may prove useful in the future.
     *
     * @return
     */
    int getID();

    /**
     * Get the item used to display this perk in the perk shop.
     *
     * @return
     */
    Material getDisplayItem();

    /**
     * Get the price for this perk at a specific level. Make use of switch/case or just return 1.
     *
     * @param level
     * @return
     */
    int getPrice(int level);


    /**
     * Get the group this ability belongs to.
     *
     * @return Active or passive.
     */
    AbilityGroup getGroup();

    /**
     * If this ability has a cooldown, get the cooldown. 0 if not specified.
     *
     * @param level The cooldown for that specific level.
     * @return The cooldown time in seconds for this ability.
     */
    int getCooldown(int level);

    /**
     * Get all races this perk applies to. Some races will have shared perks.
     *
     * @return
     */
    List<RaceType> getRaces();

    /**
     * Get if this ability can be bound to an item using the /bind command.
     *
     * @return
     */
    boolean canBind();

    /**
     * Return the description shown in the /Bind List All command.
     *
     * @return
     */
    default String getBindDescription() {
        return "";
    }

    /**
     * A method to retrieve the max level of this perk.
     *
     * @return The maxium level possible. May NEVER be over 5.
     */
    int getMaxLevel();

    /**
     * A method for getting the description per level. Use a switch/case in here to achieve this.
     *
     * @param level The level aimed at. Make sure the level is lower than the specified level in this class.
     * @return The strings that for the description for this level of the item.
     */
    String[] getDescription(int level);

    enum AbilityGroup {
        ACTIVE, PASSIVE
    }
}
