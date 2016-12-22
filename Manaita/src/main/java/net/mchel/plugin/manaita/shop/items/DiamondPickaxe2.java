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
public class DiamondPickaxe2 extends ShopItem {

	private ItemStack is = null;
	public DiamondPickaxe2() {
		is = new ItemStack(Material.DIAMOND_PICKAXE , 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "もっとしゅごぃぴっける");
		im.addEnchant(Enchantment.KNOCKBACK, 10, true);
		im.addEnchant(Enchantment.DIG_SPEED, 10, true);
		im.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		im.addEnchant(Enchantment.DURABILITY, 5, true);
		im.addEnchant(Enchantment.FIRE_ASPECT, 10, true);
		//im.addEnchant(Enchantment.PROTECTION_FALL, 10, true);
		//im.addEnchant(Enchantment.WATER_WORKER, 10, true);
		//im.addEnchant(Enchantment.THORNS, 10, true);
		im.addEnchant(Enchantment.DAMAGE_UNDEAD, 10, true);
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "もっとしゅごぃよ！");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "もっとすごいピッケル";
	}

	@Override
	public String getItemDescription() {
		return "効率10 シルク 耐久5";
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
