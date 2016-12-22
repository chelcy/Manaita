package net.mchel.plugin.manaita.manager;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.api.TabAPI;
import net.mchel.plugin.manaita.api.TitleAPI;
import net.mchel.plugin.manaita.api.TitleAPI.JSONParam;
import net.mchel.plugin.manaita.api.TitleAPI.JSONPart;
import net.mchel.plugin.manaita.sql.MySQL;
import net.mchel.plugin.manaita.util.MyLogger;
import net.mchel.plugin.manaita.util.NScore;
import net.mchel.plugin.manaita.util.PlayerID;

public class JoinLeftManager {

	private Manaita plugin;
	private MySQL sql;
	private PlayerID playerid;
	private TitleAPI titleapi;
	private MyLogger logger;
	private NScore nscore;
	private String prefix;
	private TabAPI tabapi;
	public JoinLeftManager(Manaita manaita) {
		this.plugin = manaita;
		this.prefix = plugin.getPrefix();
		this.sql = plugin.getSQL();
		this.playerid = plugin.getPlayerID();
		this.titleapi = plugin.getTitleAPI();
		this.logger = plugin.getMyLogger();
		this.nscore = plugin.getNScore();
		this.tabapi = plugin.getTabAPI();
		init();
	}

	private String login_insert = "insert into Mn_login (player_id , uuid , player_name , ip_address , time , action) "
			+ "values (%ID% , '%UUID%' , '%NAME%' , '%ADDRESS%' , now() , %ACTION%);";
	private String info_select = "select rank from Mn_info where player_id=%ID%;";
	private String info_insert = "insert into Mn_info (player_id , uuid , latest_name , block , rank , time) "
			+ "values (%ID% , '%UUID%' , '%NAME%' , 0 , %RANK% , now());";
	private String info_update = "update Mn_info set latest_name='%NAME%' , time=now() where player_id=%ID%;";

	private String jsontitle;
	private String jsonsubtitle;

	private void init() {
		List<JSONPart> jlist = new LinkedList<JSONPart>();
		jlist.add(new JSONPart("#" , ChatColor.GOLD));
		jlist.add(new JSONPart("C" , ChatColor.AQUA).setParam(JSONParam.UNDERLINED , JSONParam.BOLD));
		jlist.add(new JSONPart("HELCY" , ChatColor.DARK_AQUA).setParam(JSONParam.UNDERLINED , JSONParam.BOLD));
		jlist.add(new JSONPart("N" , ChatColor.AQUA).setParam(JSONParam.UNDERLINED , JSONParam.BOLD));
		jlist.add(new JSONPart("ETWORK" , ChatColor.DARK_AQUA).setParam(JSONParam.UNDERLINED , JSONParam.BOLD));
		jlist.add(new JSONPart("#" , ChatColor.GOLD));
		jsontitle = titleapi.JSONString(jlist);
		List<JSONPart> jlist2 = new LinkedList<JSONPart>();
		jlist2.add(new JSONPart("Manaita" , ChatColor.YELLOW).setParam(JSONParam.BOLD));
		jlist2.add(new JSONPart(" Server" , ChatColor.RED));
		jsonsubtitle = titleapi.JSONString(jlist2);
	}

	public void onJoin(Player p) {
		insertMn_login(p , 0);
		CheckInsertMn_info(p);
		sendJoinTitle(p);
		nscore.updateScoreBoard();
		tabapi.sendTabList(p);
	}

	public void onLeft(Player p) {
		insertMn_login(p , 1);
	}

	private boolean CheckInsertMn_info(Player p) {
		int player_id = playerid.getPlayerID(p);
		String select = info_select.replace("%ID%", String.valueOf(player_id));
		ResultSet rs = sql.SQLExecuteQuery(select);
		try {
			if (rs == null || !rs.next()) {
				String insert = info_insert;
				insert = insert.replace("%ID%", String.valueOf(player_id));
				insert = insert.replace("%UUID%", p.getUniqueId().toString());
				insert = insert.replace("%NAME%", p.getName());
				int rank = 0;
				if (p.hasPermission("manaita.manage")) {
					rank = 50;
				}
				insert = insert.replace("%RANK%", String.valueOf(rank));
				return sql.SQLExecuteUpdate(insert);
			} else {
				String update = info_update;
				update = update.replace("%NAME%", p.getName());
				update = update.replace("%ID%", String.valueOf(player_id));
				return sql.SQLExecuteUpdate(update);
			}
		} catch (SQLException e) {
			logger.error(e);
			return false;
		}
	}


	private boolean insertMn_login(Player p , int i) {
		int player_id = playerid.getPlayerID(p);
		if (player_id == 0) {
			return false;
		}
		String insert = login_insert;
		insert = insert.replace("%ID%", String.valueOf(player_id));
		insert = insert.replace("%UUID%", p.getUniqueId().toString());
		insert = insert.replace("%NAME%", p.getName());
		insert = insert.replace("%ADDRESS%", p.getAddress().toString().replaceFirst("/", "")
				.replace(String.valueOf(p.getAddress().getPort()), "").replace(":", ""));
		insert = insert.replace("%ACTION%", String.valueOf(i));
		return sql.SQLExecuteUpdate(insert);
	}


	private void sendJoinTitle(Player p) {
		try {
			plugin.getTitleAPI().sendTitleAndSubTitle(p, jsontitle, jsonsubtitle, 20, 60, 20);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchFieldException
				| SecurityException | ClassNotFoundException
				| NoSuchMethodException | InstantiationException e) {
			logger.error(e);
		}
	}


	public void onFirstJoin(final Player p) {
		p.sendMessage(prefix + "はじめまして! Chelcy Manaita Serverにようこそ!");
		p.sendMessage(prefix + "Welcome to Chelcy Manaita Server!!");
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				p.sendMessage(prefix + "ルールページ / rule page http://wiki.mchel.net/manaita/");
				p.sendMessage(prefix + "ルールが書かれていますので、必ずすべて読んでから遊んでください。");
				p.sendMessage(prefix + "Since the rules are written, please playing sure to read all.");
				Location loc = p.getLocation();
				loc.setX(-192.5);
				loc.setY(62);
				loc.setZ(221.5);
				p.teleport(loc);
			}
		} , 40L);
	}





}
