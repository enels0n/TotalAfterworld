package ru.enelson.total.afterworld.util;

import java.util.Random;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Material;

import ru.enelson.total.afterworld.TotalAfterworld;
import ru.enelson.total.afterworld.data.PlayerData;

public class Utils {
	static Random random = new Random();
	
	public static Location getLocationInCircle(Location origin, Integer radius) {
		double angle = random.nextInt(360);
		double mineX = origin.getBlockX() + radius * Math.cos(angle);
		double mineZ = origin.getBlockZ() + radius * Math.sin(angle);
		
		Location location = new Location(origin.getWorld(), mineX, 0, mineZ);
		Block block = origin.getWorld().getHighestBlockAt(location);
		return block.getLocation();
	}
	
	public static Integer getRadius(int deaths) {
		int radius = 10;
		for(Entry<Integer, Integer> point : TotalAfterworld.spawns.entrySet()) {
			if(point.getKey() > deaths)
				break;
			radius = point.getValue();
		}
		return radius;
	}
	
	public static Location searchPortalPoint() {
		Location loc = null;
		for(int i = 0; i < 1; i = 0) {
			int x = TotalAfterworld.minX + (int) (Math.random() * ((TotalAfterworld.maxX - (TotalAfterworld.minX)) + 1));
			int z = TotalAfterworld.minZ + (int) (Math.random() * ((TotalAfterworld.maxZ - (TotalAfterworld.minZ)) + 1));
			
			Block block = Bukkit.getWorld(TotalAfterworld.afterworld).getHighestBlockAt(x, z);
			if(block.getType() != Material.LAVA && block.getType() != Material.FIRE && block.getType() != Material.MAGMA_BLOCK) {
				loc = block.getLocation().add(0, 1, 0);
				break;
			}
		}
		return loc;
	}
	
	public static Location getNavigatorLocation(Player player) {
		PlayerData pd = TotalAfterworld.playerManager.getPlayerData(player);
		Vector vector = pd.getPortalLocation().clone().toVector().subtract(player.getLocation().toVector()).normalize().multiply(7);
		Location loc = player.getLocation().clone().add(vector);
		loc.setY(player.getLocation().getY()+1.4);
		return loc;
	}
	
	public static boolean hasPermit(Player player) {
		for(String st : TotalAfterworld.config.getConfigurationSection("requirements").getKeys(false)) {
			if(!Utils.checkRequire(player,
					TotalAfterworld.config.getString("requirements."+st+".type"),
					TotalAfterworld.config.getString("requirements."+st+".input"),
					TotalAfterworld.config.getString("requirements."+st+".output"))
					)
				return false;
		}
		return true;
	}
	
	public static boolean checkRequire(Player player, String type, String input, String output) {
		if(input!=null)
			input = PlaceholderAPI.setPlaceholders(player, input);
		if(output!=null)
			output = PlaceholderAPI.setPlaceholders(player, output);
		final String input1 = input;

		switch(type) {
		case("=="):
			return input.equals(output);
		case("!="):
			return !input.equals(output);
		case(">"):
			return Double.parseDouble(input) > Double.parseDouble(output);
		case(">="):
			return Double.parseDouble(input) >= Double.parseDouble(output);
		case("<"):
			return Double.parseDouble(input) < Double.parseDouble(output);
		case("<="):
			return Double.parseDouble(input) <= Double.parseDouble(output);
		case("has perm"):
			return (!player.isPermissionSet(input) || 
					!player.getEffectivePermissions().stream()
					.filter(pai -> pai.getPermission().equals(input1)).findFirst().get().getValue());
		case("!has perm"):
			return (!(player.isPermissionSet(input) && 
					player.getEffectivePermissions().stream()
					.filter(pai -> pai.getPermission().equals(input1)).findFirst().get().getValue()));
		default:
			return false;
		}
	}
}
