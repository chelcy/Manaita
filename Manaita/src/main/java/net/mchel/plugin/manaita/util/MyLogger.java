package net.mchel.plugin.manaita.util;

import net.mchel.plugin.manaita.Manaita;

public class MyLogger {

	private Manaita plugin;

	public MyLogger(Manaita manaita) {
		this.plugin = manaita;
	}

	public void info(String s) {
		plugin.getLogger().info( s);
	}

	public void warn(String s) {
		plugin.getLogger().warning(s);
	}

	public void error(Exception e) {
		plugin.getLogger().info(e.getLocalizedMessage());
	}

}
