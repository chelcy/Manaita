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
public class Bow extends ShopItem {

	private ItemStack is = null;
	public Bow() {
		is = new ItemStack(Material.BOW , 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "すごい弓");
		im.addEnchant(Enchantment.ARROW_DAMAGE, 10, true);
		im.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		im.addEnchant(Enchantment.FIRE_ASPECT, 5, true);
		im.addEnchant(Enchantment.DURABILITY, 10, true);
		im.addEnchant(Enchantment.ARROW_KNOCKBACK, 5, true);
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "射撃の名手");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "すごい弓";
	}

	@Override
	public String getItemDescription() {
		return "射撃ダメ増加10 無限 炎5 耐久10 パンチ5";
	}

	@Override
	public int getCost() {
		return 60;
	}

	@Override
	public ItemStack getItemStack() {
		return is;
	}



}
