package net.mchel.plugin.manaita.manager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.mchel.plugin.manaita.Manaita;

import org.bukkit.configuration.file.YamlConfiguration;

public class Conf {

	private final Manaita plugin;
	private final File pluginFileFolder;
	private final File configFile;
	private final YamlConfiguration config;

	public Conf(Manaita pointAPI) {
		this.plugin = pointAPI;
		this.pluginFileFolder = plugin.getDataFolder();
		this.configFile = new File(pluginFileFolder + File.separator + "config.yml");
		init();
		this.config = YamlConfiguration.loadConfiguration(configFile);
	}

	private void init() {
		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
			}
		} catch (IOException ex) {
		}
	}

	public File getConfigFile() {
		return configFile;
	}

	public YamlConfiguration getConfig() {
		return config;
	}

	public Object get(String path, Object value) {
		return config.get(path, value);
	}

	public void set(String path, Object value) {
		config.set(path, value);
		saveConfig();
	}

	public String getString(String str) {
		return config.getString(str);
	}

	public double getDouble(String str) {
		return config.getDouble(str);
	}

	public int getInt(String str, int def) {
		return config.getInt(str, def);
	}

	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}

	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException ex) {
		}
	}

}
