package net.mchel.plugin.manaita.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.sql.MySQL;
import net.mchel.plugin.manaita.util.MyLogger;
import net.mchel.plugin.manaita.util.PlayerID;
import net.mchel.plugin.pointapi.PointAPI;

public class BreakBlockManager {

	private Manaita plugin;
	private MySQL sql;
	private Connection conn;
	private MyLogger logger;
	private PlayerID playerid;
	private PointAPI pointapi;
	private String prefix;
	private RankManager rankm;
	public BreakBlockManager(Manaita manaita) {
		this.plugin = manaita;
		this.sql = plugin.getSQL();
		this.conn = sql.getConnection();
		this.logger = plugin.getMyLogger();
		this.playerid = plugin.getPlayerID();
		this.pointapi = plugin.getPointAPI();
		this.prefix = plugin.getPrefix();
		this.rankm = plugin.getRankManager();
	}

	private String info_select = "select block from Mn_info where player_id=%ID%;";
	private String info_update = "update Mn_info set block=%BLOCK% , time=now() where player_id=%ID%;";
	private String info_select_top = "select latest_name , block from Mn_info order by `block` desc limit 10;";
	private String info_select_rank_name = "SELECT (block), (SELECT COUNT(*) + 1 FROM `Mn_info` b WHERE b.block "
			+ "> a.block) AS `rank` FROM `Mn_info` a WHERE `latest_name` = '%NAME%';";
	private String info_select_rank_id = "SELECT (block), (SELECT COUNT(*) + 1 FROM `Mn_info` b WHERE b.block "
			+ "> a.block) AS `rank` FROM `Mn_info` a WHERE `player_id` = '%ID%';";

	private String log_insert_new = "insert into Mn_blocklog (player_id , uuid , latest_name , world , x , y , z , block_id , "
			+ "block_meta , result , epoch) values (? , ? , ? , ? , ? , ? , ? "
			+ ", ? , ? , ? , ?);";

	private String log_countall = "select log_id from `Mn_blocklog` order by log_id DESC LIMIT 1;";


	public void addBreakBlockNumber(Player player , Location loc , int blockid , byte dataid) {
		synchronized(plugin) {
			int player_id = playerid.getPlayerID(player);
			String select = info_select;
			select = select.replace("%ID%", String.valueOf(player_id));
			ResultSet rs = sql.SQLExecuteQuery(select);
			try {
				if (rs == null || !rs.next()) {
					logger.warn("infoデータがありません。player:" + player.getName());
					addLog(player , loc , blockid , dataid , false);
					return;
				} else {
					int blockdata = rs.getInt("block") + 1;
					String update = info_update;
					update = update.replace("%BLOCK%", String.valueOf(blockdata));
					update = update.replace("%ID%", String.valueOf(player_id));
					boolean result = sql.SQLExecuteUpdate(update);
					addLog(player , loc , blockid , dataid , result);
					checkBlock(player , blockdata);
					return;
				}
			} catch (Exception e) {
				logger.error(e);
				addLog(player , loc , blockid , dataid , false);
			}
		}
	}


