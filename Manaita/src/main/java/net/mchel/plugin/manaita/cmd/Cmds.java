package net.mchel.plugin.manaita.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.manager.MenuManager;
import net.mchel.plugin.manaita.manager.statistics.StatisticsManager;

/**
 * @author chelcy
 */
public class Cmds implements CommandExecutor {

	private Manaita plugin;
	private String prefix;
	private MenuManager mm;
	private StatisticsManager stm;
	public Cmds(Manaita pl) {
		this.plugin = pl;
		this.prefix = plugin.getPrefix();
		this.mm = plugin.getMenuManager();
		this.stm = plugin.getStatistics();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (label.equalsIgnoreCase("rank")) {
			mm.opRank(sender);
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(prefix + "プレイヤーチャットから実行してください。");
			return true;
		}
		Player p = (Player)sender;

		switch (cmd.getName()) {

		case "menu": {
			mm.opemMemu(p);
			return true;
		}

		case "craft": {
			mm.opCraft(p);
			return true;
		}

		case "gomi": {
			mm.opGomi(p);
			return true;
		}

		case "lobby": {
			mm.opLobby(p);
			return true;
		}

		case "spawn": {
			mm.opSpawn(p, args);
			return true;
		}

		case "manaita": {
			mm.opManaita(p);
			return true;
		}

		case "manaitahelp": {
			mm.opManaitahelp(p);
			return true;
		}

		case "sethome": {
			mm.opSethome(p);
			return true;
		}

		case "home": {
			mm.opHome(p, args);
			return true;
		}

		case "shop": {
			mm.opShop(p);
			return true;
		}

		case "rule": {
			mm.opRule(p);
			return true;
		}

		case "hn": {
			if (args.length == 0) {
				mm.giveHalfBlocks(p, 1);
			} else if (isNumber(args[0])){
				mm.giveHalfBlocks(p, Integer.valueOf(args[0]).intValue());
			} else {
				p.sendMessage(prefix + "数字を指定してください。");
			}
			return true;
		}

		case "statistics": {
			stm.opStatistics(p);
			return true;
		}

		default: {
			return false;
		}

		}

	}

	private boolean isNumber(String val) {
		String regex = "\\A[-]?[0-9]+\\z";
		Pattern p = Pattern.compile(regex);
		Matcher m1 = p.matcher(val);
		return m1.find();
	}

}
