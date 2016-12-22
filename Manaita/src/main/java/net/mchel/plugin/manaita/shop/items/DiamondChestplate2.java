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
public class DiamondChestplate2 extends ShopItem {

	private ItemStack is = null;
	public DiamondChestplate2() {
		is = new ItemStack(Material.DIAMOND_CHESTPLATE , 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "鉄壁その2");
		im.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 10, true);
		im.addEnchant(Enchantment.DURABILITY, 5, true);
		im.addEnchant(Enchantment.THORNS, 10, true);
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "守ってさしあげますわ♪");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "鉄壁その2";
	}

	@Override
	public String getItemDescription() {
		return "爆破耐性10 耐久5 とげの鎧10";
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
