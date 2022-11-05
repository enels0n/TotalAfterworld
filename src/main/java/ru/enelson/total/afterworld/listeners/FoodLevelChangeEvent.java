package ru.enelson.total.afterworld.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.data.PlayerData;

public class FoodLevelChangeEvent implements Listener {
	@EventHandler
	public void entityDead(org.bukkit.event.entity.FoodLevelChangeEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;

		Player player = (Player) e.getEntity();
		PlayerData pd = TotalAfterworld.playerManager.getPlayerData(player);

		if (!pd.isDead())
			return;
		if (!TotalAfterworld.afterworld.equals(e.getEntity().getWorld().getName()))
			return;
		
		e.setCancelled(true);
		player.setFoodLevel(20);
	}
}
