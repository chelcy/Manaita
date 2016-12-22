package net.mchel.plugin.manaita;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.mchel.plugin.manaita.api.TabAPI;
import net.mchel.plugin.manaita.api.TellrawAPI;
import net.mchel.plugin.manaita.api.TitleAPI;
import net.mchel.plugin.manaita.api.UidAPI;
import net.mchel.plugin.manaita.chat.ChatChannelManager;
import net.mchel.plugin.manaita.chat.ChatListener;
import net.mchel.plugin.manaita.cmd.Cmd_Channel;
import net.mchel.plugin.manaita.cmd.Cmds;
import net.mchel.plugin.manaita.listener.EventListener;
import net.mchel.plugin.manaita.listener.VotifierListener;
import net.mchel.plugin.manaita.manager.BreakBlockManager;
import net.mchel.plugin.manaita.manager.Conf;
import net.mchel.plugin.manaita.manager.JoinLeftManager;
import net.mchel.plugin.manaita.manager.MenuManager;
import net.mchel.plugin.manaita.manager.RankManager;
import net.mchel.plugin.manaita.manager.sale.LimitedTimeSaleManager;
import net.mchel.plugin.manaita.manager.statistics.StatisticsManager;
import net.mchel.plugin.manaita.shop.ShopItemManager;
import net.mchel.plugin.manaita.sql.MySQL;
import net.mchel.plugin.manaita.util.DateCompUtil;
import net.mchel.plugin.manaita.util.Home;
import net.mchel.plugin.manaita.util.JsonBuilder;
import net.mchel.plugin.manaita.util.MyLogger;
import net.mchel.plugin.manaita.util.NScore;
import net.mchel.plugin.manaita.util.PlayerID;
import net.mchel.plugin.manaita.util.Twit;
import net.mchel.plugin.manaita.util.WorldUtil;
import net.mchel.plugin.pointapi.PointAPI;

/**
 * @author chelcy
 */
public class Manaita extends JavaPlugin implements Listener{

	private final PluginManager pm = Bukkit.getPluginManager();

	private MySQL sql;
	private Conf config;
	private JsonBuilder jb;
	private BreakBlockManager bbm;
	private Home home;
	private PlayerID playerid;
	private WorldUtil worldutil;
	private DateCompUtil datecomp;
	private ChatChannelManager chatm;
	private RankManager rankmanager;
	private MenuManager menum;
	private JoinLeftManager join;
	private MyLogger logger;
	private NScore nscore;
	private VotifierListener votelistener;
	private LimitedTimeSaleManager sale;
	private ShopItemManager shop;
	private StatisticsManager stm;

	private TabAPI tabapi;
	private TellrawAPI tellrawapi;
	private TitleAPI titleapi;
	private UidAPI uidapi;

	private PointAPI pointapi;

	private boolean available , timesale = false , chellyoubi = false;

	private final String prefix = ChatColor.GREEN + "" + ChatColor.BOLD + " Manaita" + ChatColor.DARK_AQUA
			+ " >" + ChatColor.AQUA + "> " + ChatColor.RESET;

	@Override
	public void onEnable() {
		super.onEnable();

		try {
			saveDefaultConfig();
			logger = new MyLogger(this);
			config = new Conf(this);
			sql = new MySQL(this , config.getString("settings.mysql.url") , config.getString("settings.mysql.db")
					, config.getString("settings.mysql.user") , config.getString("settings.mysql.password"));

			jb = new JsonBuilder();
			tabapi = new TabAPI(this);
			tellrawapi = new TellrawAPI(this);
			titleapi = new TitleAPI(this);
			uidapi = new UidAPI(this);
			playerid = new PlayerID(this);
			pointapi = getPointAPI();
			datecomp = new DateCompUtil(this);
			chatm = new ChatChannelManager(this);
			rankmanager = new RankManager(this);
			bbm = new BreakBlockManager(this);
			worldutil = new WorldUtil(this);
			home = new Home(this);
			nscore = new NScore(this);
			join = new JoinLeftManager(this);
			sale = new LimitedTimeSaleManager(this);
			shop = new ShopItemManager(this);
			stm = new StatisticsManager(this);
			menum = new MenuManager(this);

			pm.registerEvents(new EventListener(this) , this);
			pm.registerEvents(new VotifierListener(this), this);
			pm.registerEvents(new ChatListener(this), this);

			//コマンド登録
			Cmds cmds = new Cmds(this);
			Map<String, Map<String, Object>> pluginCommands
			= (Map<String, Map<String, Object>>) this.getDescription().getCommands();
			for (String command : pluginCommands.keySet()) {
				if (!command.equals("ch")) {
					Bukkit.getPluginCommand(command).setExecutor(cmds);
				} else {
					Bukkit.getPluginCommand(command).setExecutor(new Cmd_Channel(this));
				}
			}

			Twit.getKeys(this);

			available = true;

		} catch (Exception | Error e) {
			available = false;
			Bukkit.getLogger().info("[Manaita] Error : " + e.getLocalizedMessage());
			Bukkit.getLogger().info("[Manaita] プラグインを有効化できませんでした。サーバーをシャットダウンします。");
			e.printStackTrace();
			Bukkit.getServer().shutdown();
		}

	}


