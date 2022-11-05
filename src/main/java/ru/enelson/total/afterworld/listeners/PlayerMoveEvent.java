package ru.enelson.total.afterworld.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.corpses.Corpse;
import ru.enelson.total.afterworld.data.PlayerData;

public class PlayerMoveEvent implements Listener {
	@EventHandler
	public void checkMove(org.bukkit.event.player.PlayerMoveEvent e) {
		Player player = e.getPlayer();
		PlayerData pd = TotalAfterworld.playerManager.getPlayerData(player);

		if (pd.isDead()) {
			if (!TotalAfterworld.afterworld.equals(player.getWorld().getName())) {
				player.teleport(pd.getAfterworldLocation());
				return;
			}

			Location loc = pd.getPortalLocation();
			if (!(player.getLocation().getX() >= loc.getX() - 1 && player.getLocation().getX() <= loc.getX() + 1
					&& player.getLocation().getY() >= loc.getY() - 1 && player.getLocation().getY() <= loc.getY() + 2
					&& player.getLocation().getZ() >= loc.getZ() - 1 && player.getLocation().getZ() <= loc.getZ() + 3))
				return;

			pd.setDead(false);
			player.teleport(pd.getLastSafeLocation());
			Corpse corpse = TotalAfterworld.corpseManager.getCorpse(player);
			if(corpse != null) {
				corpse.returnItems(player);
			}
			TotalAfterworld.corpseManager.removeCorpse(player);
			
			player.setHealth(10);
			player.setFoodLevel(10);
		} else if (TotalAfterworld.afterworld.equals(player.getWorld().getName())
				&& !(player.hasPermission("magesreincarnation.bypass") || player.isOp())) {
			player.teleport(pd.getLastSafeLocation());
		}
	}
}
