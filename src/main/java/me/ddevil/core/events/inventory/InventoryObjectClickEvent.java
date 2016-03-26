/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.core.events.inventory;

import me.ddevil.core.events.CustomEvent;
import me.ddevil.core.utils.inventory.InventoryMenu;
import me.ddevil.core.utils.inventory.objects.InventoryObject;
import org.bukkit.entity.Player;

/**
 *
 * @author Selma
 */
public class InventoryObjectClickEvent extends CustomEvent {

    private final InventoryObject object;
    private final InventoryMenu menu;
    private final Player player;

    public InventoryObjectClickEvent(InventoryObject object, Player clicker) {
        this.object = object;
        this.menu = object.getMenu();
        this.player = clicker;
    }

    public InventoryMenu getMenu() {
        return menu;
    }

    public InventoryObject getObject() {
        return object;
    }

    public Player getPlayer() {
        return player;
    }

}