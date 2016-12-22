package net.mchel.plugin.manaita.util;

import net.mchel.plugin.manaita.Manaita;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldUtil {

	@SuppressWarnings("unused")
	private Manaita plugin;

	public WorldUtil(Manaita manaita) {
		this.plugin = manaita;
	}

	public Location getWorldSpawn(String world) {
		if (world == null) {
			return getLobbyWorldSpawn();
		} else {
			return getWorldSpawn(Bukkit.getServer().getWorld(world));
		}
	}

	public Location getWorldSpawn(World world) {
		if (world == null) {
			return getLobbyWorldSpawn();
		} else {
			return convLoc(world.getSpawnLocation());
		}
	}

	public Location getLobbyWorldSpawn() {
		return convLoc(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
	}

	private Location convLoc(Location loc) {
		loc.setX(loc.getX() + 0.5);
		loc.setZ(loc.getZ() + 0.5);
		return loc;
	}

}