	@Override
	public void onDisable() {
		super.onDisable();
		sql.SQLClose();
	}


	public PluginManager getPluginManager() {
		return pm;
	}

	public Conf getConfigManager() {
		return config;
	}

	public boolean getAvailable() {
		return available;
	}
	public void setAvailable(boolean info) {
		available = info;
	}

	public boolean getChellYoubi() {
		return chellyoubi;
	}

	public void setChellYoubi(boolean info) {
		chellyoubi = info;
	}

	public boolean getTimeSale() {
		return timesale;
	}

	public void setTimeSale(boolean info) {
		timesale = info;
	}

	public MySQL getSQL() {
		return sql;
	}

	public JsonBuilder getJsonBuilder() {
		return jb;
	}

	public Home getHome() {
		return home;
	}

	public PlayerID getPlayerID() {
		return playerid;
	}

	public WorldUtil getWorldUtil() {
		return worldutil;
	}

	public ChatChannelManager getChatChannelManager() {
		return chatm;
	}

	public RankManager getRankManager() {
		return rankmanager;
	}

	public JoinLeftManager getJoinLeftManager() {
		return join;
	}

	public MyLogger getMyLogger() {
		return logger;
	}

	public NScore getNScore() {
		return nscore;
	}

	public TabAPI getTabAPI() {
		return tabapi;
	}

	public TellrawAPI getTellrawAPI() {
		return tellrawapi;
	}

	public TitleAPI getTitleAPI() {
		return titleapi;
	}

	public UidAPI getUidAPI() {
		return uidapi;
	}

	public BreakBlockManager getBreakBlockManager() {
		return bbm;
	}

	public VotifierListener getVotifierListener() {
		return votelistener;
	}

	public LimitedTimeSaleManager getLimitedTimeSaleManager() {
		return sale;
	}

	public ShopItemManager getShopManager() {
		return shop;
	}

	public MenuManager getMenuManager() {
		return menum;
	}

	public StatisticsManager getStatistics() {
		return stm;
	}

	public DateCompUtil getDateCompUtil() {
		return datecomp;
	}



	public String getPrefix() {
		return prefix;
	}

	public PointAPI getPointAPI() {
		if (pointapi == null) {
			Plugin plugin = pm.getPlugin("PointAPI");
			if (plugin instanceof PointAPI) {
				return (PointAPI)plugin;
			} else {
				return null;
			}
		} else {
			return pointapi;
		}
	}


	public String[] getServerHelp() {
		String pres = "   ";
		String[] str = {
				ChatColor.GOLD + "----------|" + ChatColor.GREEN + " Chelcy" + ChatColor.AQUA + " Manaita"
						+ ChatColor.GREEN + " Server " + ChatColor.GOLD + "|----------",
				pres + "サーバー情報",
				pres + "  まないたサーバー ver1.8-1.8.8",
				pres + "  サバイバル整地サーバー",
				pres + "システムやルール等",
				pres + "  チート・ハックは禁止。大規模な埋め立て禁止。",
				pres + "  整地のための掘削は露天堀り以外禁止。",
				pres + "  詳細 : http://wiki.mchel.net/",
				pres + "使用可能コマンド一覧",
				pres + "  /menu メニューを表示します。",
				pres + "  /manaita 各種情報を確認できます。",
				pres + "  /lobby ロビーにテレポートします。",
				pres + "  /spawn [world] そのワールドのスポーンへテレポートします。",
				pres + "  /sethome ホームを設定します。(ワールド毎に設定)",
				pres + "  /home [world] ホームへテレポートします。",
				pres + "  /gomi ゴミ箱用インベントリを表示します。",
				pres + "  /craft クラフトインベントリを表示します。",
				pres + "  /shop ショップを表示します。",
				pres + "  /rule ルールが記載されているURLを表示します。",
				pres + "  /rank 採掘数ランキングトップ10を表示します。",
				pres + "  /hn <スタック数> 石のハーフブロックを受け取ります。",
				pres + "  /ch チャットチャンネル設定をします。",
				ChatColor.GOLD + "-----------------------------------------"
		};
		return str;
	}



}
