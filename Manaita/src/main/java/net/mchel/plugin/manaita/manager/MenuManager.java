package net.mchel.plugin.manaita.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.chat.ChatChannelManager;
import net.mchel.plugin.manaita.shop.ShopItemManager;
import net.mchel.plugin.manaita.util.WorldUtil;

/**
 * @author chelcy
 */
public class MenuManager {

	private Manaita plugin;
	private String prefix;
	private BreakBlockManager bbm;
	private ShopItemManager shopm;
	private WorldUtil worldutil;
	private ChatChannelManager ccm;
	public MenuManager(Manaita pl) {
		this.plugin = pl;
		this.prefix = plugin.getPrefix();
		this.bbm = plugin.getBreakBlockManager();
		this.shopm = plugin.getShopManager();
		this.worldutil = plugin.getWorldUtil();
		this.ccm = plugin.getChatChannelManager();
		begin();
	}

	private Inventory inv;

	private void begin() {
		inv = Bukkit.createInventory(null, 45 , ChatColor.RED + "メニュー/Menu");

		inv.setItem(0, buildItem(Material.WOOD_STEP , ChatColor.AQUA + "Manaita" ,
				ChatColor.GREEN + "各種情報を確認できます。" , ChatColor.GOLD + "/manaita"));
		inv.setItem(3, buildItem(Material.CHEST , ChatColor.AQUA + "Shop" ,
				ChatColor.GREEN + "ショップを表示します。" , ChatColor.GOLD + "/shop"));
		inv.setItem(6, buildItem(Material.STEP , ChatColor.AQUA + "Half Block" ,
				ChatColor.GREEN + "半ブロックを入手します。" , ChatColor.GOLD + "/hn"));

		inv.setItem(9, buildItem(Material.DIAMOND_PICKAXE , ChatColor.AQUA + "Rank" ,
				ChatColor.GREEN + "採掘数ランキングを表示します。" , ChatColor.GOLD + "/rank"));
		inv.setItem(12, buildItem(Material.ENCHANTED_BOOK , ChatColor.AQUA + "Rule" ,
				ChatColor.GREEN + "ルールが記載されているURLを表示します。" , ChatColor.GOLD + "/rule"));
		inv.setItem(15, buildItem(Material.PAPER , ChatColor.AQUA + "ChatChannel" ,
				ChatColor.GREEN + "チャットチャンネル設定をします。" , ChatColor.GOLD + "/ch"));

		inv.setItem(18, buildItem(Material.COMPASS , ChatColor.AQUA + "Home" ,
				ChatColor.GREEN + "ホームへTPします。" , ChatColor.GOLD + "/home"));
		inv.setItem(21, buildItem(Material.WATCH , ChatColor.AQUA + "Sethome" ,
				ChatColor.GREEN + "ホームを設定します。" , ChatColor.GOLD + "/sethome"));

		inv.setItem(27, buildItem(Material.MAGMA_CREAM , ChatColor.AQUA + "Spawn" ,
				ChatColor.GREEN + "スポーンへTPします。" , ChatColor.GOLD + "/spawn"));
		inv.setItem(30, buildItem(Material.NETHER_STAR , ChatColor.AQUA + "Lobby" ,
				ChatColor.GREEN + "ロビーへTPします。" , ChatColor.GOLD + "/lobby"));

		inv.setItem(36, buildItem(Material.WORKBENCH , ChatColor.AQUA + "Craft" ,
				ChatColor.GREEN + "ワークベンチを表示します。" , ChatColor.GOLD + "/craft"));
		inv.setItem(39, buildItem(Material.CAULDRON_ITEM , ChatColor.AQUA + "Gomi" ,
				ChatColor.GREEN + "ゴミ箱を表示します。" , ChatColor.GOLD + "/gomi"));

	}

