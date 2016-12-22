package net.mchel.plugin.manaita.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.api.SendMessagePacketAPI;
import net.mchel.plugin.manaita.util.JsonBuilder;
import net.mchel.plugin.manaita.util.JsonBuilder.JSONClickEvent;
import net.mchel.plugin.manaita.util.JsonBuilder.JSONParam;
import net.mchel.plugin.manaita.util.JsonBuilder.JSONPart;

/**
 * @author chelcy
 */
public class ChatChannelManager{

	private Manaita plugin;
	public ChatChannelManager(Manaita plugin) {
		this.plugin = plugin;
		prepareMenuItem();
	}

	private String con = ChatColor.RESET + "" + ChatColor.DARK_GREEN + " >" + ChatColor.GREEN + "> " + ChatColor.RESET;
	private String prechat = ChatColor.YELLOW + "" + ChatColor.BOLD + " Channel" + ChatColor.RESET
			+ ChatColor.GOLD + " >" + ChatColor.YELLOW + "> " + ChatColor.RESET;

	private List<ChatChannel> channels = new ArrayList<ChatChannel>();

	public boolean createChannel(Player owner) {
		if (getChannel(owner) != null) {
			removeChannelMenber(owner);
		}
		if (getChannelFromOwner(owner) != null) {
			removeChannel(owner);
		}
		channels.add(new ChatChannel(owner));
		owner.sendMessage(prechat + "チャンネル作成に成功しました。");
		return true;
	}

	public boolean removeChannel(Player owner) {
		ChatChannel c = getChannelFromOwner(owner);
		if (c != null) {
			return removeChannel(c);
		}
		return false;
	}
	public boolean removeChannel(ChatChannel c) {
		for (Player p : c.getAllPlayers()) {
			p.sendMessage(prechat + p.getName() + ChatColor.GREEN + " left the channel. [" + c.getOwner().getName() + "]");
			p.sendMessage(prechat + "チャンネルが削除されました。");
		}
		channels.remove(c);
		return true;
	}

	public boolean addChannelMenber(Player owner , Player...players ) {
		return addChannelMenber(getChannelFromOwner(owner) , players);
	}
	public boolean addChannelMenber(ChatChannel c , Player...players) {
		if (c == null || players == null) {
			return false;
		}
		removeChannelMenber(players);
		c.addMenber(players);
		return true;
	}

	public boolean removeChannelMenber(Player...players) {
		if (channels.size() == 0 || players.length == 0) {
			return false;
		}
		for (Player p : players) {
			for (ChatChannel c : channels) {
				removeChannelMenber(c , p);
			}
		}
		return true;
	}
	public boolean removeChannelMenber(ChatChannel c , Player p) {
		if (c.isMenber(p)) {
			c.removeMenber(p);
		}
		return true;
	}


	public ChatChannel getChannel(Player menber) {
		for (ChatChannel c : channels) {
			if (c.isOwner(menber) || c.isMenber(menber)) {
				return c;
			}
		}
		return null;
	}

	private ChatChannel getChannelFromOwner(Player owner) {
		for (ChatChannel c : channels) {
			if (c.isOwner(owner)) {
				return c;
			}
		}
		return null;
	}


	public String getPlayerPrefix(Player p) {
		return plugin.getRankManager().getRankPrefix(p) + ChatColor.RESET + p.getName() + con;
	}

	public List<ChatChannel> getChannelList() {
		return channels;
	}

