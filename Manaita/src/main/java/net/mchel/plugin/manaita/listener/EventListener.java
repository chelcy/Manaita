package net.mchel.plugin.manaita.listener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.manager.RankManager;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class EventListener implements Listener{

	private Manaita plugin;
	private String prefix;
	private RankManager rankm;
	public EventListener(Manaita manaita) {
		this.plugin = manaita;
		this.prefix = plugin.getPrefix();
		this.rankm = plugin.getRankManager();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreak(final BlockBreakEvent e) {
		final Player p = e.getPlayer();
		final int blockid = e.getBlock().getTypeId();
		if (blockid == 120 && !e.getPlayer().hasPermission("manaita.manage")) {
			e.setCancelled(true);
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				//ブロック統計処理
				if (checkBreakBlockID(blockid) && p.getGameMode() == GameMode.SURVIVAL) {
					//カウント追加
					Block b = e.getBlock();
					plugin.getBreakBlockManager().addBreakBlockNumber(p, b.getLocation().clone() , b.getTypeId() , b.getData());
				}
				//警告(上にブロックがあった時)
				if (!p.hasPermission("manaita.manage")
						&& p.getGameMode() == GameMode.SURVIVAL
						&& existUpBlock(p ,e.getBlock())) {
					p.sendMessage(ChatColor.RED + " " + ChatColor.BOLD + "警告 : " + ChatColor.GOLD
							+ "頭上に数ブロック存在します。"
							+ "下から掘る行為はビーコン設置時など以外には認められていません。"
							+ "再度ルールを確認してください。"
							+ "(ルールに反した場合BANされることがあります。)");
				}
			}
		}.runTaskAsynchronously(plugin);
	}

	private List<Integer> blockidlist = new ArrayList<Integer>() {{
		add(6);
		add(31);
		add(32);
		add(37);
		add(38);
		add(39);
		add(40);
		add(50);
		add(51);
		add(55);
		add(59);
		add(75);
		add(76);
		add(77);
		add(83);
		add(93);
		add(94);
		add(104);
		add(105);
		add(115);
		add(131);
		add(132);
		add(141);
		add(142);
		add(149);
		add(150);
		add(165);
		add(170);
		add(171);
		add(175);
	}};
	private HashMap<Player , Integer> breakblocks = new HashMap<Player , Integer>();
	private boolean checkBreakBlockID(int num) {
		if (num <= 0 || num > 197) {
			return false;
		} else {
			if (blockidlist.contains(num)) {
				return false;
			} else {
				return true;
			}
		}
	}
	private boolean existUpBlock(Player p ,Block b) {
		int count = 0;
		for (int i = 0 ; i < 15 ; i++) {
			b = b.getRelative(BlockFace.UP);
			if (b.getType() != Material.AIR) {
				count = count + 1;
			}
		}
		if (count > 10) {
			if (breakblocks.containsKey(p)) {
				int blocks = breakblocks.get(p) + 1;
				if (blocks > 9) {
					return true;
				}
				breakblocks.put(p, blocks);
			} else {
				breakblocks.put(p, 1);
			}
			return false;
		} else {
			if (breakblocks.containsKey(p)) {
				breakblocks.remove(p);
			}
			return false;
		}
	}

	//ブロックが燃え尽きるのを防止
	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		e.setCancelled(true);
	}

	//かなどこ最終クリック
	private HashMap<Player , Block> clickedAnvil = new HashMap<Player , Block>();
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		//マグマ置くの禁止
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (!p.hasPermission("manaita.manage") && p.getGameMode() != GameMode.CREATIVE && p.getItemInHand().getType() == Material.LAVA_BUCKET) {
				e.setCancelled(true);
				p.sendMessage(prefix + "マグマを設置することは禁止されています。");
			}
		}
		//かなどこなおし
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ANVIL) {
			Block b = e.getClickedBlock();
			clickedAnvil.put(p, b);
			fixAnvil(b);
		}
		if (p.getGameMode() == GameMode.CREATIVE && p.hasPermission("manaita.manage")) {
			return;
		}
		//火打石使用禁止
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& e.getClickedBlock() != null
				&& p.getItemInHand().getType() == Material.FLINT_AND_STEEL) {
			e.setCancelled(true);
			p.sendMessage(plugin.getPrefix() + "火打石の使用は許可されていません。");
		}
	}
	//かなどこ修正
	@SuppressWarnings("deprecation")
	private void fixAnvil(Block b) {
		if (b.getType() != Material.ANVIL) {
			return;
		}
		int data = b.getData();
		switch (data) {
		case 4:
		case 8:{
			b.setData((byte) 0);
			break;
		}
		case 5:
		case 9:{
			b.setData((byte) 1);
			break;
		}
		case 6:
		case 10:{
			b.setData((byte) 2);
			break;
		}
		case 7:
		case 11:{
			b.setData((byte) 3);
			break;
		}
		}
	}

	//炎が燃え広がるのを防止
	@EventHandler
	public void onBlockSpread(BlockSpreadEvent e) {
		e.setCancelled(true);
	}

	//ブロックに着火するのを防止(OPかつクリエじゃないと着火不可)
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		if (e.getPlayer() == null) {
			e.setCancelled(true);
			return;
		}
		if (!e.getPlayer().hasPermission("manaita.manage") || e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}

	//ブロックから火が燃え広がるのを防止
	@EventHandler
	public void onCombustBlock(EntityCombustByBlockEvent e) {
		e.setCancelled(true);
	}

	//エンティティから火が燃え広がるのを防止
	@EventHandler
	public void onCombustEntity(EntityCombustByEntityEvent e) {
		e.setCancelled(true);
	}

	//空腹度を減らさない
	@EventHandler
	public void onFoodLevel(FoodLevelChangeEvent e) {
		e.setCancelled(true);
		Player p = (Player)e.getEntity();
		p.setFoodLevel(20);
	}

	//OP以外のゲームモードが変化したとき、キック
	@EventHandler
	public void onGamemodeChange(PlayerGameModeChangeEvent e) {
		if(!e.getPlayer().hasPermission("manaita.manage") && e.getNewGameMode() == GameMode.CREATIVE) {
			e.getPlayer().kickPlayer(plugin.getPrefix() + "Admin以外のゲームモード変更はできません。");
			e.setCancelled(true);
		}
	}

	//爆破による破壊をキャンセル
	@EventHandler
	public void onExplosion(EntityExplodeEvent e) {
		e.setCancelled(true);
	}

	private List<String> gmlist = new ArrayList<String>();
	//サーバーデフォコマンド無効化とgamemodeコマンドのキック
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onServerCmdProtect(PlayerCommandPreprocessEvent e) {
		if (!e.getPlayer().hasPermission("manaita.manage")) {
			String cmd = (e.getMessage().startsWith("/") ? e.getMessage().replaceFirst("/", "") : e.getMessage()).replace(" ", "*");
			if (cmd.startsWith("plugins*") || cmd.startsWith("pl*") || cmd.startsWith("bukkit:plugins*") ||
					cmd.startsWith("bukkit:pl*") || cmd.startsWith("?*") ||
					cmd.startsWith("bukkit:help*") || cmd.startsWith("bukkit:?*") || cmd.startsWith("ver*") ||
					cmd.startsWith("version*") || cmd.startsWith("bukkit:ver*") || cmd.startsWith("bukkit:version*") ||
					cmd.startsWith("about*") || cmd.startsWith("bukkit:about*")) {
				e.setCancelled(true);
			} else if (cmd.startsWith("gamemode*")) {
				e.setCancelled(true);
				if (gmlist.contains(e.getPlayer().getName())) {
					e.getPlayer().kickPlayer(ChatColor.RED + "You temporary use admin command.");
					e.getPlayer().setBanned(true);
					Bukkit.getBanList(Type.NAME).getBanEntry(e.getPlayer().getName()).setReason("Use of frequent administrator command");
				} else {
					e.getPlayer().kickPlayer(ChatColor.RED + "DO NOT USE ADMIN COMMAND. Next is BAN.");
					gmlist.add(e.getPlayer().getName());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandLog(PlayerCommandPreprocessEvent e) {
		if (e.getMessage().startsWith("/")) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.hasPermission("manaita.manage")) {
					p.sendMessage(ChatColor.GREEN + "*" + ChatColor.GRAY + "[Log] " + e.getPlayer().getName()
							+ " : " + e.getMessage());
				}
			}
		}
	}

	//リスポーンスキップ
	@EventHandler
	public void respawnSkip(PlayerDeathEvent e) {
		final Player p = e.getEntity();
		e.setDeathMessage(e.getDeathMessage().replace(p.getName(), rankm.getRankPrefix(p) + ChatColor.RESET + p.getName()));
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				PacketPlayInClientCommand in = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
				EntityPlayer cPlayer = ((CraftPlayer)p).getHandle();
				cPlayer.playerConnection.a(in);
			}
		}, 1L);
	}

	//ログイン時
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if (!plugin.getAvailable() && e.getPlayer().hasPermission("manaita.manage")) {
			e.disallow(null, "接続できませんでした。Reason: サーバーで障害が発生しています。");
			return;
		}
	}
	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		plugin.getJoinLeftManager().onJoin(e.getPlayer());
		if (!nodamagep.contains(e.getPlayer())) {
			nodamagep.add(e.getPlayer());
		}
		e.getPlayer().setMaxHealth(40);
		if (plugin.getChellYoubi()) {
			e.getPlayer().sendMessage(prefix + "今日はちぇる曜日です!ポイントが2倍になります!");
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (nodamagep.contains(e.getPlayer())) {
					nodamagep.remove(e.getPlayer());
				}
			}
		}, 300L);
		if (!e.getPlayer().hasPlayedBefore()) {
			plugin.getJoinLeftManager().onFirstJoin(e.getPlayer());
			e.setJoinMessage(ChatColor.DARK_GREEN + " >" + ChatColor.GREEN + "> " + ChatColor.RESET
					+ e.getPlayer().getName() + ChatColor.YELLOW + " joined the game." + ChatColor.GREEN + " (first time)");
			Bukkit.getLogger().info(e.getPlayer().getName() + " is first join.");
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					e.getPlayer().teleport(plugin.getWorldUtil().getLobbyWorldSpawn());
					e.getPlayer().sendMessage(plugin.getServerHelp());
					e.getPlayer().sendMessage(plugin.getPrefix() + "上記メッセージは/helpコマンドで再度表示することができます。");
					e.getPlayer().sendMessage(plugin.getPrefix() + "かならずルールを読んでからプレイしてください。ルールはwikiに記載してあります。");
				}
			}, 1L);
		} else {
			e.setJoinMessage(ChatColor.DARK_GREEN + " >" + ChatColor.GREEN + "> " + ChatColor.RESET
					+ e.getPlayer().getName() + ChatColor.YELLOW + " joined the game.");
		}
	}

	//ログアウト時
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (!plugin.getAvailable()) {
			plugin.getMyLogger().info("プラグイン無効");
		} else {
			plugin.getJoinLeftManager().onLeft(e.getPlayer());
			e.setQuitMessage(ChatColor.DARK_GREEN + " >" + ChatColor.GREEN + "> " + ChatColor.RESET
					+ e.getPlayer().getName() + ChatColor.YELLOW + " left the game.");
		}
	}

	//エンダーマンがブロック改変した時
	@EventHandler
	public void onEndermanBlock(EntityChangeBlockEvent e) {
		if (e.getEntityType() == EntityType.ENDERMAN) {
			e.setCancelled(true);
		}
	}

	//ネザゲの作成をキャンセル
	@EventHandler
	public void createPortal(EntityCreatePortalEvent e) {
		if (e.getEntityType() != EntityType.PLAYER) {
			return;
		}
		if (e.getPortalType() != PortalType.NETHER) {
			return;
		}
		Player p = (Player)e.getEntity();
		if (!p.hasPermission("manaita.manage") || p.getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}

	private List<Player> nodamagep = new ArrayList<Player>();
	//ダメージ軽減処理
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity().getType() == EntityType.PLAYER) {
			if (nodamagep.contains((Player)e.getEntity())) {
				e.setDamage(0);
				e.setCancelled(true);
				return;
			}
			if (e.getCause() == DamageCause.FALL) {
				BigDecimal damage = new BigDecimal(e.getDamage());
				BigDecimal bd = new BigDecimal(0.5);
				e.setDamage(damage.multiply(bd).doubleValue());
			}
		}
	}

	//クラフト禁止
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		Material type = e.getCurrentItem().getType();
		if (type == Material.TNT || type == Material.FLINT_AND_STEEL) {
			e.setCancelled(true);
		} else if (type == Material.WORKBENCH) {
			e.setCancelled(true);
			plugin.getMenuManager().opCraft((Player)e.getWhoClicked());;
			e.getWhoClicked().sendMessage(prefix + "クラフトは /craft で行ってください。");
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		String name = e.getInventory().getName();
		//メニュー
		if (ChatColor.stripColor(name).contains("メニュー/Menu")) {
			plugin.getMenuManager().invClickEvent(e);
		}
		//かなどこ
		if (e.getInventory().getType() == InventoryType.ANVIL && clickedAnvil.containsKey(e.getWhoClicked())) {
			fixAnvil(clickedAnvil.get(e.getWhoClicked()));
		}
	}

	//インベントリ閉じた時に音
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player)e.getPlayer();
		if (ChatColor.stripColor(e.getInventory().getName()).contains("ゴミ箱")) {
			p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1F, 1F);
		}
	}


	//アイテムの耐久減った時にメッセ
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {
		final Player p = e.getPlayer();
		ItemStack is = e.getItem();
		int remainbef = is.getType().getMaxDurability() - is.getDurability();
		int remainaft = remainbef - e.getDamage();
		if (e.getDamage() > 0 && remainbef >= 30 && remainaft < 30) {
			p.sendMessage(prefix + ChatColor.YELLOW + "⚠⚠" + ChatColor.RESET + "アイテムの耐久が減っています！！" + ChatColor.YELLOW + "⚠⚠");
			p.playSound(p.getLocation(), Sound.NOTE_PLING, 1F, 1F);
			new BukkitRunnable() {
				@Override
				public void run() {
					p.playSound(p.getLocation(), Sound.NOTE_PLING, 1F, 0.5F);
				}
			}.runTaskLaterAsynchronously(plugin, 3L);
		}
	}







}
