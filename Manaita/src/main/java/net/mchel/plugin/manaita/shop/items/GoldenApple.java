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
public class GoldenApple extends ShopItem {

	private ItemStack is;
	public GoldenApple() {
		is = new ItemStack(Material.GOLDEN_APPLE , 1 , (byte)1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "すごい金リンゴ");
		im.addEnchant(Enchantment.KNOCKBACK, 2, true);
		List<String> lore = new ArrayList<String>() {{
			add(ChatColor.RESET + "" + ChatColor.YELLOW + "いや普通の金リンゴっぽいから");
		}};
		im.setLore(lore);
		is.setItemMeta(im);
	}

	@Override
	public String getItemName() {
		return "すごい金リンゴ";
	}

	@Override
	public String getItemDescription() {
		return "金リンゴ";
	}

	@Override
	public int getCost() {
		return 10;
	}

	@Override
	public ItemStack getItemStack() {
		return is;
	}

}