	private void addLog(Player player , Location loc , int blockid , byte dataid , boolean result) {
		try {
			PreparedStatement ps = conn.prepareStatement(log_insert_new);
			ps.setInt(1, playerid.getPlayerID(player));
			ps.setString(2, player.getUniqueId().toString());
			ps.setString(3, player.getName());
			ps.setString(4, loc.getWorld().getName());
			ps.setInt(5, loc.getBlockX());
			ps.setInt(6, loc.getBlockY());
			ps.setInt(7, loc.getBlockZ());
			ps.setInt(8, blockid);
			ps.setByte(9, dataid);
			ps.setBoolean(10, result);
			ps.setLong(11, System.currentTimeMillis()/1000);
			ps.executeUpdate();
			sql.close(ps);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public int getBlockNum(Player p) {
		int player_id = playerid.getPlayerID(p);
		String select = info_select;
		select = select.replace("%ID%", String.valueOf(player_id));
		ResultSet rs = sql.SQLExecuteQuery(select);
		try {
			if (rs == null || !rs.next()) {
				return 0;
			} else {
				return rs.getInt("block");
			}
		} catch (SQLException e) {
			logger.error(e);
			return 0;
		}
	}

	private void checkBlock(Player p , int blockdata) {
		if (blockdata % 1000 == 0) {
			if (plugin.getChellYoubi()) {
				if (pointapi.addPoint(p, 2 , "[Manaita]Break 1000 time blocks"))
				p.sendMessage(prefix + ChatColor.GOLD + "[ちぇる曜日ポイント2倍デー]" + ChatColor.RESET + "2ポイント追加 ("
						+ blockdata + " ブロック破壊 , 合計ポイント: " + pointapi.getPoint(p) + " Chell)");
			} else {
				if (pointapi.addPoint(p, 1 , "[Manaita]Break 1000 time blocks"))
				p.sendMessage(prefix + "1ポイント追加 (" + blockdata + " ブロック破壊 , 合計ポイント: " + pointapi.getPoint(p) + " Chell)");
			}
		}
		double d = Math.random();
		if (d < 0.00001) {
			if (pointapi.addPoint(p, 10, "[Manaita]Break bonus"))
			p.sendMessage(prefix + "[" + ChatColor.AQUA + "ボーナスポイント" + ChatColor.RESET + "]" + ChatColor.GREEN
					+ "10" + ChatColor.RESET + "ポイント追加 (合計ポイント: " + pointapi.getPoint(p) + " Chell)");
		}
		int rank = rankm.getRank(p).getRankNum();
		if (rank >= 14) {
			return;
		}
		int nextblock = rankm.getRank(rank + 1).getRankBlock();
		if (blockdata == nextblock) {
			rankm.rankUp(p);
			p.sendMessage(prefix + "おめでとうございます。ランクが " + rankm.getRankName(p) + " になりました。");
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 2f, 1f);
			plugin.getServer().broadcastMessage(prefix + p.getName() + " さんが " + rankm.getRankName(p)
					+ " に昇格しました。(破壊ブロック数: " + nextblock + " )");
		}
	}

	public int getAllBreakBlock() {
		ResultSet rs = sql.SQLExecuteQuery(log_countall);
		try {
			if (rs == null || !rs.next()) {
				return -1;
			} else {
				return rs.getInt("log_id");
			}
		} catch (SQLException e) {
			logger.error(e);
			return -1;
		}
	}

	public List<Entry<String, Integer>> getRankTop() {
		HashMap<String , Integer> list = new HashMap<String , Integer>();
		ResultSet rs = sql.SQLExecuteQuery(info_select_top);
		try {
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				list.put(rs.getString("latest_name"), rs.getInt("block"));
			}
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
		return sort(list);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Map.Entry<String , Integer>> sort(HashMap<String , Integer> list) {
		List<Map.Entry<String , Integer>> entries = new ArrayList<Map.Entry<String , Integer>>(list.entrySet());
		Collections.sort(entries, new Comparator(){
			public int compare(Object o1, Object o2){
			Map.Entry e1 =(Map.Entry)o1;
			Map.Entry e2 =(Map.Entry)o2;
			return ((Integer)e2.getValue()).compareTo((Integer)e1.getValue());
			}
		});
		return entries;
	}

	public int[] getBlockRank(String player_name) {
		String query = info_select_rank_name;
		query = query.replace("%NAME%", player_name);
		return getBlockRankCore(query);
	}

	public int[] getBlockRank(Player player) {
		int player_id = playerid.getPlayerID(player);
		String query = info_select_rank_id;
		query = query.replace("%ID%", String.valueOf(player_id));
		return getBlockRankCore(query);
	}

	private int[] getBlockRankCore(String query) {
		ResultSet rs = sql.SQLExecuteQuery(query);
		try {
			if (rs == null || !rs.next()) {
				return null;
			} else {
				int[] num = {
						rs.getInt("block"),
						rs.getInt("rank")
				};
				return num;
			}
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
	}







}
