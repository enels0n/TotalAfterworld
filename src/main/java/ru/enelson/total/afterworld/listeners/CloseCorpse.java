package ru.enelson.total.afterworld.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import ru.enelson.total.afterworld.TotalAfterworld;

public class CloseCorpse implements Listener {
	@EventHandler
	public void closeCorpse(InventoryCloseEvent e) {
		if(!(e.getPlayer() instanceof Player))
			return;
		TotalAfterworld.am.removeAS((Player)e.getPlayer());
	}
}
