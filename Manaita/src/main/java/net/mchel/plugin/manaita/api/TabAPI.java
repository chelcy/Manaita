package net.mchel.plugin.manaita.api;

import java.lang.reflect.Field;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.util.MyLogger;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TabAPI{

	private String headers = "&6#&bC&3helcy &bN&3etwork&6#\n&r&e%PLAYER% &cWelcome!!";
	private String footers = "&aMANAITA.mchel.net";

	private Manaita plugin;
	private MyLogger logger;
	public TabAPI(Manaita manaita) {
		this.plugin = manaita;
		this.logger = plugin.getMyLogger();
	}

	public void sendTabListAll() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendTabList(p);
		}
	}

	public void sendTabList(Player p) {
		String header = ChatColor.translateAlternateColorCodes('&', headers);
		String footer = ChatColor.translateAlternateColorCodes('&', footers);
		header = header.replace("%PLAYER%", p.getName());
		PlayerConnection con = ((CraftPlayer)p).getHandle().playerConnection;
		IChatBaseComponent tabheader = ChatSerializer.a("{\"text\": \"" + header + "\"}");
		IChatBaseComponent tabfooter = ChatSerializer.a("{\"text\": \"" + footer + "\"}");
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(tabheader);
		try {
			Field f = packet.getClass().getDeclaredField("b");
			f.setAccessible(true);
			f.set(packet, tabfooter);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			con.sendPacket(packet);
		}

	}

}
