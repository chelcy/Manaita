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
public class DiamondBoots extends ShopItem {

	private ItemStack is = null;
	public DiamondBoots() {
		is = new ItemStack(Material.DIAMOND_BOOTS , 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "足をアシスト(笑)");
		im.addEnchant(Enchantment.PROTECTION_FIRE, 10, true);
		im.addEnchant(Enchantment.DURABILITY, 5, true);
		im.addEnchant(Enchantment.PROTECTION_FALL, 10, true);
		im.addEnchant(Enchantment.WATER_WORKER, 3, true);
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "足は大事だよね・・・？");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "足をアシスト(笑)";
	}

	@Override
	public String getItemDescription() {
		return "火炎耐性10 耐久5 落下耐性10 水中歩行3";
	}

	@Override
	public int getCost() {
		return 50;
	}

	@Override
	public ItemStack getItemStack() {
		return is;
	}



}
