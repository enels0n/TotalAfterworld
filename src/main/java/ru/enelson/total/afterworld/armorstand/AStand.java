package ru.enelson.total.afterworld.armorstand;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import ru.enelson.total.afterworld.corpses.Corpse;

public class AStand {
	private Player player;
	private ArmorStand stand;
	private Long createTime;
	private Corpse corpse;
	
	AStand(Player player, Corpse corpse) {
		this.player = player;
		this.corpse = corpse;
		
		this.stand = (ArmorStand) player.getWorld().spawn(player.getLocation().clone().add(0,0.1,0), ArmorStand.class);
		this.stand.setCustomName(ChatColor.translateAlternateColorCodes('&', "*&eОсматривает труп "+corpse.getPlayerName()+"&f*"));
		this.stand.setCustomNameVisible(true);
		this.stand.setGravity(false);
		this.stand.setVisible(false);
		this.stand.setInvisible(true);
		
		this.createTime = System.currentTimeMillis()/1000;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public ArmorStand getStand() {
		return this.stand;
	}
	
	public Long getCreateTime() {
		return this.createTime;
	}
	
	public Corpse getCorpse() {
		return this.corpse;
	}
	
	public void destroy() {
		this.stand.remove();
	}
	
	public void updateLocation() {
		this.stand.teleport(this.player.getLocation().clone().add(0,0.1,0));
	}
}
