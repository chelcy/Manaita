package net.mchel.plugin.manaita.shop.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.mchel.plugin.manaita.shop.ShopItem;

/**
 * @author chelcy
 */
public class PotionSpeed2 extends ShopItem {

	private ItemStack is;
	public PotionSpeed2() {
		is = new ItemStack(Material.POTION , 1 , (byte)8258);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "もっとﾌﾞｰﾝ(((⊂(  ･ω･)⊃");
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "長く早く走れるよ＞＜");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "もっとﾌﾞｰﾝ(((⊂(  ･ω･)⊃";
	}

	@Override
	public String getItemDescription() {
		return "スピードポーション 8:00 lv1";
	}

	@Override
	public int getCost() {
		return 10;
	}

	@Override
	public ItemStack getItemStack() {
		return is;
	}

}
