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
package me.ddevil.core.commands;

import java.util.ArrayList;
import java.util.List;

import me.ddevil.core.CustomPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CustomCommand extends Command {

    private final CustomPlugin plugin;
    protected List<String> usageMessages;

    public CustomCommand(CustomPlugin plugin, String name, String permission) {
        super(name);
        this.plugin = plugin;
        plugin.registerPermission(permission);
        this.permission = permission;
    }

    public CustomCommand(String name, String permission, List<String> aliases, CustomPlugin plugin) {
        super(name);
        this.plugin = plugin;
        plugin.registerPermission(permission);
        setAliases(aliases);
        this.permission = permission;
    }

    public CustomCommand(String name, String permission, List<String> aliases, String description, CustomPlugin plugin) {
        super(name);
        this.plugin = plugin;
        this.plugin.registerPermission(permission);
        setAliases(aliases);
        setDescription(description);
        this.permission = permission;
    }

    public CustomCommand(String name, String permission, List<String> aliases, String description, String usage, CustomPlugin plugin) {
        super(name);
        this.plugin = plugin;
        this.plugin.registerPermission(permission);
        setAliases(aliases);
        setDescription(description);
        setUsage(usage);
        this.permission = permission;
    }

    public String permission;

    @Override
    public boolean execute(CommandSender cs, String string, String[] strings) {
        return handleExecute(cs, strings);
    }

    private final ArrayList<SubCommand> subCommands = new ArrayList();

    public void addSubCommand(SubCommand cmd) {
        subCommands.add(cmd);
    }

    public SubCommand getSubCommand(String name) {
        for (SubCommand command : subCommands) {
            if (command.getName().equals(name) || command.getAliases().contains(name)) {
                return command;
            }
        }
        return null;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public boolean checkPerm(Player p) {
        return p.hasPermission(permission);
    }


    public void sendInvalidArguments(Player p, String msg) {
        plugin.messageManager.sendMessage(p, "§cInvalid arguments! §7" + msg);
    }

    public abstract boolean handleExecute(CommandSender sender, String[] args);
}
