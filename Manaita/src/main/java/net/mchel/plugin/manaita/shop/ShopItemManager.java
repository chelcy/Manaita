package net.mchel.plugin.manaita.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.shop.items.Bow;
import net.mchel.plugin.manaita.shop.items.DiamondBoots;
import net.mchel.plugin.manaita.shop.items.DiamondChestplate;
import net.mchel.plugin.manaita.shop.items.DiamondChestplate2;
import net.mchel.plugin.manaita.shop.items.DiamondPickaxe;
import net.mchel.plugin.manaita.shop.items.DiamondPickaxe2;
import net.mchel.plugin.manaita.shop.items.DiamondPickaxe3;
import net.mchel.plugin.manaita.shop.items.DiamondPickaxe4;
import net.mchel.plugin.manaita.shop.items.DiamondShovel;
import net.mchel.plugin.manaita.shop.items.DiamondSword;
import net.mchel.plugin.manaita.shop.items.DiamondSword2;
import net.mchel.plugin.manaita.shop.items.GoldenApple;
import net.mchel.plugin.manaita.shop.items.PotionJump;
import net.mchel.plugin.manaita.shop.items.PotionJump2;
import net.mchel.plugin.manaita.shop.items.PotionSpeed;
import net.mchel.plugin.manaita.shop.items.PotionSpeed2;
import net.mchel.plugin.pointapi.PointAPI;

/**
 * @author chelcy
 */
public class ShopItemManager implements Listener{

	private static ShopItemManager instance;
	private Manaita plugin;
	private PointAPI pointapi;
	private String prefix;

	private List<ShopItem> shopItemList = new ArrayList<ShopItem>();
	private HashMap<Player , Integer> haspoint = new HashMap<Player , Integer>();

	//コンストラクタ
	public ShopItemManager(Manaita manaita) {
		instance = this;
		this.plugin = manaita;
		this.pointapi = plugin.getPointAPI();
		this.prefix = plugin.getPrefix();
		manaita.getPluginManager().registerEvents(this, manaita);

		//アイテム追加
		registerShopItem(new Bow());
		registerShopItem(new DiamondBoots());
		registerShopItem(new DiamondChestplate());
		registerShopItem(new DiamondChestplate2());
		registerShopItem(new DiamondPickaxe());
		registerShopItem(new DiamondPickaxe2());
		registerShopItem(new DiamondPickaxe3());
		registerShopItem(new DiamondPickaxe4());
		registerShopItem(new DiamondShovel());
		registerShopItem(new DiamondSword());
		registerShopItem(new DiamondSword2());
		registerShopItem(new GoldenApple());
		registerShopItem(new PotionJump());
		registerShopItem(new PotionJump2());
		registerShopItem(new PotionSpeed());
		registerShopItem(new PotionSpeed2());
	}

	//インスタンス
	public static ShopItemManager getInstance() {
		return instance;
	}

	/**
	 * ショップへアイテム追加
	 * @param si ShopItem
	 * @return true:追加成功
	 */
	public boolean registerShopItem(ShopItem si) {
		if (shopItemList.contains(si)) {
			return false;
		}
		shopItemList.add(si);
		return true;
	}

	/**
	 * ショップアイテムのリストを返します
	 * @return アイテムリスト(ShopItem)
	 */
	public List<ShopItem> getShopItemList() {
		return shopItemList;
	}

	/**
	 * ショップインベントリを表示します。
	 * @param p
	 */
	public void openShopInventory(Player p) {
		int point = pointapi.getPoint(p);
		haspoint.put(p, point);
		Inventory inv = Bukkit.createInventory(null, 36 , ChatColor.BLUE + "Shop " + ChatColor.GOLD
				+ "所持ポイント: " + ChatColor.LIGHT_PURPLE + point + ChatColor.GOLD + " Chell");
		for (int i = 0 ; i < shopItemList.size() ; i++) {
			if (i >= 36) {
				break;
			}
			ShopItem si = shopItemList.get(i);
			boolean canbuy = point >= si.getCost();
			inv.setItem(i, si.setItemLore(canbuy , true));
		}
		p.openInventory(inv);
	}

