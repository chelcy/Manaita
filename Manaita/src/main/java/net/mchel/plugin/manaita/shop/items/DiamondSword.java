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
public class DiamondSword extends ShopItem {

	private ItemStack is = null;
	public DiamondSword() {
		is = new ItemStack(Material.DIAMOND_SWORD , 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "すごい剣");
		im.addEnchant(Enchantment.DAMAGE_ARTHROPODS, 10, true);
		im.addEnchant(Enchantment.DURABILITY, 5, true);
		im.addEnchant(Enchantment.KNOCKBACK, 5, true);
		im.addEnchant(Enchantment.FIRE_ASPECT, 5, true);
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "つおい");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "すごい剣";
	}

	@Override
	public String getItemDescription() {
		return "ダメージ増加10 耐久力5 ノクバ5 火属性5";
	}

	@Override
	public int getCost() {
		return 40;
	}

	@Override
	public ItemStack getItemStack() {
		return is;
	}



}
