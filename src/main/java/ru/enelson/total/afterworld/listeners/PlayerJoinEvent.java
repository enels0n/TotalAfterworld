package ru.enelson.total.afterworld.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.data.PlayerData;

public class PlayerJoinEvent implements Listener {
	@EventHandler
	public void onJoin(org.bukkit.event.player.PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = TotalAfterworld.playerManager.addPlayer(p);
		
		if(pd.isDead()) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',TotalAfterworld.config.getString("messages.death")));
			return;
		}
		if(pd.getPlayer().getGameMode() == GameMode.SPECTATOR)
			return;
		if(!TotalAfterworld.worlds.contains(p.getWorld().getName()))
			return;
		if(!p.getWorld().getBlockAt(p.getLocation().clone().add(0, -1, 0)).getType().isSolid())
			return;
		if(p.getWorld().getBlockAt(p.getLocation()).getType() == Material.LAVA)
			return;
		
		pd.setLastSafeLocation(p.getLocation());
	}
}
