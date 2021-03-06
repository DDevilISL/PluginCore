/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.core.ui.objects.interfaces;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import me.ddevil.core.ui.menus.InventoryMenu;

/**
 *
 * @author Selma
 */
public interface InventoryObject {

    public InventoryMenu getMenu();

    public boolean hasMultiSlots();

    public List<Integer> getMultiSlots();

    public boolean hasItemStack();

    public ItemStack getIcon();

    public void update();
}
