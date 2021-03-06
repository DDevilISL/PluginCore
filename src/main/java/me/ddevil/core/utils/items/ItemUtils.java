/* 
 * Copyright (C) 2016 Selma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.ddevil.core.utils.items;

import me.ddevil.core.CustomPlugin;
import me.ddevil.core.exceptions.ItemConversionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Selma
 */
public class ItemUtils {
    public static CustomPlugin plugin;

    public void setup(CustomPlugin plugin) {
        this.plugin = plugin;
    }

    public static class Deserializer {

        /**
         * Gets an item back from the Map created by {@link Deserializer#serialize(ItemStack)}
         *
         * @param map The map to deserialize from.
         * @return The deserialized item.
         * @throws IllegalAccessException    Things can go wrong.
         * @throws IllegalArgumentException  Things can go wrong.
         * @throws InvocationTargetException Things can go wrong.
         */
        public static ItemStack deserialize(Map<String, Object> map) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            ItemStack i = ItemStack.deserialize(map);
            if (map.containsKey("meta")) {
                try {
                    //  org.bukkit.craftbukkit.v1_8_R3.CraftMetaItem$SerializableMeta
                    //  CraftMetaItem.SerializableMeta.deserialize(Map<String, Object>)
                    if (ITEM_META_DESERIALIZATOR != null) {
                        ItemMeta im = (ItemMeta) DESERIALIZE.invoke(i, map.get("meta"));
                        i.setItemMeta(im);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw e;
                }
            }
            return i;
        }

        /**
         * Serializes an ItemStack and it's ItemMeta, use {@link Deserializer#deserialize(Map)}
         * to get the item back.
         *
         * @param item Item to serialize
         * @return A HashMap with the serialized item
         */
        public static Map<String, Object> serialize(ItemStack item) {
            HashMap<String, Object> itemDocument = new HashMap(item.serialize());
            if (item.hasItemMeta()) {
                itemDocument.put("meta", new HashMap(item.getItemMeta().serialize()));
            }
            return itemDocument;
        }

        //Below here lays some crazy shit that make the above methods work :D yay!
        // <editor-fold desc="Some crazy shit" defaultstate="collapsed">
    /*
         * @return The string used in the CraftBukkit package for the version.
         */
        public static String getVersion() {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            String version = name.substring(name.lastIndexOf('.') + 1) + ".";
            return version;
        }

