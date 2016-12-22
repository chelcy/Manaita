package net.mchel.plugin.manaita.manager.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.util.PlayerID;
import net.md_5.bungee.api.ChatColor;

/**
 * @author chelcy
 */
public class StatisticsManager implements StatisticsUtil.StatisticsCallback{

	private Manaita plugin;
	private String prefix;
	private PlayerID playerid;
	private StatisticsUtil stutil;
	public StatisticsManager(Manaita plugin) {
		this.plugin = plugin;
		this.prefix = plugin.getPrefix();
		this.playerid = plugin.getPlayerID();
		stutil = new StatisticsUtil(plugin.getSQL());
		stutil.setCallbacks(this);
	}

	private HashMap<UUID , HashMap<Integer , Integer>> map = new HashMap<UUID , HashMap<Integer , Integer>>();


	/**
	 * こーるばっく
	 */
	@Override
	public synchronized void callbackMethod(UUID taskid , final int typeid , final int number) {
		if (map.containsKey(taskid)) {
			map.get(taskid).put(typeid, number);
		} else {
			map.put(taskid, new HashMap<Integer , Integer>() {{
				put(typeid , number);
			}});
		}
		if (checkComplete(taskid)) {
			sendStatisticsMessage(taskid);
		} else {
			Player p = Bukkit.getPlayer(taskid);
			if (p != null && p.isOnline()) {
				p.sendMessage(prefix + "進行状況 " + map.get(taskid).size() + " / 6");
			}
		}
	}

	/**
	 * マップ内全部そろったか確認
	 * @param taskid
	 * @return
	 */
	private boolean checkComplete(UUID taskid) {
		if (!map.containsKey(taskid)) {
			return false;
		}
		if (map.get(taskid).size() != 6) {
			return false;
		}
		for (int i = 1 ; i < 7 ; i++) {
			if (!map.get(taskid).containsKey(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * メッセージを送る
	 * @param taskid
	 */
	private void sendStatisticsMessage(UUID taskid) {
		Player p = Bukkit.getPlayer(taskid);
		if (p == null || !p.isOnline()) {
			return;
		}
		String[] msg = {
				prefix + "----------Statistics----------",
				prefix + "Last 24H : " + ChatColor.GREEN + map.get(taskid).get(1) + ChatColor.RESET + " blocks.",
				prefix + "Last 1week : " + ChatColor.GREEN + map.get(taskid).get(2) + ChatColor.RESET + " blocks.",
				prefix + "Last 1month : " + ChatColor.GREEN + map.get(taskid).get(3) + ChatColor.RESET + " blocks.",
				prefix + "Today : " + ChatColor.GREEN + map.get(taskid).get(4) + ChatColor.RESET + " blocks.",
				prefix + "This week : " + ChatColor.GREEN + map.get(taskid).get(5) + ChatColor.RESET + " blocks.",
				prefix + "This month : " + ChatColor.GREEN + map.get(taskid).get(6)  + ChatColor.RESET + " blocks.",
				prefix + "------------------------------",
		};
		p.sendMessage(msg);
	}

	private List<Player> interval = new ArrayList<Player>();

	/**
	 * 統計メッセインデックス
	 * @param p
	 */
	public void opStatistics(final Player p) {
		if (interval.contains(p)) {
			p.sendMessage(prefix + "間隔を開けて実行してください。(5分)");
			return;
		}
		interval.add(p);
		new BukkitRunnable() {
			@Override
			public void run() {
				while (interval.contains(p)) {
					interval.remove(p);
				}
			}
		}.runTaskLaterAsynchronously(plugin, 6000);
		p.sendMessage(prefix + "計算をしています。しばらくお待ちください。");
		final UUID taskid = p.getUniqueId();
		if (map.containsKey(taskid)) {
			map.remove(taskid);
		}
		final int id = playerid.getPlayerID(p);
		new BukkitRunnable() {
			@Override
			public void run() {
				stutil.get24H(taskid, id);
			}
		}.runTaskAsynchronously(plugin);
		new BukkitRunnable() {
			@Override
			public void run() {
				stutil.get1Week(taskid, id);
			}
		}.runTaskAsynchronously(plugin);
		new BukkitRunnable() {
			@Override
			public void run() {
				stutil.get1Month(taskid, id);
			}
		}.runTaskAsynchronously(plugin);
		new BukkitRunnable() {
			@Override
			public void run() {
				stutil.getToday(taskid, id);
			}
		}.runTaskAsynchronously(plugin);
		new BukkitRunnable() {
			@Override
			public void run() {
				stutil.getThisWeek(taskid, id);
			}
		}.runTaskAsynchronously(plugin);
		new BukkitRunnable() {
			@Override
			public void run() {
				stutil.getThisMonth(taskid, id);
			}
		}.runTaskAsynchronously(plugin);
	}


}
