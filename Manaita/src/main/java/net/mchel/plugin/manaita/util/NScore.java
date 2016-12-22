package net.mchel.plugin.manaita.util;

import java.util.ArrayList;
import java.util.List;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.manager.BreakBlockManager;
import net.mchel.plugin.manaita.manager.RankManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NScore {

	private Manaita plugin;
	private RankManager rankm;
	private Scoreboard nboard;
	private BreakBlockManager bbm;
	public NScore(Manaita manaita) {
		this.plugin = manaita;
		this.rankm = plugin.getRankManager();
		this.bbm = plugin.getBreakBlockManager();
		this.nboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		blockrun();
	}


	private void blockrun() {
		new BukkitRunnable() {
			@Override
			public void run() {
				updateBlockNum();
			}
		}.runTaskTimer(plugin, 20, 1200);
	}


	public void updateScoreBoard() {
		updateTeam();
		updateBlockNum();
	}


	@SuppressWarnings("deprecation")
	public void updateTeam() {
		for (Team team : nboard.getTeams()) {
			team.unregister();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			Team team = null;
			team = nboard.registerNewTeam(p.getName());
			team.setPrefix(rankm.getRankPrefix(p) + ChatColor.RESET);
			team.addPlayer(p);
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(nboard);
		}
	}



	//ブロック破壊個数の情報表示(tab list)
	@SuppressWarnings("deprecation")
	public void updateBlockNum() {
		List<Player> playerlist = new ArrayList<Player>();
		playerlist.addAll(plugin.getServer().getOnlinePlayers());
		if (playerlist.size() == 0) {
			return;
		}
		for (Player p : playerlist) {
			Scoreboard board = p.getScoreboard();
			if (board.getObjective("blocks") != null) {
				board.getObjective("blocks").unregister();
			}
			Objective ob = board.registerNewObjective("blocks", "dummy");
			ob.setDisplaySlot(DisplaySlot.PLAYER_LIST);
			for (Player p1 : playerlist) {
				ob.getScore(p1).setScore(bbm.getBlockNum(p1));
			}
			for (Player p1 : playerlist) {
				p1.setScoreboard(board);
			}
			break;
		}
	}




}
