package net.mchel.plugin.manaita.shop.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import net.mchel.plugin.manaita.shop.ShopItem;

/**
 * @author chelcy
 */
public class PotionSpeed extends ShopItem {

	private ItemStack is;
	public PotionSpeed() {
		is = new ItemStack(Material.POTION , 1);
		Potion po = new Potion(PotionType.SPEED);
		po.setLevel(1);
		po.apply(is);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "ﾌﾞｰﾝ(((っ･ω･)っ");
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "はやくはしれちゃうよ(ちゝω・´★)ぇ");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "ﾌﾞｰﾝ(((っ･ω･)っ";
	}

	@Override
	public String getItemDescription() {
		return "スピードポーション 3:00 lv1";
	}

	@Override
	public int getCost() {
		return 5;
	}

	@Override
	public ItemStack getItemStack() {
		return is;
	}

}
