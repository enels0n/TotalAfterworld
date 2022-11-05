package ru.enelson.total.afterworld.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.corpses.Corpse;

public class InventoryHandler implements Listener {
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Corpse corpse = TotalAfterworld.corpseManager.getCorpse(e.getInventory());
		if(corpse!=null) {
			corpse.setNotSafe();
			return;
		}
		
		corpse = TotalAfterworld.corpseManager.getCorpse(e.getClickedInventory());
		if(corpse!=null) {
			corpse.setNotSafe();
		}
	}
	
	@EventHandler
	public void onMove(InventoryDragEvent e) {
		Corpse corpse = TotalAfterworld.corpseManager.getCorpse(e.getInventory());
		if(corpse!=null) {
			corpse.setNotSafe();
			return;
		}
	}
}
