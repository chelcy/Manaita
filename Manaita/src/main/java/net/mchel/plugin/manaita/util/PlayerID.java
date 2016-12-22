package net.mchel.plugin.manaita.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import net.mchel.plugin.manaita.Manaita;

import org.bukkit.entity.Player;

public class PlayerID {

	private Manaita plugin;

	public PlayerID(Manaita manaita) {
		this.plugin = manaita;
	}

	private String upquery = "select player_id from `network-players` where player='%UUID%';";

	private HashMap<String , Integer> playerlist = new HashMap<String,Integer>();

	public int getPlayerID(Player p) {
		return getPlayerID(p.getUniqueId().toString());
	}

	public int getPlayerID(String uuid) {
		if (uuid == null) {
			return 0;
		}
		if (playerlist.containsKey(uuid)) {
			return playerlist.get(uuid);
		} else {
			String query = upquery.replace("%UUID%", uuid);
			ResultSet rs = plugin.getSQL().SQLExecuteQuery(query);
			try {
				if (rs == null || !rs.next()) {
					return 0;
				} else {
					int id = rs.getInt("player_id");
					playerlist.put(uuid, id);
					return id;
				}
			} catch (SQLException e) {
				return 0;
			}
		}
	}

}
