package net.mchel.plugin.manaita.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mchel.plugin.manaita.Manaita;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TellrawAPI {

	@SuppressWarnings("unused")
	private Manaita plugin;
	public TellrawAPI(Manaita manaita) {
		this.plugin = manaita;
	}

	public  enum JSONParam {
		BOLD,
		ITALIC,
		UNDERLINED,
		STRIKETHROUGH,
		OBFUSCATED;
	}

	public enum JSONClickEvent {
		RUN_COMMAND,
		SUGGEST_COMMAND,
		OPEN_URL,
		CHANGE_PAGE
	}

	public class JSONPart {
		ChatColor color;
		String string;
		boolean bold = false;
		boolean italic = false;
		boolean underlined = false;
		boolean strikethrough = false;
		boolean obfuscated = false;
		String clickevent = "null";
		String clickeventvalue = "null";
		String hoverevent = "null";
		String hovereventvalue = "null";

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
					+ ",strikethrough:" + strikethrough + ",obfuscated:" + obfuscated + ",clickEvent:{action:'" + clickevent + "',value:'" + clickeventvalue + "'}"
					+ ",hoverEvent:{action:'" + hoverevent + "',value:" + hovereventvalue + "}"+ "}";
		}
		public String __INVALID__getJSONPartExtra(){
			return "{text:'" + string + "',color:'" + color.name().toLowerCase() + "',bold:" + bold + ",italic:" + italic + ",underlined:" + underlined
					+ ",strikethrough:" + strikethrough + ",obfuscated:" + obfuscated + ",clickEvent:{action:'" + clickevent + "',value:'" + clickeventvalue + "'}"
					+ ",hoverEvent:{action:'" + hoverevent + "',value:" + hovereventvalue + "}" + ",extra:[";
		}
		public boolean isValid(){
			return (string != null && color != null);
		}
		//字体パラメーター処理
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
		//クリック処理
		public JSONPart setClickEvent(JSONClickEvent events , String value) {
			if (value != null) {
				clickeventvalue = value;
			}
			if (events == JSONClickEvent.RUN_COMMAND) {
				clickevent = "run_command";
			} else if (events == JSONClickEvent.SUGGEST_COMMAND) {
				clickevent = "suggest_command";
			} else if (events == JSONClickEvent.OPEN_URL) {
				clickevent = "open_url";
			} else if (events == JSONClickEvent.CHANGE_PAGE) {
				clickevent = "change_page";
			}
			return this;
		}
		//ホバー処理
		public JSONPart setHoverEvent(List<JSONPart> list) {
			hoverevent = "show_text";
			if (list != null) {
				hovereventvalue = JSONString(list);
			}
			return this;
		}
	}

	//ListをString化
	/**
	 * LinkedListをJSONのStringに変換します。
	 *
	 * @param list JSONPartのListを指定
	 */
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

	//Tellrawの送るやつ
	/**
	 * Tellrawを送信します。
	 *
	 * @param player プレイヤーを指定
	 * @param JSONmsg JSONPartのLinkedListをJSONStringでStringに変換したものを指定
	 */
	public void sendTellrawMessage(Player player , String JSONmsg)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException{

		Field playerConnection = getField("EntityPlayer", PackageType.MINECRAFT_SERVER, false, "playerConnection");
		Constructor<?> packetConstructor = getConstructor(PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutChat"));
		Method getIChatBaseComponent = getMethod("IChatBaseComponent$ChatSerializer", PackageType.MINECRAFT_SERVER, "a", String.class);
		Method getHandle = getMethod("CraftPlayer", PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
		Method sendPacket = getMethod(playerConnection.getType(), "sendPacket", PackageType.MINECRAFT_SERVER.getClass("Packet"));

		Class<?> IChatBaseComponent_class = PackageType.MINECRAFT_SERVER.getClass("IChatBaseComponent");

		Object JSONmsg_component = null;
		if (JSONmsg != null) {
			JSONmsg_component = getIChatBaseComponent.invoke(IChatBaseComponent_class, JSONmsg);
		} else {
			JSONmsg_component = getIChatBaseComponent.invoke(IChatBaseComponent_class, "{text:''}");
		}
		sendPacket.invoke(playerConnection.get(getHandle.invoke(player)), instancePacketTellraw(packetConstructor, JSONmsg_component));
	}

	//Tellrawインスタンスパケット
	private Object instancePacketTellraw(Constructor<?> packetConstructor, Object a)
			throws InstantiationException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException{
		Object packet = null;
		packet = packetConstructor.newInstance();

		setValue(packet, true, "a", a);
		return packet;
	}

	//簡略送るやつ
	/**
	 * Tellrawを送信します。
	 *
	 * @param player プレイヤーを指定
	 * @param JSONPartList JSONPartのLinkedListを指定
	 */
	public void sendTellraw(Player player ,List<JSONPart> JSONPartList) {
		try {
			sendTellrawMessage(player, JSONString(JSONPartList));
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

		private final static Map<Class<?>, DataType> CLASS_MAP = new HashMap<Class<?>, DataType>();
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
}
