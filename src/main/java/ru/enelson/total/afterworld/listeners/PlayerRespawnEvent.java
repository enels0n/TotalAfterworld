package ru.enelson.total.afterworld.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.data.PlayerData;

public class PlayerRespawnEvent implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void toAfterworld(org.bukkit.event.player.PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		PlayerData pd = TotalAfterworld.playerManager.getPlayerData(player);
		
		if(!pd.isDead())
			return;

		player.sendMessage(ChatColor.translateAlternateColorCodes('&',TotalAfterworld.config.getString("messages.death")));
		
		int i = 0;
		Location loc;
		while(i<1) {
			loc = pd.getAfterworldLocation();
			Block block = loc.getBlock();
			if (block.getType() != Material.LAVA && block.getType() != Material.MAGMA_BLOCK && block.getType() != Material.FIRE) {
				e.setRespawnLocation(loc);
				break;
			}
		}
		
		TotalAfterworld.plugin.getServer().getScheduler().runTaskLater(TotalAfterworld.plugin, new Runnable() {
			public void run() {
				player.setHealth(2);
			}
		}, 5L);
	}
}
