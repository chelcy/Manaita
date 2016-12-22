package net.mchel.plugin.manaita.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.mchel.plugin.manaita.Manaita;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Home {

	private Manaita plugin;
	private String select = "select x,y,z,pitch,yaw from Mn_home where player_id=%ID% and world='%WORLD%';";
	private String select2 = "select x from Mn_home where player_id=%ID% and world='%WORLD%';";
	private String update = "update Mn_home set latest_name='%NAME%' , x=%X% , y=%Y% , z=%Z% , pitch=%PITCH% , yaw=%YAW% "
			+ ", time=now() where player_id='%ID%' and world='%WORLD%';";
	private String insert = "insert into Mn_home (player_id , uuid , latest_name , world , x , y , z , pitch , yaw , time) "
			+ "values (%ID% , '%UUID%' , '%NAME%' , '%WORLD%' , %X% , %Y% , %Z% , %PITCH% , %YAW% , now());";

	public Home(Manaita manaita) {
		this.plugin = manaita;
	}

	public Location getHomeLocation(Player p) {
		return getHomeLocation(p , p.getWorld().getName());
	}

	public Location getHomeLocation(Player p , String world) {
		return getHomeLocation(p.getUniqueId().toString() , world);
	}

	public Location getHomeLocation(String uuid , String world) {
		int id = plugin.getPlayerID().getPlayerID(uuid);
		String query = select.replace("%ID%", String.valueOf(id));
		query = query.replace("%WORLD%", world);
		ResultSet rs = plugin.getSQL().SQLExecuteQuery(query);
		try {
			if (rs == null || !rs.next()) {
				return null;
			} else {
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				double z = rs.getDouble("z");
				float pitch = rs.getFloat("pitch");
				float yaw = rs.getFloat("yaw");
				Location loc = new Location(Bukkit.getWorld(world), x, y, z);
				loc.setPitch(pitch);
				loc.setYaw(yaw);
				return loc;
			}
		} catch (SQLException e) {
			return null;
		}
	}

	public boolean setHomeLocation(Player p) {
		int id = plugin.getPlayerID().getPlayerID(p);
		String world = p.getLocation().getWorld().getName();
		String name = p.getName();
		String sel = select2.replace("%ID%", String.valueOf(id));
		sel = sel.replace("%WORLD%", p.getWorld().getName());
		double x = p.getLocation().getX();
		double y = p.getLocation().getY();
		double z = p.getLocation().getZ();
		float pitch = p.getLocation().getPitch();
		float yaw = p.getLocation().getYaw();
		ResultSet rs = plugin.getSQL().SQLExecuteQuery(sel);
		try {
			if (rs == null || !rs.next()) {
				String ins = insert;
				ins = ins.replace("%NAME%", name);
				ins = ins.replace("%ID%", String.valueOf(id));
				ins = ins.replace("%UUID%", p.getUniqueId().toString());
				ins = ins.replace("%WORLD%", world);
				ins = ins.replace("%X%", String.valueOf(x));
				ins = ins.replace("%Y%", String.valueOf(y));
				ins = ins.replace("%Z%", String.valueOf(z));
				ins = ins.replace("%PITCH%", String.valueOf(pitch));
				ins = ins.replace("%YAW%", String.valueOf(yaw));
				return plugin.getSQL().SQLExecuteUpdate(ins);
			} else {
				String upd = update;
				upd = upd.replace("%NAME%", name);
				upd = upd.replace("%ID%", String.valueOf(id));
				upd = upd.replace("%WORLD%", world);
				upd = upd.replace("%X%", String.valueOf(x));
				upd = upd.replace("%Y%", String.valueOf(y));
				upd = upd.replace("%Z%", String.valueOf(z));
				upd = upd.replace("%PITCH%", String.valueOf(pitch));
				upd = upd.replace("%YAW%", String.valueOf(yaw));
				return plugin.getSQL().SQLExecuteUpdate(upd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


}