	/**
	 * 最初のメニューを表示
	 * @param p
	 */
	public void openChatChannelMenu(Player p) {
		Inventory inv = Bukkit.createInventory(null , 27 , ChatColor.DARK_PURPLE + "ChatChannel MainMenu");
		ChatChannel c = getChannel(p);
		boolean chCreate = false;
		boolean chJoin = false;
		boolean chLeave = false;
		boolean chDelete = false;
		boolean chInvite = false;
		boolean chInvited = false;
		if (p.hasPermission("manaita.manage")) { //adminの場合
			chCreate = true; chJoin = true; chDelete = true; chInvited = true;
			if (c != null) {
				chInvite = true;
				if (c.getOwner() != p) {
					chLeave = true;
				}
			}
		} else if (getChannelFromOwner(p) != null) { //プレイヤーがオーナーのチャンネルがある場合
			//プレイヤー招待 チャンネル削除選択
			chInvite = true; chDelete = true;
		} else if (getChannel(p) != null) { //オーナーではないがチャンネルに入っている場合
			//チャンネルから退出する 新しくチャンネルを作る
			chLeave = true; chCreate = true;
		} else { //どこのチャンネルにも入っていない場合
			//チャンネルを作成する 招待されているチャンネルに入る
			chCreate = true; chInvited = true;
		}
		if (chCreate) {
			inv.setItem(10, chCreateItem);
		}
		if (chJoin) {
			inv.setItem(11, chJoinItem);
		}
		if (chLeave) {
			inv.setItem(12, chLeaveItem);
		}
		if (chDelete) {
			inv.setItem(14, chDeleteItem);
		}
		if (chInvite) {
			inv.setItem(15, chInviteItem);
		}
		if (chInvited) {
			inv.setItem(16, chInvitedItem);
		}
		p.openInventory(inv);
	}
	//メニューの準備
	private ItemStack chCreateItem;
	private ItemStack chJoinItem;
	private ItemStack chLeaveItem;
	private ItemStack chDeleteItem;
	private ItemStack chInviteItem;
	private ItemStack chInvitedItem;
	private void prepareMenuItem() {
		//create
		String[] create = {ChatColor.YELLOW + "クリックでチャンネルを新規作成"};
		chCreateItem = createMenuItemStack(DyeColor.PINK , ChatColor.LIGHT_PURPLE + "チャンネルを作成する" , create);
		//join
		String[] join = {ChatColor.YELLOW + "クリックでチャンネルに入室"};
		chJoinItem = createMenuItemStack(DyeColor.LIME , ChatColor.LIGHT_PURPLE + "チャンネルに入室する" , join);
		//leave
		String[] leave = {ChatColor.YELLOW + "クリックでチャンネルから退室"};
		chLeaveItem = createMenuItemStack(DyeColor.CYAN , ChatColor.LIGHT_PURPLE + "チャンネルから退室する" , leave);
		//delete
		String[] delete = {ChatColor.YELLOW + "クリックでチャンネルを削除"};
		chDeleteItem = createMenuItemStack(DyeColor.RED , ChatColor.LIGHT_PURPLE + "チャンネルを削除する" , delete);
		//invite
		String[] invite = {ChatColor.YELLOW + "クリックでプレイヤーをチャンネルに招待"};
		chInviteItem = createMenuItemStack(DyeColor.BLUE , ChatColor.LIGHT_PURPLE + "プレイヤーを招待する" , invite);
		//invited
		String[] invited = {ChatColor.YELLOW + "クリックで招待されているチャンネルに入室"};
		chInvitedItem = createMenuItemStack(DyeColor.BROWN , ChatColor.LIGHT_PURPLE + "チャンネルに入室する" , invited);
	}
	@SuppressWarnings("deprecation")
	private ItemStack createMenuItemStack(DyeColor dc , String name , String[] lore) {
		ItemStack is = new ItemStack(Material.WOOL , 1 , dc.getData());
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.setLore(Arrays.asList(lore));
		is.setItemMeta(im);
		return is;
	}



