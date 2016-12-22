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
public class DiamondSword2 extends ShopItem {

	private ItemStack is = null;
	public DiamondSword2() {
		is = new ItemStack(Material.DIAMOND_SWORD , 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "すごい剣その2");
		im.addEnchant(Enchantment.DAMAGE_UNDEAD, 10, true);
		im.addEnchant(Enchantment.DURABILITY, 5, true);
		im.addEnchant(Enchantment.FIRE_ASPECT, 5, true);
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "つおいとおもう！");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "すごい剣その2";
	}

	@Override
	public String getItemDescription() {
		return "虫属性10 耐久5 火属性5";
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
