package ru.enelson.total.afterworld.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.util.Utils;

public class PlayerManager {

	private Particle.DustOptions portalOption = new Particle.DustOptions(Color.TEAL, 2f);
	private Particle.DustOptions navigatorOption = new Particle.DustOptions(Color.WHITE, 2f);

	public static List<PlayerData> players = new ArrayList<PlayerData>();
	private int setLastSafeLocationTaskId = -1;
	private int createPortalTaskId = -1;
	private int createNavigatorTaskId = -1;
	private int decreaserTaskId = -1;
	
	public PlayerManager(Plugin plugin) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			players.add(new PlayerData(p));
		}
		
		setLastSafeLocationTaskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for (PlayerData pd : players) {
					if(pd.isDead())
						continue;
					if(pd.getPlayer().getGameMode() == GameMode.SPECTATOR)
						continue;
					if(!TotalAfterworld.worlds.contains(pd.getPlayer().getWorld().getName()))
						continue;
					if(!pd.getPlayer().getWorld().getBlockAt(pd.getPlayer().getLocation().clone().add(0, -1, 0)).getType().isSolid())
						continue;
					if(pd.getPlayer().getWorld().getBlockAt(pd.getPlayer().getLocation()).getType() == Material.LAVA)
						continue;
					pd.setLastSafeLocation(pd.getPlayer().getLocation());
				}
			}
		}, 2 * 20L, 2 * 20L);
		
		createPortalTaskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for (PlayerData pd : players) {
					if(!pd.isDead())
						continue;
					if(!pd.getPlayer().getWorld().equals(pd.getPortalLocation().getWorld()))
						continue;
					if(pd.getPlayer().getLocation().distance(pd.getPortalLocation())>100)
						continue;
					for(Location l : pd.getPortalLocations())
						pd.getPlayer().spawnParticle(Particle.REDSTONE, l, 10, 0.12, 0.12, 0.12, portalOption);
				}
			}
		}, 1 * 15L, 1 * 15L);
		
		createNavigatorTaskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for (PlayerData pd : players) {
					if(!pd.isDead())
						continue;
					if(!pd.getPlayer().getWorld().equals(pd.getPortalLocation().getWorld()))
						continue;
					if(pd.getPlayer().getLocation().distance(pd.getPortalLocation())<50) {
						if(pd.isGuideCloseMessageSended())
							continue;
						pd.setGuideClose();
					}
					
					Location loc = Utils.getNavigatorLocation(pd.getPlayer());
					pd.getPlayer().spawnParticle(Particle.REDSTONE, loc, 10, 0.12, 0.12, 0.12, navigatorOption);
				}
			}
		}, 1 * 15L, 1 * 15L);
		
		decreaserTaskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for (PlayerData pd : players) {
					if(pd.getDeaths()<1)
						continue;
					if(pd.getLastDecrease()+TotalAfterworld.config.getLong("decrease-time")>System.currentTimeMillis()/1000)
						continue;
					pd.removeDeath();
				}
			}
		}, 10 * 20L, 300 * 20L);
	}

	public PlayerData getPlayerData(Player player) {
		for (PlayerData pd : players) {
			if(!pd.getPlayer().getName().equalsIgnoreCase(player.getName()))
				continue;
			return pd;
		}
		return null;
	}
	
	public PlayerData getPlayerData(String player) {
		for (PlayerData pd : players) {
			if(!pd.getPlayer().getName().equalsIgnoreCase(player))
				continue;
			return pd;
		}
		return null;
	}
	
	public PlayerData addPlayer(Player player) {
		PlayerData pd = new PlayerData(player);
		players.add(pd);
		return pd;
	}
	
	public void removePlayer(Player player) {
		PlayerData pd = this.getPlayerData(player);
		pd.setLastOnline();
		players.remove(pd);
	}
	
	public void deInit(Plugin plugin) {
		plugin.getServer().getScheduler().cancelTask(setLastSafeLocationTaskId);
		plugin.getServer().getScheduler().cancelTask(createPortalTaskId);
		plugin.getServer().getScheduler().cancelTask(createNavigatorTaskId);
		plugin.getServer().getScheduler().cancelTask(decreaserTaskId);
	}
	
	public Long getLastOnline(String player) {
		File file = getFileOfflinePlayer(player);
		if(file==null)
			return 0L;
		
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		Long lo = cfg.getLong("lastOnline");

		return lo;
	}
	
	private File getFileOfflinePlayer(String player) {
		@SuppressWarnings("deprecation")
		OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		
		String filePath = TotalAfterworld.plugin.getDataFolder().getAbsolutePath() + File.separator + "players/"
				+ op.getUniqueId().toString().toLowerCase() + ".yml";
		File file = new File(filePath);
		if (file.exists())
			return file;
		return null;
	}
}
