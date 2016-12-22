package net.mchel.plugin.manaita.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.mchel.plugin.pointapi.PointAPI;

/**
 * @author chelcy
 */
public abstract class ShopItem {

	/**
	 * アイテム名
	 * @return
	 */
	public abstract String getItemName();

	/**
	 * アイテムの説明
	 * @return
	 */
	public abstract String getItemDescription();

	/**
	 * コスト(Chell)
	 * @return
	 */
	public abstract int getCost();

	/**
	 * アイテムスタック
	 * @return
	 */
	public abstract ItemStack getItemStack();

	/**
	 * マテリアル
	 * @return
	 */
	public Material getItemMaterial() {
		return getItemStack().getType();
	}

	/**
	 * コスト表記を追加
	 * @param is ItemStack
	 * @param canbuy 購入可能かどうか
	 * @param message 購入できますメッセを入れるか。
	 * @return ItemStack
	 */
	protected ItemStack setItemLore(boolean canbuy , boolean message) {
		ItemStack is = getItemStack().clone();
		int cost = getCost();
		is = is.clone();
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.addAll(im.getLore());
		if (canbuy) {
			lore.add(0, ChatColor.BLUE + "Cost : " + ChatColor.LIGHT_PURPLE + String.valueOf(cost) + ChatColor.BLUE + " Chell");
			if (message) {
				lore.add(ChatColor.YELLOW + "購入できます! クリックして購入");
			}
		} else {
			lore.add(0, ChatColor.RED + "購入できません。 Cost : " + ChatColor.DARK_PURPLE + String.valueOf(cost) + ChatColor.BLUE + " Chell");
		}
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	@SuppressWarnings("deprecation")
	public void apply(PointAPI pointapi ,Player p , String prefix) {
		int cost = this.getCost();
		if (pointapi.removePoint(p, cost, "[Manaita]Buy item from shop : " + this.getClass().getSimpleName())) {
			int emptyslot = p.getInventory().firstEmpty();
			if (emptyslot < 0) {
				p.getLocation().getWorld().dropItem(p.getLocation(), this.getItemStack());
			} else {
				p.getInventory().addItem(this.getItemStack());
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
