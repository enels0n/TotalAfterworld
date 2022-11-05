package ru.enelson.total.afterworld.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import ru.enelson.total.afterworld.TotalAfterworld;

public class MainCommand implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length > 0 && args[0].equalsIgnoreCase("reload") && (sender.hasPermission("totalunderworld.reload") || sender.isOp())) {
			TotalAfterworld.config = YamlConfiguration.loadConfiguration(TotalAfterworld.FileConfig);

			TotalAfterworld.afterworld = TotalAfterworld.config.getString("afterworld");
			TotalAfterworld.worlds = TotalAfterworld.config.getStringList("enabled-worlds");
			TotalAfterworld.minX = TotalAfterworld.config.getInt("portal.minX");
			TotalAfterworld.maxX = TotalAfterworld.config.getInt("portal.maxX");
			TotalAfterworld.minZ = TotalAfterworld.config.getInt("portal.minZ");
			TotalAfterworld.maxZ = TotalAfterworld.config.getInt("portal.maxZ");
			
			TotalAfterworld.spawns.clear();
			for (String key : TotalAfterworld.config.getConfigurationSection("spawns").getKeys(false))
				TotalAfterworld.spawns.put(Integer.parseInt(key), TotalAfterworld.config.getInt("spawns." + key));
			
			TotalAfterworld.reincarnationPoint = TotalAfterworld.config.getString("default-reincarnation-point");
			
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',TotalAfterworld.config.getString("messages.reload")));
		}
		else {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4Mages&6Reincarnation &fv" + TotalAfterworld.pluginVersion + " by E.NeLsOn"));
		}
		return false;
	}
}