	/**
	 * アイテム購入時の確認画面を表示します。
	 * @param p プレイヤー
	 */
	@SuppressWarnings("deprecation")
	private void openShopConfirmGui(Player p , ShopItem si) {

		Inventory inv = Bukkit.createInventory(null , 45 , ChatColor.BLUE + "Shop " + ChatColor.GREEN + "アイテム購入確認");
		//アイテムを配置
		inv.setItem(13, si.setItemLore(true, false));
		//yes
		ItemStack itemyes = new ItemStack(Material.WOOL , 1 , DyeColor.LIME.getData());
		ItemMeta metayes = itemyes.getItemMeta();
		metayes.setDisplayName(ChatColor.GREEN + "" + si.getCost() + " Chellでこのアイテムを購入する。");
		itemyes.setItemMeta(metayes);
		inv.setItem(30, itemyes);
		//no
		ItemStack itemno = new ItemStack(Material.WOOL , 1 , DyeColor.RED.getData());
		ItemMeta metano = itemno.getItemMeta();
		metano.setDisplayName(ChatColor.RED + "購入をキャンセルする。");
		itemno.setItemMeta(metano);
		inv.setItem(32, itemno);
		//ショップに戻る
		ItemStack returnshop = new ItemStack(Material.SLIME_BALL , 1);
		ItemMeta metashop = returnshop.getItemMeta();
		metashop.setDisplayName(ChatColor.YELLOW + "ショップに戻る。");
		returnshop.setItemMeta(metashop);
		inv.setItem(44, returnshop);

		p.openInventory(inv);
		buyConfirmItem.put(p, si);
	}
	//確認中アイテム
	private HashMap<Player , ShopItem> buyConfirmItem = new HashMap<Player , ShopItem>();
	//確認中アイテム削除
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent e) {
		if (buyConfirmItem.containsKey(e.getPlayer())) {
			buyConfirmItem.remove(e.getPlayer());
		}
	}

	/**
	 * ショップのインベントリをクリックした時の処理
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void clickInventoryEvent(InventoryClickEvent e) {
		String invname = ChatColor.stripColor(e.getInventory().getName());
		if (!invname.startsWith("Shop ")) {
			return;
		}
		if (e.getSlotType() != SlotType.CONTAINER) {
			return;
		}
		ItemStack is = e.getCurrentItem();
		e.setCancelled(true);
		if (is == null || is.getType() == Material.AIR) {
			return;
		}
		Player p = (Player)e.getWhoClicked();
		//一覧画面じゃない時(確認画面のとき) 確認後購入処理
		if (!invname.contains("Chell")) {
			if (!buyConfirmItem.containsKey(p)) {
				return;
			}
			if (is.getType() == Material.SLIME_BALL) {
				openShopInventory(p);
				return;
			}
			if (is.getType() != Material.WOOL) {
				return;
			}
			if (is.getData().getData() == DyeColor.LIME.getData()) {
				//購入処理
				buyShopItem(p , buyConfirmItem.get(p));
			} else if (is.getData().getData() == DyeColor.RED.getData()) {
				//キャンセル処理
				p.closeInventory();
				p.sendMessage(prefix + "購入をキャンセルしました。");
				return;
			}
			return;
		}
		int slot = e.getSlot();
		int point = haspoint.get(p);
		if (slot >= shopItemList.size()) {
			return;
		}
		ShopItem si = shopItemList.get(slot);
		if (p.getGameMode() == GameMode.CREATIVE && p.hasPermission("manaita.manage")) {
			p.getInventory().addItem(si.getItemStack());
			return;
		}
		int cost = si.getCost();
		if (cost < 0) {
			return;
		}
		if (point < cost) {
			p.sendMessage(prefix + "ポイントが不足しています。購入できません。");
			return;
		}
		//一覧画面から確認画面に移動
		openShopConfirmGui(p , si);
	}

	//購入時確認後
	@SuppressWarnings("deprecation")
	private void buyShopItem(Player p , ShopItem si) {
		int cost = si.getCost();
		if (pointapi.removePoint(p, cost, "[Manaita]Buy item from shop : " + si.getClass().getSimpleName())) {
			int emptyslot = p.getInventory().firstEmpty();
			if (emptyslot < 0) {
				p.getLocation().getWorld().dropItem(p.getLocation(), si.getItemStack());
			} else {
				p.getInventory().addItem(si.getItemStack());
			}
			p.updateInventory();
			p.sendMessage(prefix + "アイテムを購入しました。残りポイントが " + pointapi.getPoint(p) + " Chellになりました。");
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 2F, 1F);
			p.closeInventory();
		} else {
			p.sendMessage(prefix + "エラー : 購入できませんでした。");
			p.closeInventory();
			return;
		}
	}



}
