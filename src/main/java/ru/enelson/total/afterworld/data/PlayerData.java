package ru.enelson.total.afterworld.data;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.util.Serializer;
import ru.enelson.total.afterworld.util.Utils;

public class PlayerData {

	private Player player;
	private String uuid;
	private Boolean dead;
	private Boolean guideCloseMessageSended;
	private Location portal[];
	private Location lastSafeLocation;
	private int deaths;
	private Long lastDecrease;
	private Long lastOnline;
	private BukkitTask br;
	private File file;
	private FileConfiguration playerConfig;
	
	PlayerData(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId().toString().toLowerCase();
		String filePath = TotalAfterworld.plugin.getDataFolder().getAbsolutePath() + File.separator + "players/"
				+ this.uuid + ".yml";
		this.file = new File(filePath);
		
		if (!this.file.exists()) {
			try {
				this.file.createNewFile();
				FileConfiguration cfg = new YamlConfiguration();
				cfg.set("name", player.getName());
				cfg.set("first-join", System.currentTimeMillis()/1000);
				cfg.set("death-count", 0);
				cfg.set("last-decrease", 0);
				cfg.set("lastOnline", 0);
				cfg.set("lastSafeLocation", TotalAfterworld.reincarnationPoint);
				cfg.set("isDead", false);
				
				cfg.save(this.file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.playerConfig = YamlConfiguration.loadConfiguration(this.file);
		this.lastSafeLocation = Serializer.getDeserializedLocation(this.playerConfig.getString("lastSafeLocation"));
		this.dead = this.playerConfig.getBoolean("isDead");
		this.deaths = this.playerConfig.getInt("death-count");
		this.lastDecrease = this.playerConfig.getLong("last-decrease");
		this.lastOnline = 0L;
		this.portal = Serializer.getDeserializedLocation(this.playerConfig.getStringList("portal"));
		this.guideCloseMessageSended = false;
	}
	
	public Location[] getPortalLocations() {
		return this.portal;
	}
	
	public Location getPortalLocation() {
		return this.portal[0];
	}
	
	public String getName() {
		return this.player.getDisplayName();
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Location getLastSafeLocation() {
		return this.lastSafeLocation;
	}
	
	public Boolean isDead() {
		return this.dead;
	}
	
	public int getDeaths() {
		return this.deaths;
	}
	
	public Long getLastDecrease() {
		return this.lastDecrease;
	}
	
	public Location getAfterworldLocation() {
		return Utils.getLocationInCircle(this.portal[0],Utils.getRadius(this.deaths));
	}
	
	public Long getLastOnline() {
		return this.lastOnline;
	}
	
	public Boolean isGuideCloseMessageSended() {
		return this.guideCloseMessageSended;
	}
	
	public void setLastDecrease(Long lastDecrease) {
		this.lastDecrease = lastDecrease;
		this.saveConfigs();
	}
	
	public void addDeath() {
		this.deaths += 1;
		this.lastDecrease = System.currentTimeMillis()/1000;
		this.saveConfigs();
	}
	
	public void removeDeath() {
		this.deaths -= 1;
		this.lastDecrease = System.currentTimeMillis()/1000;
		this.saveConfigs();
	}
	
	public void setDead(Boolean dead) {
		this.dead = dead;
		this.guideCloseMessageSended = false;
		if(this.br != null)
			this.br.cancel();
		this.saveConfigs();
	}
	
	public void setGuideCloseMessageSended(Boolean sended) {
		this.guideCloseMessageSended = sended;
	}
	
	public void setGuideClose() {
		this.player.sendMessage(ChatColor.translateAlternateColorCodes('&',TotalAfterworld.config.getString("messages.too-close")));
		this.guideCloseMessageSended = true;
		final PlayerData pd = this;
		br = new BukkitRunnable() {
			@Override
			public void run() {
				pd.setGuideCloseMessageSended(false);
			}
		}.runTaskLater(TotalAfterworld.plugin, 1200L);
	}
	
	public void setPortal(Location location) {
		this.portal = createPortal(location);
		this.saveConfigs();
	}
	
	public void setLastSafeLocation(Location location) {
		this.lastSafeLocation = location;
		this.saveConfigs();
	}
	
	public void setLastOnline() {
		this.lastOnline = System.currentTimeMillis()/1000;
		this.saveConfigs();
	}
	
	private Location[] createPortal(Location location) {
		Location locs[] = new Location[17];
		int i;
		
		for(i = 0; i < 17; i++)
			locs[i] = location.clone();

		locs[1].add(0, 0.5, 0);
		locs[2].add(0, 1, 0);
		locs[3].add(0, 1.5, 0);
		locs[4].add(0, 2, 0);
		locs[5].add(0, 2.3, 0.25);
		locs[6].add(0, 2.6, 0.75);
		locs[7].add(0, 2.8, 1);
		locs[8].add(0, 2.8, 1.5);
		locs[9].add(0, 2.8, 2);
		locs[10].add(0, 2.6, 2.3);
		locs[11].add(0, 2.3, 2.7);
		locs[12].add(0, 2, 3);
		locs[13].add(0, 1.5, 3);
		locs[14].add(0, 1, 3);
		locs[15].add(0, 0.5, 3);
		locs[16].add(0, 0, 3);
		
		return locs;
	}
	
	private void saveConfigs() {
		playerConfig.set("death-count", this.deaths);
		playerConfig.set("last-decrease", this.lastDecrease);
		playerConfig.set("lastOnline", this.lastOnline);
		playerConfig.set("lastSafeLocation", Serializer.getSerializedLocation(this.lastSafeLocation));
		playerConfig.set("isDead", this.dead);
		playerConfig.set("portal", Serializer.getSerializedLocation(this.portal));
		
		try {
			this.playerConfig.save(this.file);
		} catch (IOException e) {
		}
	}
}
