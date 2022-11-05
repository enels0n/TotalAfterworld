package ru.enelson.total.afterworld.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import dev.sergiferry.playernpc.api.NPC;
import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.corpses.Corpse;

public class CorpseInteractHandler implements Listener {

	@EventHandler
	public void onNPCInteract(NPC.Events.Interact event){
	    Player player = event.getPlayer();
	    NPC npc = event.getNPC();
	    
	    Corpse corpse = TotalAfterworld.corpseManager.getCorpse(npc);
	    TotalAfterworld.am.createAS(player, corpse);
	    player.openInventory(corpse.getInv());
	}
}
