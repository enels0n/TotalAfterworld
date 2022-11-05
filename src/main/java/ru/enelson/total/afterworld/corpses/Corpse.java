package ru.enelson.total.afterworld.corpses;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import net.md_5.bungee.api.ChatColor;
import ru.enelson.total.afterworld.TotalAfterworld;

public class Corpse {
	private Long createTime;
	private NPC.Global npc;
	private Inventory inv;
	private String playerName;
	private String uuid;
	private boolean saved;
	private Location location;
	
	Corpse(Player player) {
		this.saved = true;
		this.uuid = player.getUniqueId().toString();
		this.createTime = System.currentTimeMillis()/1000;
		
		this.playerName = player.getName();
		this.inv = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&7Труп &f"+player.getName()));
		this.inv.setContents(player.getInventory().getContents());
		
		this.location = TotalAfterworld.playerManager.getPlayerData(player).getLastSafeLocation();

		this.createNPC(player);
		
		this.save();
	}
	

	Corpse(String uuid, Long createTime, ItemStack[] inventory, String playerName, Location location) {
		this.uuid = uuid;
		this.createTime = createTime;
		this.playerName = playerName;
		this.location = location;
		
		this.inv = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&7Труп &f"+this.playerName));
		this.inv.setContents(inventory);
		
		this.createNPC(null);
	}
	
	public Long getCreateTime() {
		return this.createTime;
	}
	
	public String getPlayerName() {
		return this.playerName;
	}
	
	public NPC getNPC() {
		return this.npc;
	}
	
	public boolean isSaved() {
		return this.saved;
	}
	
	public void setNotSafe() {
		this.saved = false;
	}
	
	public Inventory getInv() {
		return this.inv;
	}
	
	public NPC.Global getCorpse() {
		return this.npc;
	}
	
	public void removeCorpse() {
		this.npc.destroy();
		NPCLib.getInstance().removeGlobalNPC(this.npc);
		
		this.closeInventories();

		String filePath = TotalAfterworld.plugin.getDataFolder().getAbsolutePath() + File.separator + "corpses/"
				+ this.uuid + ".yml";
		File file = new File(filePath);
		if (file.exists())
			file.delete();
	}
	
	public void dropItems() {
		for(ItemStack item : this.inv.getContents()) {
			if(item!=null)
				this.npc.getWorld().dropItem(this.npc.getLocation(), item.clone());
		}
		this.inv.clear();
	}

	public void returnItems(Player player) {
		
		this.closeInventories();
		
		ItemStack[] inventory = this.inv.getContents();
		ItemStack helmet = null;
		ItemStack chestplate = null;
		ItemStack leggings = null;
		ItemStack boots = null;
		ItemStack offHand = null;
		
		if(inventory[39]!=null) {
			helmet = inventory[39].clone();
			inventory[39] = null;
		}
		if(inventory[38]!=null) {
			chestplate = inventory[38].clone();
			inventory[38] = null;
		}
		if(inventory[37]!=null) {
			leggings = inventory[37].clone();
			inventory[37] = null;
		}
		if(inventory[36]!=null) {
			boots = inventory[36].clone();
			inventory[36] = null;
		}
		if(inventory[40]!=null) {
			offHand = inventory[40].clone();
			inventory[40] = null;
		}
		for(int y = 41; y<45;) {
			if(inventory[y]!=null)
				player.getWorld().dropItem(player.getLocation(), inventory[y].clone());
			inventory[y++] = null;
		}
		
		inventory = Arrays.stream(inventory)
                .filter(s -> (s != null))
                .toArray(ItemStack[]::new);

		player.getInventory().addItem(inventory);
		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
		player.getInventory().setItemInOffHand(offHand);
	}
	
	public void save() {
		String filePath = TotalAfterworld.plugin.getDataFolder().getAbsolutePath() + File.separator + "corpses/"
				+ this.uuid + ".yml";
		File file = new File(filePath);
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		
		FileConfiguration config = new YamlConfiguration();
		config.set("createTime", createTime);
		config.set("playerName", playerName);
		config.set("location", this.npc.getLocation());

		int i = 0;
		for (ItemStack item : inv.getContents()) {
			config.set("inventory." + i, item);
			i++;
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
		}
		this.saved = true;
	}
	
	public void closeInventories() {
		for (int i = this.inv.getViewers().size() - 1; i >= 0; --i) {
			Player p = (Player)this.inv.getViewers().get(i);
			p.closeInventory();
			TotalAfterworld.am.removeAS(p);
		}
	}
	
	private void createNPC(Player player) {
		this.npc = NPCLib.getInstance().generateGlobalNPC(TotalAfterworld.plugin, this.playerName, this.location);

		this.npc.setSkin(player);
		this.npc.setText(ChatColor.translateAlternateColorCodes('&', "&7Труп &f"+this.playerName));
		this.npc.setTextAlignment(new Vector(0,0.3,0));
		this.npc.setPose(NPC.Pose.SLEEPING);
		this.npc.setAutoCreate(true);
		this.npc.setAutoShow(true);

		this.npc.forceUpdateText();
		Bukkit.getScheduler().runTaskLater(TotalAfterworld.plugin, () -> {
			this.npc.setHideDistance(10);
			this.npc.update();
		}, 10);
	}
}
