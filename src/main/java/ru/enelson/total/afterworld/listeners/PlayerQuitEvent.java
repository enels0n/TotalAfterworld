package ru.enelson.total.afterworld.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ru.enelson.total.afterworld.TotalAfterworld;

public class PlayerQuitEvent implements Listener {
	@EventHandler
	public void onQuit(org.bukkit.event.player.PlayerQuitEvent e) {
		TotalAfterworld.playerManager.removePlayer(e.getPlayer());
		Player player = (Player)e.getPlayer();
		TotalAfterworld.am.removeAS(player);
	}
}
