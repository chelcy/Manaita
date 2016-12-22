package net.mchel.plugin.manaita.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.sql.MySQL;
import net.mchel.plugin.manaita.util.MyLogger;
import net.mchel.plugin.manaita.util.PlayerID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RankManager {

	private Manaita plugin;
	private MySQL sql;
	private MyLogger logger;
	private PlayerID playerid;
	public RankManager(Manaita manaita) {
		this.plugin = manaita;
		this.sql = plugin.getSQL();
		this.logger = plugin.getMyLogger();
		this.playerid = plugin.getPlayerID();
	}


	private String info_select = "select rank from Mn_info where player_id=%ID%;";
	private String info_update = "update Mn_info set rank=%RANK% , time=now() where player_id=%ID%;";

	private String log_insert = "insert into Mn_ranklog (player_id , uuid , latest_name , rank_old , rank_aft , block , "
			+ " result , time) values (%ID% , '%UUID%' , '%NAME%' , %OLD% , %AFTER% , %BLOCK% , %RESULT% , now());";

	private HashMap<Integer , Integer> ranklist = new HashMap<Integer , Integer>();


	public String getRankPrefix(Player p) {
		Ranks rank = getRank(p);
		return ChatColor.DARK_GRAY + "[" + rank.getRankColor() + rank.getRankSName() + ChatColor.DARK_GRAY + "]";
	}


	public Ranks getRank(Player p) {
		return getRank(getRankInt(p));
	}

	private int getRankInt(Player p) {
		int player_id = playerid.getPlayerID(p);
		if (ranklist.containsKey(player_id)) {
			return ranklist.get(player_id);
		}
		String query = info_select;
		query = query.replace("%ID%", String.valueOf(player_id));
		ResultSet rs = sql.SQLExecuteQuery(query);
		try {
			if (rs == null || !rs.next()) {
				return 0;
			} else {
				int rank = rs.getInt("rank");
				ranklist.put(player_id, rank);
				return rank;
			}
		} catch (SQLException e) {
			logger.error(e);
			return 0;
		}
	}


	public boolean setRank(Player p , int i) {
		return setRank(p , getRank(i));
	}

	public boolean setRank(Player p , Ranks r) {
		int player_id = playerid.getPlayerID(p);
		int old = getRank(p).getRankNum();
		int after = r.getRankNum();
		String update = info_update;
		update = update.replace("%RANK%", String.valueOf(after));
		update = update.replace("%ID%", String.valueOf(player_id));
		boolean result = sql.SQLExecuteUpdate(update);
		ranklist.put(player_id, after);
		addlog(p , old , after , result);
		plugin.getNScore().updateTeam();
		return false;
	}

	public boolean rankUp(Player p) {
		int rank = getRank(p).getRankNum();
		setRank(p , rank + 1);
		return false;
	}


	private void addlog(Player p , int old , int after , boolean result) {
		int player_id = playerid.getPlayerID(p);
		String insert = log_insert;
		insert = insert.replace("%ID%", String.valueOf(player_id));
		insert = insert.replace("%UUID%", p.getUniqueId().toString());
		insert = insert.replace("%NAME%", p.getName());
		insert = insert.replace("%OLD%", String.valueOf(old));
		insert = insert.replace("%AFTER%", String.valueOf(after));
		insert = insert.replace("%BLOCK%", String.valueOf(plugin.getBreakBlockManager().getBlockNum(p)));
		insert = insert.replace("%RESULT%", String.valueOf(result));
		sql.SQLExecuteUpdate(insert);
	}

	/*
	 * 階級
	 * empire
	 * domain
	 * kingdom
	 * group
	 * phylum
	 * section
	 * class
	 * legion
	 * cohort
	 * order
	 * family
	 * tribe
	 * genus
	 * species
	 */

	public enum Ranks {

		Noob(0 , "Noob" , "Noob" , ChatColor.WHITE , 0),
		Species( 1 , "Species" , "SPE" , ChatColor.DARK_GRAY , 10000),
		Genus(2 , "Genus" , "GEN" , ChatColor.GRAY , 30000),
		Tribe(3 , "Tribe" , "TRI" , ChatColor.DARK_GREEN , 50000),
		Family(4 , "Family" , "FAM" , ChatColor.DARK_PURPLE , 80000),
		Order(5 , "Order" , "ORD" , ChatColor.BLACK , 100000),
		Cohort(6 , "Cohort" , "COH" , ChatColor.DARK_AQUA , 300000),
		Legion(7 , "Legion" , "LEG" , ChatColor.DARK_BLUE , 500000),
		Class(8 , "Class" , "CLA" , ChatColor.DARK_RED , 800000),
		Section(9 , "Section" , "SEC" , ChatColor.LIGHT_PURPLE , 1000000),
		Phylum(10 , "Phylum" , "PHY" , ChatColor.YELLOW , 2000000),
		Group(11 , "Group" , "GRO" , ChatColor.RED , 3000000),
		Kingdom(12 , "Kingdom" , "KIN" , ChatColor.BLUE , 5000000),
		Domain(13 , "Domain" , "DOM" , ChatColor.GREEN , 8000000),
		Empire(14 , "Empire" , "EMP" , ChatColor.GOLD , 10000000),
		Admin(50 , "Admin" , "Admin" , ChatColor.AQUA , -1);

		private int number;
		private String name;
		private String sname;
		private ChatColor color;
		private int block;

		Ranks(int num , String name , String sname , ChatColor color , int block) {
			this.number = num;
			this.name = name;
			this.sname = sname;
			this.color = color;
			this.block = block;
		}

		public int getRankNum() {
			return number;
		}
		public String getRankName() {
			return name;
		}
		public String getRankSName() {
			return sname;
		}
		public ChatColor getRankColor() {
			return color;
		}
		public int getRankBlock() {
			return block;
		}
	}

	public Ranks getRank(int i) {
		for (Ranks r : Ranks.values()) {
			if (r.getRankNum() == i) {
				return r;
			}
		}
		return null;
	}

	public ChatColor getRankColor(Player p) {
		return getRank(p).getRankColor();
	}

	public String getRankName(Player p) {
		return getRank(p).getRankName();
	}

	public String getRankSName(Player p) {
		return getRank(p).getRankSName();
	}



}
