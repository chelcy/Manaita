package net.mchel.plugin.manaita.cmd;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.chat.ChatChannel;
import net.mchel.plugin.manaita.chat.ChatChannelManager;
import net.mchel.plugin.manaita.manager.MenuManager;

/**
 * @author chelcy
 */
public class Cmd_Channel implements CommandExecutor {

	private Manaita plugin;
	private MenuManager mm;
	private ChatChannelManager ccm;
	private String prechat = ChatColor.YELLOW + "" + ChatColor.BOLD + " Channel" + ChatColor.RESET
			+ ChatColor.GOLD + " >" + ChatColor.YELLOW + "> " + ChatColor.RESET;
	private String con = ChatColor.GOLD + " >" + ChatColor.YELLOW + "> " + ChatColor.RESET;
	public Cmd_Channel(Manaita manaita) {
		this.plugin = manaita;
		this.mm = plugin.getMenuManager();
		this.ccm = plugin.getChatChannelManager();
	}

	private String[] helpm = { prechat + "へるぷめっせーじ",
			con + "list - show channel list",
											};

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equals("ch")) {
			return false;
		}

		//権限必要なし

		if (args.length == 0) {
			if (sp(sender)) {
				mm.opChatChannel(getP(sender));
			} else {
				sender.sendMessage(helpm);
			}
			return true;
		}

		switch(args[0]) {

		case "list": {
			List<ChatChannel> list = ccm.getChannelList();
			if (list.size() == 0) {
				sender.sendMessage(prechat + "ChatChannel is not exist.");
				return true;
			}
			sender.sendMessage(prechat + "-----" + ChatColor.GREEN + "Channel List" + ChatColor.RESET + "-----");
			StringBuilder sb = new StringBuilder();
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (ccm.getChannel(p) == null) {
					if (sb.length() == 0) {
						sb.append(p.getName());
					} else {
						sb.append(", " + p.getName());
					}
				}
			}
			sender.sendMessage(con + "[Global] " + sb.toString());
			for (ChatChannel c : list) {
				sender.sendMessage(con + " " + convChannel(c));
			}
			sender.sendMessage(prechat + "----------------------");
			return true;
		}

		case "approval": {
			if (!sp(sender)) {
				sender.sendMessage(prechat + "Please run this command in player chat.");
				return true;
			}
			Player p = getP(sender);
			if (args.length != 2) {
				p.sendMessage(prechat + "/ch approval <player name>");
				return true;
			}
			@SuppressWarnings("deprecation")
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				p.sendMessage(prechat + args[0] + " is offline now.");
				return true;
			}
			ccm.approvalJoinToChatChannel(target , p);
			return true;
		}

		case "reject": {
			if (!sp(sender)) {
				sender.sendMessage(prechat + "Please run this command in player chat.");
				return true;
			}
			Player p = getP(sender);
			if (args.length != 2) {
				p.sendMessage(prechat + "/ch reject <player name>");
				return true;
			}
			@SuppressWarnings("deprecation")
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				p.sendMessage(prechat + args[0] + " is offline now.");
				return true;
			}
			ccm.rejectInvite(target , p);
			return true;
		}
		}


		//ここから権限必須

		if (sp(sender) && !getP(sender).hasPermission("manaita.manage")) {
			sender.sendMessage(prechat + "You don't have permission.");
			return true;
		}

		switch (args[0]) {

		case "join": {

			return true;
		}

		case "add": {

			return true;
		}

		case "remove": {


			return true;
		}

		default : {
			sender.sendMessage(helpm);
			return true;
		}
		}
	}

	private Player getP(CommandSender sender) {
		return (Player)	sender;
	}
	private boolean sp(CommandSender sender) {
		return sender instanceof Player;
	}

	private String convChannel(ChatChannel c) {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GREEN + "*" + ChatColor.RESET + c.getOwner().getName());
		for (Player p : c.getMenbers()) {
			sb.append(", " + p.getName());
		}
		return sb.toString();
	}

}
