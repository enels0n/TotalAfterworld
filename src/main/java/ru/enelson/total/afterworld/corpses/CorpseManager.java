package ru.enelson.total.afterworld.corpses;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import dev.sergiferry.playernpc.api.NPC;
import ru.enelson.total.afterworld.TotalAfterworld;

public class CorpseManager {
	private List<Corpse> corpses;
	private BukkitTask taskRemover;
	
	public CorpseManager() {
		this.corpses = new ArrayList<Corpse>();

		for (File file : new File(TotalAfterworld.plugin.getDataFolder().getAbsolutePath() + File.separator + "corpses/").listFiles()) {
			if(!file.getName().endsWith(".yml"))
				continue;
			
			String name = file.getName();
			String uuid = name.replaceAll("(?<!^)[.]" + "[^.]*$", "");
			
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			Long createTime = config.getLong("createTime");
			String playerName = config.getString("playerName");
			Location location = config.getLocation("location");
			ItemStack[] inventory = new ItemStack[45];
			
			for(int i = 0; i < inventory.length; i++) {
				inventory[i] = config.getItemStack("inventory."+i);
			}
			
			this.corpses.add(new Corpse(uuid, createTime, inventory, playerName, location));
	    }
		
		this.taskRemover = Bukkit.getScheduler().runTaskTimer(TotalAfterworld.plugin, () -> {
			for(Iterator<Corpse> it = this.corpses.iterator(); it.hasNext();) {
				Corpse corpse = it.next();
				if(corpse.getCreateTime()+TotalAfterworld.config.getInt("decrease-time") <= System.currentTimeMillis()/1000) {
					corpse.dropItems();
					corpse.removeCorpse();
					it.remove();
				}
				else if(!corpse.isSaved()){
					corpse.save();
				}
			}
		}, 5*20, 5*20);
	}
	
	public void createCorpse(Player player) {
		if(!player.getInventory().isEmpty()) {
			Corpse corpse = new Corpse(player);
			this.corpses.add(corpse);
		}
	}
	
	public Corpse getCorpse(Player player) {
		return this.corpses.stream().filter(c -> c.getPlayerName().equals(player.getName())).findFirst().orElse(null);
	}
	
	public Corpse getCorpse(NPC npc) {
		return this.corpses.stream().filter(c -> npc.getSimpleCode().equals("global_"+c.getNPC().getSimpleCode())).findFirst().orElse(null);
	}
	
	public Corpse getCorpse(Inventory inv) {
		return this.corpses.stream().filter(c -> c.getInv().equals(inv)).findFirst().orElse(null);
	}
	
	public void removeCorpse(Player player) {
		Corpse corpse = this.getCorpse(player);
		if(corpse != null) {
			corpse.removeCorpse();
			this.corpses.remove(corpse);
		}
	}
	
	public void removeCorpse(NPC npc) {
		Corpse corpse = this.getCorpse(npc);
		if(corpse != null) {
			corpse.removeCorpse();
			this.corpses.remove(corpse);
		}
	}
	
	public void deInit() {
		this.corpses.forEach(c -> { c.save(); c.closeInventories(); });
		this.taskRemover.cancel();
	}
}
