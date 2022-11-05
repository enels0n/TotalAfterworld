package ru.enelson.total.afterworld.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.data.PlayerData;

public class PlayerTeleportHandler implements Listener {
	@EventHandler
	public void checkTeleport(org.bukkit.event.player.PlayerTeleportEvent e) {
		PlayerData pd = TotalAfterworld.playerManager.getPlayerData(e.getPlayer());
		
		if(pd == null)
			return;
		
		if (pd.isDead() && !TotalAfterworld.afterworld.equals(e.getTo().getWorld().getName())) {
			e.setCancelled(true);
			return;
		}

		if (!pd.isDead() && TotalAfterworld.afterworld.equals(e.getTo().getWorld().getName())
				&& !(e.getPlayer().hasPermission("magesreincarnation.bypass") || e.getPlayer().isOp())) {
			e.setCancelled(true);
			return;
		}
	}
}