	private ItemStack buildItem(Material material , String name , String... lores) {
		ItemStack is = new ItemStack(material , 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + name);
		List<String> lore = new ArrayList<String>();
		for (String s : lores) {
			lore.add(ChatColor.RESET + s);
		}
		im.setLore(lore);
		im.addEnchant(Enchantment.DURABILITY, 1, true);
		is.setItemMeta(im);
		return is;
	}


	public void opemMemu(Player p) {
		p.openInventory(inv);
	}

	public void invClickEvent(InventoryClickEvent e) {
		if (e.getSlotType() != SlotType.CONTAINER) {
			return;
		}
		ItemStack is = e.getCurrentItem();
		e.setCancelled(true);
		if (is == null || is.getType() == Material.AIR) {
			return;
		}
		Player p = (Player)e.getWhoClicked();
		p.closeInventory();
		switch (is.getType()) {
		case WOOD_STEP: {
			opManaita(p);
			break;
		}
		case CHEST: {
			opShop(p);
			break;
		}
		case DIAMOND_PICKAXE: {
			opRank(p);
			break;
		}
		case ENCHANTED_BOOK: {
			opRule(p);
			break;
		}
		case COMPASS: {
			opHome(p , null);
			break;
		}
		case WATCH: {
			opSethome(p);
			break;
		}
		case MAGMA_CREAM: {
			opSpawn(p, null);
			break;
		}
		case NETHER_STAR: {
			opLobby(p);
			break;
		}
		case WORKBENCH: {
			opCraft(p);
			break;
		}
		case CAULDRON_ITEM: {
			opGomi(p);
			break;
		}
		case STEP: {
			giveHalfBlocks(p, 1);
			break;
		}
		case PAPER: {
			ccm.openChatChannelMenu(p);
			break;
		}
		default : break;
		}
	}





	//コマンド類の動作

	public void opCraft(Player p) {
		p.openWorkbench(null, true);
	}

