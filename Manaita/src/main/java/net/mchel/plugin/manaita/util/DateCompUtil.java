package net.mchel.plugin.manaita.util;

import java.util.Calendar;

import net.mchel.plugin.manaita.Manaita;

import org.bukkit.scheduler.BukkitRunnable;

public class DateCompUtil {

	private Manaita plugin;
	public DateCompUtil(Manaita manaita) {
		this.plugin = manaita;
		initial();
	}

	private void initial() {
		//ちぇる曜日
		new BukkitRunnable() {
			@Override
			public void run() {
				Calendar cal = Calendar.getInstance();
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					plugin.setChellYoubi(true);
				} else {
					plugin.setChellYoubi(false);
				}
			}
		}.runTaskTimer(plugin, 10, 1200);

		//タイムセール処理



	}

}