        /**
         * @param className
         * @return
         */
        public static Class<?> getOBCClass(String className) {
            String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
            Class<?> clazz = null;
            try {
                clazz = Class.forName(fullName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return clazz;
        }

        private static final Class ITEM_META_DESERIALIZATOR = getOBCClass("inventory.CraftMetaItem").getClasses()[0];
        private static final Method DESERIALIZE = getDeserialize();

        private static Method getDeserialize() {

            try {
                return ITEM_META_DESERIALIZATOR.getMethod("deserialize", new Class[]{Map.class});
            } catch (NoSuchMethodException | SecurityException ex) {
                return null;
            }
        }
        // </editor-fold>

    }

    private static boolean glowRegistered = false;

    //geral
    public static final ItemStack NA = ItemUtils.createItem(Material.BARRIER, "§4§l§o§nN/A");
    public static final ItemStack Gray = ItemUtils.createItem(Material.IRON_FENCE, "§r");

    public static String toString(ItemStack i) {
        return i.getType() + ":" + i.getData().getData();
    }

    public static ItemStack addToLore(ItemStack is, String... strings) {
        List<String> lore = getLore(is);
        for (String toAdd : strings) {
            lore.add(toAdd);
        }
        ItemStack i = new ItemStack(is);
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setLore(lore);
        i.setItemMeta(itemMeta);
        return i;
    }

    public static ItemStack createItem(ConfigurationSection itemSection) throws IllegalArgumentException {
        try {
            plugin.debug("Loading item " + itemSection.getName() + " from config.");
            String itemName = null;
            if (itemSection.contains("name")) {
                itemName = plugin.messageManager.translateAll(itemSection.getString("name"));
            }
            List<String> itemLore = null;
            if (itemSection.contains("lore")) {
                itemLore = plugin.messageManager.translateAll(itemSection.getStringList("lore"));
            }
            Material m = Material.valueOf(itemSection.getString("type"));
            byte data = ((Integer) itemSection.getInt("data")).byteValue();
            return createItem(
                    new ItemStack(m, 1, (short) 0, data),
                    itemName,
                    itemLore);
        } catch (Exception e) {
            e.printStackTrace();
            String info = " (" + e.getCause().getMessage() + ")";
            if (itemSection == null) {
                throw new IllegalArgumentException("Given configuration section is null! " + e.getCause().toString() + info);
            } else {
                throw new IllegalArgumentException("Configuration Section " + itemSection.getCurrentPath() + " is baddly formated!" + info);
            }
        }
    }

    public static ItemStack createItem(Material material, String nome) {
        ItemStack is = new ItemStack(material);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(nome);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack createItem(ItemStack material, String nome) {
        ItemStack is = new ItemStack(material);
        ItemMeta im = is.getItemMeta();
        if (nome != null) {
            im.setDisplayName(nome);
        }
        im.addItemFlags(ItemFlag.values());
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack createItem(ItemStack material, String nome, List<String> desc) {
        ItemStack is = ItemUtils.createItem(material, nome);
        ItemMeta im = is.getItemMeta();
        im.setLore(desc);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack createItem(Material material, String nome, Collection<String> desc) {
        ItemStack is = ItemUtils.createItem(material, nome);
        ItemMeta im = is.getItemMeta();
        im.setLore(new ArrayList<String>(desc));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack clearLore(ItemStack i) {
        ItemMeta itemMeta = i.getItemMeta();
        itemMeta.setLore(null);
        i.setItemMeta(itemMeta);
        return i;
    }

    public static ItemStack addToLore(ItemStack i, List<String> strings) {
        List<String> lore = getLore(i);
        for (String toAdd : strings) {
            lore.add(toAdd);
        }
        ItemMeta itemMeta = i.getItemMeta();
        itemMeta.setLore(lore);
        i.setItemMeta(itemMeta);
        return i;
    }

    public static void addGlow(ItemStack i) {
        if (!glowRegistered) {
            registerGlow();
        }
        Glow glow = new Glow(70);
        ItemMeta im = i.getItemMeta();
        im.addEnchant(glow, 1, true);
        i.setItemMeta(im);
    }

    public static boolean checkDisplayName(ItemStack i) {
        if (checkItemMeta(i)) {
            ItemMeta im = i.getItemMeta();
            if (im.getDisplayName() != null) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkItemMeta(ItemStack i) {
        ItemMeta im = i.getItemMeta();
        return im != null;
    }

    public static boolean checkLore(ItemStack i) {
        if (checkItemMeta(i)) {
            ItemMeta im = i.getItemMeta();
            if (im.getLore() != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValid(ItemStack item) {
        return isValid(item, true);
    }

    public static boolean isValid(ItemStack item, boolean checkName) {
        if (item != null) {
            if (item.getItemMeta() != null) {
                if (checkName) {
                    if (item.getItemMeta().getDisplayName() != null) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static List<String> getLore(ItemStack i) {
        if (checkLore(i)) {
            return i.getItemMeta().getLore();
        } else {
            return new ArrayList();
        }
    }

    public static ItemStack convertFromInput(String input, String name) throws ItemConversionException {
        return createItem(convertFromInput(input), name);
    }

    public static ItemStack convertFromInput(String input) throws ItemConversionException {
        try {
            String[] materialanddata = input.split(":");
            Material mat = Material.valueOf(materialanddata[0]);
            Byte b;
            if (materialanddata.length > 1) {
                try {
                    b = Byte.valueOf(materialanddata[1]);
                } catch (NumberFormatException exception) {
                    plugin.debug(materialanddata[1] + " in " + input + "isn't a number! Setting byte to 0");
                    b = 0;
                }
            } else {
                b = 0;
            }
            return new ItemStack(mat, 1, (short) 0, b);
        } catch (Exception e) {
            throw new ItemConversionException(input);
        }
    }

    public static boolean equals(ItemStack a, ItemStack b) {
        if (!checkDisplayName(a) || !checkDisplayName(b)) {
            return false;
        } else {
            return a.getItemMeta().getDisplayName().equalsIgnoreCase(b.getItemMeta().getDisplayName());
        }
    }

    public static boolean equalMaterial(ItemStack a, ItemStack b) {
        if (a == null || b == null) {
            return false;
        } else {
            if (a.getType() == b.getType()) {

                if (a.getData().
                        getData()
                        == b.getData()
                        .getData() || a.
                        getData()
                        .getData()
                        == -1 || b.
                        getData()
                        .getData()
                        == -1) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {

        }
        try {
            Glow glow = new Glow(70);
            Enchantment.registerEnchantment(glow);
            glowRegistered = true;
        } catch (IllegalArgumentException e) {
        }
    }
}

class Glow extends Enchantment {

    public Glow(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return "Glow";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public boolean conflictsWith(Enchantment e) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack is) {
        return true;
    }
}