	public void opGomi(Player p) {
		p.openInventory(plugin.getServer().createInventory(null, 36 , ChatColor.BLUE + "ゴミ箱"));
		p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1F, 1F);
	}

	public void opLobby(Player p) {
		p.teleport(plugin.getWorldUtil().getLobbyWorldSpawn());
		p.sendMessage(prefix + "ロビーにテレポートしました。");
	}

	public void opSpawn(Player p , String[] args) {
		if (args == null || args.length == 0) {
			p.teleport(worldutil.getWorldSpawn(p.getWorld()));
			p.sendMessage(prefix + "スポーンへテレポートしました。");
			return;
		} else {
			World world = plugin.getServer().getWorld(args[0]);
			if (world != null) {
				p.teleport(worldutil.getWorldSpawn(world));
				p.sendMessage(prefix + "ワールド : " + args[0] + " のスポーンへテレポートしました。");
				return;
			} else {
				p.teleport(worldutil.getWorldSpawn(p.getWorld()));
				p.sendMessage(prefix + "ワールド : " + args[0] + " が見つからないため、現在のワールドのスポーンへテレポートしました。");
				return;
			}
		}
	}

	public void opManaita(final Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				String[] msg = {
						prefix + "-----まないたサーバー : " + p.getName() + " -----",
						prefix + "現在のワールド : " + p.getWorld().getName(),
						prefix + "ランク : " + plugin.getRankManager().getRank(p),
						prefix + "削ったブロック数 : " + plugin.getBreakBlockManager().getBlockNum(p),
						prefix + "ポイント : " + plugin.getPointAPI().getPoint(p) + " Chell",
						prefix + "総破壊ブロック個数 : " + plugin.getBreakBlockManager().getAllBreakBlock() + " ブロック",
						prefix + "------------------------"
				};
				p.sendMessage(msg);
			}
		}.runTaskAsynchronously(plugin);
	}

	public void opManaitahelp(Player p) {
		p.sendMessage(plugin.getServerHelp());
	}

	public void opRank(final CommandSender sender) {
		new BukkitRunnable() {
			@Override
			public void run() {
				List<Entry<String, Integer>> list = bbm.getRankTop();
				if (list == null) {
					sender.sendMessage(prefix + "取得に失敗しました。");
					return;
				}
				boolean namecontains = false;
				int allbreakblocks = bbm.getAllBreakBlock();
				sender.sendMessage(ChatColor.GOLD + "----------|" + ChatColor.GREEN + " ブロック採掘数トップ10 " + ChatColor.GOLD + "|----------");
				String pre = "  ";
				int count = 0;
				for (Map.Entry<String, Integer> map : list) {
					count++;
					sender.sendMessage(pre + ChatColor.GOLD + count + ChatColor.WHITE + " 位 " + ChatColor.AQUA
							+ map.getKey() + " " + ChatColor.GREEN + map.getValue() + ChatColor.WHITE + " ブロック ("
							+ ChatColor.YELLOW + getPercent(map.getValue() , allbreakblocks) + "%" + ChatColor.RESET + ")");
					if (sender.getName().equalsIgnoreCase(map.getKey())) {
						namecontains = true;
					}
				}
				sender.sendMessage(ChatColor.GOLD + "----------------------------------");
				if (sender instanceof Player) {
					Player p = (Player)sender;
					if (namecontains) {
						return;
					}
					int[] rank = bbm.getBlockRank(p);
					if (rank == null) {
						return;
					}
					sender.sendMessage(pre + ChatColor.GOLD + rank[1] + ChatColor.WHITE + " 位 " + ChatColor.AQUA
							+ p.getName() + " " + ChatColor.GREEN + rank[0] + ChatColor.WHITE + " ブロック ("
							+ ChatColor.YELLOW + getPercent(rank[0] , allbreakblocks) + "%" + ChatColor.RESET + ")");
					sender.sendMessage(ChatColor.GOLD + "----------------------------------");
				}
			}
		}.runTaskAsynchronously(plugin);
	}
	private String getPercent(int playerbreak , int allbreak) {
		BigDecimal pbreak = new BigDecimal(playerbreak);
		BigDecimal abreak = new BigDecimal(allbreak);
		BigDecimal hundred = new BigDecimal(100);
		BigDecimal res = (pbreak.multiply(hundred)).divide(abreak, 2, BigDecimal.ROUND_HALF_UP);
		return res.toString();
	}

	public void opRule(Player p) {
		p.sendMessage(prefix + "ルールはwikiを参照してください。");
		p.sendMessage(prefix + "http://wiki.mchel.net");
	}

	public void opShop(Player p) {
		shopm.openShopInventory(p);
	}

	public void opHome(Player p , String[] args) {
		if (args == null || args.length == 0) {
			Location loc = plugin.getHome().getHomeLocation(p);
			if (loc != null) {
				p.teleport(loc);
			} else {
				p.sendMessage(prefix + "ワールド: " + p.getWorld().getName() + " でホームが設定されていません。");
				return;
			}
		} else {
			Location loc = plugin.getHome().getHomeLocation(p, args[0]);
			if (loc != null) {
				p.teleport(loc);
			} else {
				p.sendMessage(prefix + "ワールド: " + args[0] + " でホームが設定されていません。");
				return;
			}
		}
	}

	public void opSethome(Player p) {
		boolean result = plugin.getHome().setHomeLocation(p);
		if (result) {
			p.sendMessage(prefix + "現在位置をホームに設定しました。");
			return;
		} else {
			p.sendMessage(prefix + "ホームを設定できませんでした。");
			return;
		}
	}

	public void giveHalfBlocks(Player p , int stack) {
		for (int i = 0 ; i < stack ; i++) {
			p.getInventory().addItem(new ItemStack(Material.STEP , 64));
		}
	}

	public void opChatChannel(Player p) {
		plugin.getChatChannelManager().openChatChannelMenu(p);
	}




}
