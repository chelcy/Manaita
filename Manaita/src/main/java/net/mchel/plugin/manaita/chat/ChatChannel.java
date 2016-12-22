package net.mchel.plugin.manaita.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author chelcy
 */
public class ChatChannel {

	private UUID owner;
	private List<UUID> menbers = new ArrayList<UUID>();
	private String prechat = ChatColor.YELLOW + "" + ChatColor.BOLD + " Channel" + ChatColor.RESET
			+ ChatColor.GOLD + " >" + ChatColor.YELLOW + "> " + ChatColor.RESET;

	public ChatChannel(Player owner) {
		this.owner = owner.getUniqueId();
		owner.sendMessage(prechat + owner.getName() + ChatColor.GREEN + " joined the channel. [" + owner.getName() + "]");
	}

	public boolean isOwner(Player player) {
		return owner == player.getUniqueId();
	}

	public boolean isMenber(Player player) {
		return menbers.contains(player.getUniqueId());
	}

	public void addMenber(Player... players) {
		if (players == null) {
			return;
		}
		checkPlayerOnline();
		for (Player p : players) {
			if (!isMenber(p)) {
				menbers.add(p.getUniqueId());
				broadcast(prechat + p.getName() + ChatColor.GREEN + " joined the channel. [" + getPlayer(owner).getName() + "]");
			}
		}
	}

	public void removeMenber(Player... players) {
		if (players == null) {
			return;
		}
		checkPlayerOnline();
		for (Player p : players) {
			if (isMenber(p)) {
				broadcast(prechat + p.getName() + ChatColor.GREEN + " left the channel. [" + getPlayer(owner).getName() + "]");
				menbers.remove(p.getUniqueId());
			}
		}
	}

	public List<Player> getMenbers() {
		checkPlayerOnline();
		List<Player> list = new ArrayList<Player>();
		for (UUID u : menbers) {
			list.add(getPlayer(u));
		}
		return list;
	}

	public Player getOwner() {
		return getPlayer(owner);
	}

	public List<Player> getAllPlayers() {
		List<Player> menber = new ArrayList<Player>() {{
			addAll(getMenbers());
			add(getPlayer(owner));
		}};
		return menber;
	}

	public void broadcast(String msg) {
		for (Player p : getAllPlayers()) {
			p.sendMessage(msg);
		}
	}

	private void checkPlayerOnline() {
		List<UUID> list = new ArrayList<UUID>() {{addAll(menbers);}};
		for (UUID u : list) {
			Player p = getPlayer(u);
			if (p == null) {
				menbers.remove(u);
			}
		}
	}

	private Player getPlayer(UUID uuid) {
		return Bukkit.getPlayer(uuid);
	}



}
