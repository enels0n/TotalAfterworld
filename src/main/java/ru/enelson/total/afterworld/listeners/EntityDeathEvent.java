package ru.enelson.total.afterworld.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ru.enelson.total.afterworld.TotalAfterworld;

public class EntityDeathEvent implements Listener {
	@EventHandler
    public void entityDead(org.bukkit.event.entity.EntityDeathEvent e) {
		if(!TotalAfterworld.afterworld.equals(e.getEntity().getWorld().getName()))
			return;
		e.getDrops().clear();
	}
}
