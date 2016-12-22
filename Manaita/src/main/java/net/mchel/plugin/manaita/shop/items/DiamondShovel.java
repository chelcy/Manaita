package net.mchel.plugin.manaita.shop.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.mchel.plugin.manaita.shop.ShopItem;

/**
 * @author chelcy
 */
public class DiamondShovel extends ShopItem {

	private ItemStack is = null;
	public DiamondShovel() {
		is = new ItemStack(Material.DIAMOND_SPADE , 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "すごいしゃべる");
		im.addEnchant(Enchantment.DIG_SPEED, 5, true);
		im.addEnchant(Enchantment.DURABILITY, 10, true);
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "しゅごいしゅごい");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "すごいしゃべる";
	}

	@Override
	public String getItemDescription() {
		return "効率5 耐久10";
	}

	@Override
	public int getCost() {
		return 30;
	}

	@Override
	public ItemStack getItemStack() {
		return is;
	}



}
