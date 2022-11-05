package ru.enelson.total.afterworld;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.reflect.ClassPath;

import dev.sergiferry.playernpc.api.NPCLib;
import ru.enelson.total.afterworld.armorstand.ASManager;
import ru.enelson.total.afterworld.command.MainCommand;
import ru.enelson.total.afterworld.corpses.CorpseManager;
import ru.enelson.total.afterworld.data.PlayerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TotalAfterworld extends org.bukkit.plugin.java.JavaPlugin implements Listener {

	public static Plugin plugin;

	public static File FileConfig;

	public static FileConfiguration config;

	public static String reincarnationPoint;

	public static String afterworld;
	public static String pluginVersion;

	public static List<String> worlds;

	public static PlayerManager playerManager;
	public static CorpseManager corpseManager;
	public static Map<Integer, Integer> spawns;

	public static int minX;
	public static int maxX;
	public static int minZ;
	public static int maxZ;
	
	public static ASManager am;
	
	public void onEnable() {
		TotalAfterworld.plugin = this;
		
		if(!NPCLib.getInstance().isRegistered(TotalAfterworld.plugin))
			NPCLib.getInstance().registerPlugin(TotalAfterworld.plugin);
		else {
			this.onDisable();
		}

		FileConfig = new File(getDataFolder(), "config.yml");
		if (!FileConfig.exists())
			saveResource("config.yml", true);
		config = YamlConfiguration.loadConfiguration(FileConfig);

		File f = new File(plugin.getDataFolder() + "/players/");
		if(!f.exists())
		    f.mkdir();
		
		File f1 = new File(plugin.getDataFolder() + "/corpses/");
		if(!f1.exists())
		    f1.mkdir();
		
		TotalAfterworld.afterworld = TotalAfterworld.config.getString("afterworld");
		TotalAfterworld.worlds = TotalAfterworld.config.getStringList("enabled-worlds");
		TotalAfterworld.minX = TotalAfterworld.config.getInt("portal.minX");
		TotalAfterworld.maxX = TotalAfterworld.config.getInt("portal.maxX");
		TotalAfterworld.minZ = TotalAfterworld.config.getInt("portal.minZ");
		TotalAfterworld.maxZ = TotalAfterworld.config.getInt("portal.maxZ");
		TotalAfterworld.reincarnationPoint = TotalAfterworld.config.getString("default-reincarnation-point");
				
		this.getCommand("totalafterworld").setExecutor(new MainCommand());

		playerManager = new PlayerManager(this);
		corpseManager = new CorpseManager();

		spawns = new HashMap<Integer, Integer>();
		for (String key : TotalAfterworld.config.getConfigurationSection("spawns").getKeys(false))
			spawns.put(Integer.parseInt(key), TotalAfterworld.config.getInt("spawns." + key));

		PluginManager pluginManager = Bukkit.getPluginManager();
		try {
			for (ClassPath.ClassInfo clazzInfo : ClassPath.from(getClassLoader()).getTopLevelClasses("ru.enelson.total.afterworld.listeners")) {
				Class<?> clazz = Class.forName(clazzInfo.getName());
				if (Listener.class.isAssignableFrom(clazz)) {
					pluginManager.registerEvents((Listener)clazz.getDeclaredConstructor().newInstance(), this);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		runnable.runTaskLater(this, 1L);
		am = new ASManager();
	}

	public void onDisable() {
		corpseManager.deInit();
	}

	BukkitRunnable runnable = new BukkitRunnable() {
		public void run() {
			TotalAfterworld.pluginVersion = getDescription().getVersion();
		}
	};
}
