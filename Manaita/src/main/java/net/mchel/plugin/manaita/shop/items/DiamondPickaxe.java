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
public class DiamondPickaxe extends ShopItem {

	private ItemStack is = null;
	public DiamondPickaxe() {
		is = new ItemStack(Material.DIAMOND_PICKAXE , 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "すごいぴっける");
		im.addEnchant(Enchantment.KNOCKBACK, 10, true);
		im.addEnchant(Enchantment.DIG_SPEED, 10, true);
		im.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "しゅごい");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "すごいピッケル";
	}

	@Override
	public String getItemDescription() {
		return "効率10 シルク ノックバック10";
	}

	@Override
	public int getCost() {
		return 20;
	}

	@Override
	public ItemStack getItemStack() {
		return is;
	}



}
