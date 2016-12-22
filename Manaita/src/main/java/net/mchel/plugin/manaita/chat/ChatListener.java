package net.mchel.plugin.manaita.chat;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.mchel.plugin.manaita.Manaita;

/**
 * @author chelcy
 */
public class ChatListener implements Listener{

	private ChatChannelManager ccm;
	private RomaToHira rth;
	private String regionall = ChatColor.GRAY + "[All] ";
	private String prechat = ChatColor.YELLOW + "" + ChatColor.BOLD + " Channel" + ChatColor.RESET
			+ ChatColor.GOLD + " >" + ChatColor.YELLOW + "> " + ChatColor.RESET;
	private HashMap<Player , String> beforeMsg = new HashMap<Player , String>();
	private String prefix;

	public ChatListener(Manaita plugin) {
		prefix = plugin.getPrefix();
		this.ccm = plugin.getChatChannelManager();
		this.rth = new RomaToHira();
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (beforeMsg.containsKey(p) && beforeMsg.get(p).equals(e.getMessage())) {
			p.sendMessage(prefix + "同じメッセージを送ることはできません。");
		}
		beforeMsg.put(p, e.getMessage());
		ChatChannel c = ccm.getChannel(p);
		if (c != null) { //チャンネルに入室していた場合
			e.setCancelled(true);
			String msg = ChatColor.GRAY + "[" + c.getOwner().getName() + "] " + ccm.getPlayerPrefix(p)
					+ ChatColor.translateAlternateColorCodes('&', rth.RomaToHiragana(e.getMessage()));
			c.broadcast(msg);
			sendConsole(msg);
			for (Player pl : Bukkit.getOnlinePlayers()) {
				ChatChannel ch = ccm.getChannel(pl);
				if (pl.hasPermission("manaita.manage") && (ch == null || ch != c)) {
					pl.sendMessage(msg);
				}
			}
		} else if (p.hasPermission("manaita.manage")) { //権限持ちの場合
			e.setFormat(regionall + ccm.getPlayerPrefix(p) + ChatColor.RED + ChatColor.translateAlternateColorCodes('&', rth.RomaToHiragana(e.getMessage())));
		} else { //チャンネルに入室しておらず権限もない場合
			e.setCancelled(true);
			String msg = regionall + ccm.getPlayerPrefix(p) + ChatColor.translateAlternateColorCodes('&', rth.RomaToHiragana(e.getMessage()));
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (ccm.getChannel(pl) == null || pl.hasPermission("manaita.manage")) {
					pl.sendMessage(msg);
				}
			}
			sendConsole(msg);
		}
	}

	private void sendConsole(String s) {
		Bukkit.getServer().getConsoleSender().sendMessage(s);
	}

	@EventHandler
	public void onLeft(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		ccm.removeChannel(p);
		ccm.removeChannelMenber(p);
		ccm.removeInvite(p);
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e) {
		final String inventoryName = ChatColor.stripColor(e.getInventory().getName());
		if (!inventoryName.startsWith("Chat")) {
			return;
		}
		if (e.getSlotType() != SlotType.CONTAINER) {
			return;
		}
		e.setCancelled(true);
		ItemStack is = e.getCurrentItem();
		if (is == null || is.getType() == Material.AIR) {
			return;
		}
		Player p = (Player)e.getWhoClicked();
		if (inventoryName.contains("MainMenu")) {
			//最初のインベントリメニュー
			clickMainMenu(p , is);
			return;
		}
		if (inventoryName.contains("InviteMenu")) {
			//招待するプレイヤーの選択画面
			clickInviteMenu(p , is);
			return;
		}
		if (inventoryName.contains("InvitedChannels")) {
			//招待されているチャンネル一覧をクリック
			clickInvitedChannels(p , is);
			return;
		}
		if (inventoryName.contains("JoinMenu")) {
			//参加メニューをクリック
			clickJoinMenu(p , is);
			return;
		}
	}

	@SuppressWarnings("deprecation")
	private void clickMainMenu(Player p , ItemStack is) {
		if (is.getType() != Material.WOOL) {
			return;
		}
		DyeColor color = DyeColor.getByData(is.getData().getData());
		switch(color) {
		case PINK: {
			//create
			ccm.createChannel(p);
			ccm.openChatChannelMenu(p);
			break;
		}
		case LIME: {
			//join
			ccm.openChannelJoinMenu(p);
			break;
		}
		case CYAN: {
			//leave
			ccm.removeChannelMenber(p);
			p.closeInventory();
			break;
		}
		case RED: {
			//delete
			ccm.removeChannel(p);
			p.closeInventory();
			break;
		}
		case BLUE: {
			//invite
			ccm.openChatChannelInvite(p);
			break;
		}
		case BROWN: {
			//invited
			ccm.openInvitedChannel(p);
			break;
		}
		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void clickInviteMenu(Player p , ItemStack is) {
		if (is.getType() != Material.SKULL_ITEM || is.getData().getData() != (short)SkullType.PLAYER.ordinal()) {
			return;
		}
		SkullMeta meta = (SkullMeta)is.getItemMeta();
		String owner = meta.getOwner();
		Player target = Bukkit.getPlayer(owner);
		if (target != null) {
			ccm.sendInvite(p, target);
		} else {
			p.sendMessage(prechat + owner + " はオフラインです");
		}
		ccm.openChatChannelInvite(p);
	}

	//招待されたチャンネルの一覧をクリックした時
	@SuppressWarnings("deprecation")
	private void clickInvitedChannels(Player p , ItemStack is) {
		if (is.getType() != Material.SKULL_ITEM || is.getData().getData() != (short)SkullType.PLAYER.ordinal()) {
			return;
		}
		SkullMeta meta = (SkullMeta)is.getItemMeta();
		String owner = meta.getOwner();
		Player target = Bukkit.getPlayer(owner);
		if (target != null) {
			ccm.approvalJoinToChatChannel(target, p);
			p.closeInventory();
		} else {
			p.sendMessage(prechat + owner + " はオフラインです");
			ccm.openInvitedChannel(p);
		}
	}

	//JoinMenuをクリックしたときの処理
	@SuppressWarnings("deprecation")
	private void clickJoinMenu(Player p , ItemStack is) {
		if (is.getType() != Material.SKULL_ITEM || is.getData().getData() != (short)SkullType.PLAYER.ordinal()) {
			return;
		}
		SkullMeta meta = (SkullMeta)is.getItemMeta();
		String owner = meta.getOwner();
		Player target = Bukkit.getPlayer(owner);
		if (target != null) {
			ChatChannel c = ccm.getChannel(target);
			if (c == null) {
				p.sendMessage(prechat + owner + " のチャンネルが見つかりませんでした。");
				ccm.openChannelJoinMenu(p);
				return;
			}
			c.addMenber(p);
			p.closeInventory();
		} else {
			p.sendMessage(prechat + owner + " はオフラインです");
			ccm.openChannelJoinMenu(p);
		}
	}

}
