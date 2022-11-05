package ru.enelson.total.afterworld.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.data.PlayerData;
import ru.enelson.total.afterworld.util.Utils;

public class PlayerDeathEvent implements Listener {
	@EventHandler
	public void setPlayerDead(org.bukkit.event.entity.PlayerDeathEvent e) {
		Player player = (Player) e.getEntity();
		
		if(!TotalAfterworld.worlds.contains(player.getWorld().getName()))
			return;
		if(!Utils.hasPermit(player))
			return;
		
		PlayerData pd = TotalAfterworld.playerManager.getPlayerData(player);
		pd.addDeath();
		pd.setPortal(Utils.searchPortalPoint());
		pd.setDead(true);
		TotalAfterworld.corpseManager.createCorpse(player);
		e.getDrops().clear();
	}
}
