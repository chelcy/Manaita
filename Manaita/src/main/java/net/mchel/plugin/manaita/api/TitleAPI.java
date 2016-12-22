package net.mchel.plugin.manaita.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.mchel.plugin.manaita.Manaita;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleAPI {

	@SuppressWarnings("unused")
	private Manaita plugin;
	public TitleAPI(Manaita manaita) {
		this.plugin = manaita;
	}

	public static enum JSONParam {
		BOLD,
		ITALIC,
		UNDERLINED,
		STRIKETHROUGH,
		OBFUSCATED;
	}

	public static class JSONPart {
		ChatColor color;
		String string;
		boolean bold = false;
		boolean italic = false;
		boolean underlined = false;
		boolean strikethrough = false;
		boolean obfuscated = false;

		public JSONPart(String string, ChatColor color){
			if(string == null){
				new NullPointerException("The string cannot be null !").printStackTrace();
				return;
			}
			this.string = string.replaceAll("'", "").replaceAll('"'+"", "");
			if(color != null){
				this.color = color;
			} else {
				color = ChatColor.WHITE;
			}
		}
		public String getString(){
			return string;
		}
		public ChatColor getColor(){
			return color;
		}
		public String getJSONPart(){
			return "{text:'" + string + "',color:'" + color.name().toLowerCase() + "',bold:" + bold + ",italic:" + italic + ",underlined:" + underlined
					+ ",strikethrough:" + strikethrough + ",obfuscated:" + obfuscated+ "}";
		}
		public String __INVALID__getJSONPartExtra(){
			return "{text:'" + string + "',color:'" + color.name().toLowerCase() + "',bold:" + bold + ",italic:" + italic + ",underlined:" + underlined
					+ ",strikethrough:" + strikethrough + ",obfuscated:" + obfuscated+ ",extra:[";
		}
		public boolean isValid(){
			return (string != null && color != null);
		}
		public JSONPart setParam(JSONParam... params){
			for(JSONParam param : params){
				if(param == JSONParam.BOLD){
					bold = true;
				} else if(param == JSONParam.ITALIC){
					italic = true;
				} else if(param == JSONParam.OBFUSCATED){
					obfuscated = true;
				} else if(param == JSONParam.STRIKETHROUGH){
					strikethrough = true;
				} else if(param == JSONParam.UNDERLINED){
					underlined = true;
				}
			}
			return this;
		}
	}

	public String JSONString(List<JSONPart> list){
		if(list == null){
			new NullPointerException("The list cannot be null !").printStackTrace();
			return null;
		}
		if(list.size() < 1){
			new IndexOutOfBoundsException("The must contains at least 1 element !").printStackTrace();
			return null;
		}
		if(list.size() > 1){
			String result = "";
			boolean first_done = false;
			for(int i = 0; i < list.size(); i++){
				JSONPart json_part = list.get(i);
				if(!first_done){
					result = json_part.__INVALID__getJSONPartExtra();
					first_done = true;
				} else {
					if(list.size() >= (i+2)){
						result = result + json_part.__INVALID__getJSONPartExtra();
					} else {
						result = result + json_part.getJSONPart();
						for(int end = 0; end < i; end++){
							result = result + "]}";
						}
						return result;
					}
				}
			}
		} else {
			return list.get(0).getJSONPart();
		}
		return null;
	}

	public void sendSubTitle(Player player, String JSONsubtitle, Integer fadeIn, Integer stay, Integer fadeOut)
			throws IllegalAccessException, IllegalArgumentException,
				InvocationTargetException, NoSuchFieldException,
				SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
		if(player != null && JSONsubtitle != null){
			sendTitle(player, null, JSONsubtitle, fadeIn, stay, fadeOut);
		}  else {
			new NullPointerException("The vars: 'player' and 'JSONsubtitle' musn't be null !").printStackTrace();
		}
	}

	public void sendTitle(Player player, String JSONtitle, Integer fadeIn, Integer stay, Integer fadeOut)
			throws IllegalAccessException, IllegalArgumentException,
				InvocationTargetException, NoSuchFieldException,
				SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
		if(player != null && JSONtitle != null){
			sendTitle(player, JSONtitle, null, fadeIn, stay, fadeOut);
		} else {
			new NullPointerException("The vars: 'player' and 'JSONtitle' musn't be null !").printStackTrace();
		}
	}

	public void sendTitleAndSubTitle(Player player, String JSONtitle, String JSONsubtitle, Integer fadeIn, Integer stay, Integer fadeOut)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
		if(player != null && JSONtitle != null && JSONsubtitle != null){
			sendTitle(player, JSONtitle, JSONsubtitle, fadeIn, stay, fadeOut);
		} else {
			new NullPointerException("The vars: 'player', 'JSONtitle' and 'JSONsubtitle' musn't be null !").printStackTrace();
		}
	}

	private void sendTitle(Player player, String JSONtitle, String JSONsubtitle, Integer fadeIn, Integer stay, Integer fadeOut)
			throws IllegalAccessException, IllegalArgumentException,
				InvocationTargetException, NoSuchFieldException,
				SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException {

		Field playerConnection = getField("EntityPlayer", PackageType.MINECRAFT_SERVER, false, "playerConnection");
		Constructor<?> packetConstructor = getConstructor(PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutTitle"));
		Method getIChatBaseComponent = getMethod("IChatBaseComponent$ChatSerializer", PackageType.MINECRAFT_SERVER, "a", String.class);
		Method getHandle = getMethod("CraftPlayer", PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
		Method sendPacket = getMethod(playerConnection.getType(), "sendPacket", PackageType.MINECRAFT_SERVER.getClass("Packet"));

		Class<?> enum_titleaction = PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutTitle$EnumTitleAction");
		Class<?> IChatBaseComponent_class = PackageType.MINECRAFT_SERVER.getClass("IChatBaseComponent");

		Object JSONsubtitle_component = null;
		Object JSONtitle_component = null;
		if(JSONtitle != null){
			JSONtitle_component = getIChatBaseComponent.invoke(IChatBaseComponent_class, JSONtitle);
		} else {
			JSONtitle_component = getIChatBaseComponent.invoke(IChatBaseComponent_class, "{text:''}");
		}
		if(JSONsubtitle != null){
			JSONsubtitle_component = getIChatBaseComponent.invoke(IChatBaseComponent_class, JSONsubtitle);
		}

		sendPacket.invoke(playerConnection.get(getHandle.invoke(player)), instancePacket(packetConstructor, enum_titleaction.getEnumConstants()[2], null, fadeIn, stay, fadeOut));
		sendPacket.invoke(playerConnection.get(getHandle.invoke(player)), instancePacket(packetConstructor, enum_titleaction.getEnumConstants()[0], JSONtitle_component, -1, -1, -1));
		if(JSONsubtitle != null){
			sendPacket.invoke(playerConnection.get(getHandle.invoke(player)), instancePacket(packetConstructor, enum_titleaction.getEnumConstants()[1], JSONsubtitle_component, -1, -1, -1));
		}
    }

	private Object instancePacket(Constructor<?> packetConstructor, Object a, Object b, Object c, Object d, Object e)
			throws InstantiationException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException{
		Object packet = null;
		packet = packetConstructor.newInstance();

		setValue(packet, true, "a", a);
		setValue(packet, true, "b", b);
		setValue(packet, true, "c", c);
		setValue(packet, true, "d", d);
		setValue(packet, true, "e", e);
		return packet;
	}

	public void sendTitle(Player p , HashMap<String , ChatColor> list , int fadein , int show , int fadeout) {
		List<JSONPart> jlist = new LinkedList<JSONPart>();
		for (Map.Entry<String , ChatColor> entry : list.entrySet()) {
			jlist.add(new JSONPart(entry.getKey() , entry.getValue()));
		}
		try {
			sendTitle(p, JSONString(jlist), fadein, show, fadeout);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException
				| SecurityException | ClassNotFoundException | NoSuchMethodException | InstantiationException e) {
			e.printStackTrace();
		}
	}

	public void sendSimpleTitle(Player p , String s , ChatColor c , int fadein , int show , int fadeout) {
		HashMap<String , ChatColor> list = new HashMap<>();
		list.put(s, c);
		sendTitle(p , list ,fadein , show , fadeout);
	}

	public void sendTitle(Player player ,List<JSONPart> JSONPartList , int fadein , int show , int fadeout) {
		try {
			sendTitle(player, JSONString(JSONPartList) , fadein , show , fadeout);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchFieldException
				| SecurityException | ClassNotFoundException
				| NoSuchMethodException | InstantiationException e) {
			e.printStackTrace();
		}
	}


	//Reflaction
	public Field getField(Class<?> clazz, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException {
		Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
		field.setAccessible(true);
		return field;
	}
	public Field getField(String className, PackageType packageType, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
		return getField(packageType.getClass(className), declared, fieldName);
	}
	public Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
		Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
		for (Constructor<?> constructor : clazz.getConstructors()) {
			if (!DataType.compare(DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes)) {
				continue;
			}
			return constructor;
		}
		throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
	}
	public Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
		Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
		for (Method method : clazz.getMethods()) {
			if (!method.getName().equals(methodName) || !DataType.compare(DataType.getPrimitive(method.getParameterTypes()), primitiveTypes)) {
				continue;
			}
			return method;
		}
		throw new NoSuchMethodException("There is no such method in this class with the specified name and parameter types");
	}
	public Method getMethod(String className, PackageType packageType, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
		return getMethod(packageType.getClass(className), methodName, parameterTypes);
	}
	public void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		getField(clazz, declared, fieldName).set(instance, value);
	}
	public void setValue(Object instance, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		setValue(instance, instance.getClass(), declared, fieldName, value);
	}
	public enum PackageType {
		MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()),
		CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion()),
		CRAFTBUKKIT_ENTITY(CRAFTBUKKIT, "entity");
		private final String path;
		private PackageType(String path) {
			this.path = path;
		}
		private PackageType(PackageType parent, String path) {
			this(parent + "." + path);
		}
		public Class<?> getClass(String className) throws ClassNotFoundException {
			return Class.forName(this + "." + className);
		}
		@Override
		public String toString() {
			return path;
		}
		public static String getServerVersion() {
			return Bukkit.getServer().getClass().getPackage().getName().substring(23);
		}
	}
	public enum DataType {
		BYTE(byte.class, Byte.class),
		SHORT(short.class, Short.class),
		INTEGER(int.class, Integer.class),
		LONG(long.class, Long.class),
		CHARACTER(char.class, Character.class),
		FLOAT(float.class, Float.class),
		DOUBLE(double.class, Double.class),
		BOOLEAN(boolean.class, Boolean.class);

		private static final Map<Class<?>, DataType> CLASS_MAP = new HashMap<Class<?>, DataType>();
		private final Class<?> primitive;
		private DataType(Class<?> primitive, Class<?> reference) {
			this.primitive = primitive;
		}
		public Class<?> getPrimitive() {
			return primitive;
		}
		public static DataType fromClass(Class<?> clazz) {
			return CLASS_MAP.get(clazz);
		}
		public static Class<?> getPrimitive(Class<?> clazz) {
			DataType type = fromClass(clazz);
			return type == null ? clazz : type.getPrimitive();
		}
		public static Class<?>[] getPrimitive(Class<?>[] classes) {
			int length = classes == null ? 0 : classes.length;
			Class<?>[] types = new Class<?>[length];
			for (int index = 0; index < length; index++) {
				types[index] = getPrimitive(classes[index]);
			}
			return types;
		}
		public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
			if (primary == null || secondary == null || primary.length != secondary.length) {
				return false;
			}
			for (int index = 0; index < primary.length; index++) {
				Class<?> primaryClass = primary[index];
				Class<?> secondaryClass = secondary[index];
				if (primaryClass.equals(secondaryClass) || primaryClass.isAssignableFrom(secondaryClass)) {
					continue;
				}
				return false;
			}
			return true;
		}
	}

	public void sendActionBarbf(Player p , String msg) {
		if (msg == null) msg = "";
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		msg = msg.replaceAll("%PLAYER%", p.getDisplayName());
		PlayerConnection con = ((CraftPlayer)p).getHandle().playerConnection;
		IChatBaseComponent chat = ChatSerializer.a("{\"text\": \"" + msg + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(chat , (byte)2);
		con.sendPacket(packet);
	}


	public void sendActionBar(Player player, String message){
		String nmsver = Bukkit.getServer().getClass().getPackage().getName();
		nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
		message = ChatColor.translateAlternateColorCodes('&', message);
		try {
			Class<?> CraftPlayer = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
			Object p = CraftPlayer.cast(player);
			Object ppoc = null;
			Class<?> PacketPlayOutChat = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
			Class<?> Packet = Class.forName("net.minecraft.server." + nmsver + ".Packet");
			if (nmsver.equalsIgnoreCase("v1_8_R1") || !nmsver.startsWith("v1_8_")) {
				Class<?> ChatSerializer = Class.forName("net.minecraft.server." + nmsver + ".ChatSerializer");
				Class<?> IChatBaseComponent = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
				Method m3 = ChatSerializer.getDeclaredMethod("a", new Class<?>[] {String.class});
				Object cbc = IChatBaseComponent.cast(m3.invoke(ChatSerializer, "{\"text\": \"" + message + "\"}"));
				ppoc = PacketPlayOutChat.getConstructor(new Class<?>[] {IChatBaseComponent, byte.class}).newInstance(new Object[] {cbc, (byte) 2});
			} else {
				Class<?> ChatComponentText = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
				Class<?> IChatBaseComponent = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
				Object o = ChatComponentText.getConstructor(new Class<?>[] {String.class}).newInstance(new Object[] {message});
				ppoc = PacketPlayOutChat.getConstructor(new Class<?>[] {IChatBaseComponent, byte.class}).newInstance(new Object[] {o, (byte) 2});
			}
			Method m1 = CraftPlayer.getDeclaredMethod("getHandle", new Class<?>[] {});
			Object h = m1.invoke(p);
			Field f1 = h.getClass().getDeclaredField("playerConnection");
			Object pc = f1.get(h);
			Method m5 = pc.getClass().getDeclaredMethod("sendPacket",new Class<?>[] {Packet});
			m5.invoke(pc, ppoc);
		} catch (Exception ex) {	}
	}
}


