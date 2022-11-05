package ru.enelson.total.afterworld.armorstand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.corpses.Corpse;

public class ASManager {
	private List<AStand> stands;
	private BukkitTask task;
	
	public ASManager() {
		this.stands = new ArrayList<AStand>();
		
		this.task = Bukkit.getScheduler().runTaskTimer(TotalAfterworld.plugin, () -> {
			for(Iterator<AStand> it = this.stands.iterator(); it.hasNext();) {
				AStand stand = it.next();
				if(stand.getCorpse().getNPC() == null ||
						!stand.getCorpse().getInv().getViewers().contains((HumanEntity)stand.getPlayer()) ||
						!stand.getPlayer().isOnline()) {
					stand.destroy();
					it.remove();
				}
				else
					stand.updateLocation();
			}
		}, 5*20, 5*20);
	}
	
	public void createAS(Player player, Corpse corpse) {
		this.removeAS(player);
		this.stands.add(new AStand(player, corpse));
	}
	
	public void removeAS(Player player) {
		AStand stand = this.getAS(player);
		if(stand==null)
			return;
		stand.destroy();
		this.stands.remove(stand);
	}
	
	public AStand getAS(Player player) {
		return this.stands.stream().filter(s -> s.getPlayer().equals(player)).findFirst().orElse(null);
	}
	
	public void deInit() {
		this.stands.forEach(s -> s.destroy());
		this.task.cancel();
	}
}
