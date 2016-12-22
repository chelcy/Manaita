package net.mchel.plugin.manaita.manager.sale;

import java.math.BigDecimal;

import org.bukkit.scheduler.BukkitRunnable;

import net.mchel.plugin.manaita.Manaita;

/**
 * @author chelcy
 */
public class LimitedTimeSaleManager {

	private Manaita plugin;
	private boolean salenow = false;
	private int salePercent = 0;
	public LimitedTimeSaleManager(Manaita plugin) {
		this.plugin = plugin;
		begin();
	}

	private void begin() {

		new BukkitRunnable() {
			@Override
			public void run() {
			}
		}.runTaskTimerAsynchronously(plugin, 20, 1200);
	}

	public boolean isSale() {
		return salenow;
	}

	public int convertPrice(int price) {
		if (salenow) {
			return price;
		}
		BigDecimal cost = new BigDecimal(price);
		BigDecimal ne = new BigDecimal(100 - salePercent);
		BigDecimal hun = new BigDecimal(100);
		return cost.multiply(ne).divide(hun).setScale(0 , BigDecimal.ROUND_UP).intValue();
	}




}
