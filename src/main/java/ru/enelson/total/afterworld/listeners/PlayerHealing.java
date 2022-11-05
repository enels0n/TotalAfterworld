package ru.enelson.total.afterworld.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import ru.enelson.total.afterworld.TotalAfterworld;

public class PlayerHealing implements Listener {
	@EventHandler
	public void playerRegen(EntityRegainHealthEvent e) {
		if((e.getEntity() instanceof Player) && TotalAfterworld.afterworld.equals(e.getEntity().getWorld().getName()))
			e.setCancelled(true);
	}
}