	/**
	 * プレイヤーの招待インベントリ表示
	 * @param p owner
	 */
	public void openChatChannelInvite(final Player p) {
		ChatChannel c = getChannel(p);
		final List<Player> channelm = c.getAllPlayers();
		final List<Player> invited = getInvitePlayers(p);
		final List<Player> list = new ArrayList<Player>() {{
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (!channelm.contains(pl) && !invited.contains(pl)) { //自分のメンバーじゃないかつまだ招待していない
					add(pl);
				}
			}
		}};
		Inventory inv = Bukkit.createInventory(null , 45 , ChatColor.DARK_PURPLE + "ChatChannel InviteMenu");
		if (list.size() > 45) {
			Collections.shuffle(list);
		}
		for (int i = 0 ; i < list.size() && i < 45 ; i++) {
			ItemStack skull = new ItemStack(Material.SKULL_ITEM , 1 , (short) SkullType.PLAYER.ordinal());
			SkullMeta skullmeta = (SkullMeta)skull.getItemMeta();
			skullmeta.setOwner(list.get(i).getName());
			skullmeta.setDisplayName(list.get(i).getName() + " を招待する。");
			skull.setItemMeta(skullmeta);
			inv.setItem(i, skull);
		}
		p.openInventory(inv);
	}

	//招待リスト
	//player owner , player invited
	private List<InvitePair> invitelist = new ArrayList<InvitePair>();

	public void sendInvite(Player from , Player to) {
		//招待リスト調整
		InvitePair ip = getInvite(from , to);
		if (ip != null) {
			return;
		}
		invitelist.add(new InvitePair(from , to));
		to.sendMessage(prechat + from.getName() + " さんからチャットチャンネルへの招待が来ています。");
		List<JSONPart> list = new ArrayList<JSONPart>();
		list.add(new JSONPart(" >" , ChatColor.GOLD));
		list.add(new JSONPart("> " , ChatColor.YELLOW));
		list.add(new JSONPart("招待を受けますか？" , ChatColor.WHITE));
		list.add(new JSONPart(" [ 承認 ] " , ChatColor.GREEN).setParam(JSONParam.BOLD).setClickEvent(JSONClickEvent.RUN_COMMAND, "/ch approval " + from.getName()));
		list.add(new JSONPart(" [ 拒否 ] " , ChatColor.RED).setParam(JSONParam.BOLD).setClickEvent(JSONClickEvent.RUN_COMMAND, "/ch reject " + from.getName()));
		SendMessagePacketAPI.sendTellrawMessage(to, JsonBuilder.JSONString(list));
	}

	/**
	 * 招待承認で追加
	 * @param owner
	 * @param invited
	 */
	public void approvalJoinToChatChannel(Player owner ,Player invited) {
		InvitePair ip = getInvite(owner , invited);
		if (ip == null) {
			return;
		} else while (invitelist.contains(ip)){
			invitelist.remove(ip);
		}
		addChannelMenber(owner , invited);
	}

	/**
	 * 招待拒否
	 * @param owner
	 * @param invited
	 */
	public void rejectInvite(Player owner , Player invited) {
		InvitePair ip = getInvite(owner , invited);
		if (ip == null) {
			return;
		} else while (invitelist.contains(ip)){
			invitelist.remove(ip);
		}
		owner.sendMessage(prechat + invited.getName() + " への招待が拒否されました。");
		invited.sendMessage(prechat + owner.getName() + " からの招待を拒否しました。");
	}

	/**
	 * 招待を受けているチャンネルを表示します
	 * @param invited 招待を受けているプレイヤー
	 */
	public void openInvitedChannel(Player invited) {
		List<Player> owners = new ArrayList<Player>();
		if (invitelist.size() == 0) {
			invited.sendMessage(prechat + "招待を受けているチャンネルはありません。");
			invited.closeInventory();
			return;
		}
		for (InvitePair ip : invitelist) {
			if (ip.getTo() == invited) {
				owners.add(ip.getFrom());
			}
		}
		if (owners.size() == 0) {
			invited.sendMessage(prechat + "招待を受けているチャンネルはありません。");
			invited.closeInventory();
			return;
		}
		Inventory inv = Bukkit.createInventory(null , 45 , ChatColor.DARK_PURPLE + "ChatChannel InvitedChannels");
		if (owners.size() > 45) {
			Collections.shuffle(owners);
		}
		for (int i = 0 ; i < owners.size() && i < 45 ; i++) {
			ItemStack skull = new ItemStack(Material.SKULL_ITEM , 1 , (short) SkullType.PLAYER.ordinal());
			SkullMeta skullmeta = (SkullMeta)skull.getItemMeta();
			skullmeta.setOwner(owners.get(i).getName());
			skullmeta.setDisplayName(owners.get(i).getName() + " の招待を受ける。");
			skull.setItemMeta(skullmeta);
			inv.setItem(i, skull);
		}
		invited.openInventory(inv);
	}

	/**
	 * 招待済みプレイヤーリスト
	 * @param owner
	 * @return
	 */
	private List<Player> getInvitePlayers(Player owner) {
		List<Player> list = new ArrayList<Player>();
		if (invitelist.size() == 0) {
			return list;
		}
		for (InvitePair ip : invitelist) {
			if (ip.getFrom().equals(owner) && ip.getTo().isOnline()) {
				list.add(ip.getTo());
			}
		}
		return list;
	}


	private class InvitePair {
		private Player from;
		private Player to;
		public InvitePair(Player from , Player to) {
			this.from = from;
			this.to = to;
		}
		public Player getFrom() {
			return from;
		}
		public Player getTo() {
			return to;
		}
	}

	private InvitePair getInvite(Player owner , Player invited) {
		if (invitelist.size() == 0) {
			return null;
		}
		for (InvitePair ip : invitelist) {
			if (ip.getFrom() == owner && ip.getTo() == invited) {
				return ip;
			}
		}
		return null;
	}

	//招待破棄
	public void removeInvite(Player p) {
		if (invitelist.size() == 0) {
			return;
		}
		List<InvitePair> list = new ArrayList<InvitePair>();
		list.addAll(invitelist);
		for (InvitePair ip : list) {
			if (ip.getFrom() == p || ip.getTo() == p) {
				invitelist.remove(ip);
			}
		}
	}

	/**
	 * 存在するチャットチャンネルの一覧を表示します。
	 * @param p
	 */
	public void openChannelJoinMenu(Player p) {
		if (channels == null || channels.size() == 0) {
			p.sendMessage(prechat + "チャンネルが存在しません。");
			return;
		}
		List<Player> owners = new ArrayList<Player>();
		for (ChatChannel c : channels) {
			if (c.getOwner() != null) {
				owners.add(c.getOwner());
			}
		}
		Inventory inv = Bukkit.createInventory(null , 45 , ChatColor.DARK_PURPLE + "ChatChannel JoinMenu");
		if (owners.size() > 45) {
			Collections.shuffle(owners);
		}
		for (int i = 0 ; i < owners.size() && i < 45 ; i++) {
			ItemStack skull = new ItemStack(Material.SKULL_ITEM , 1 , (short) SkullType.PLAYER.ordinal());
			SkullMeta skullmeta = (SkullMeta)skull.getItemMeta();
			skullmeta.setOwner(owners.get(i).getName());
			skullmeta.setDisplayName(owners.get(i).getName() + " のチャンネルへ入室する。");
			skull.setItemMeta(skullmeta);
			inv.setItem(i, skull);
		}
		p.openInventory(inv);
	}


}
