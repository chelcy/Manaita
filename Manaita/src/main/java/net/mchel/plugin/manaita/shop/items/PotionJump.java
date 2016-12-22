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
public class PotionJump extends ShopItem {

	private ItemStack is;
	public PotionJump() {
		is = new ItemStack(Material.POTION , 1);
		Potion po = new Potion(PotionType.JUMP);
		po.setLevel(1);
		po.apply(is);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "あぁ＾～心がぴょんぴょんするんじゃ＾～");
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "めっちゃ跳ねるよ！");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "あぁ＾～心がぴょんぴょんするんじゃ＾～";
	}

	@Override
	public String getItemDescription() {
		return "跳躍力上昇ポーション 3:00 lv1";
	}

	@Override
	public int getCost() {
		return 15;
	}

	@Override
	public ItemStack getItemStack() {
		return is;
	}

}
