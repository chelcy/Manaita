package net.mchel.plugin.manaita.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author chelcy
 */
public class Twit {

	private static String OAuthConsumerKey = "";
	private static String OAuthConsumerSecret = "";
	private static String OAuthAccessToken = "";
	private static String OAuthAccessTokenSecret = "";

	/**
	 * Twitterでつぶやきます
	 * @param message メッセージ
	 * @return true:成功 false:失敗
	 */
	public static boolean sendTwit(String msg) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(OAuthConsumerKey);
		cb.setOAuthConsumerSecret(OAuthConsumerSecret);
		cb.setOAuthAccessToken(OAuthAccessToken);
		cb.setOAuthAccessTokenSecret(OAuthAccessTokenSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		User user = null;
		try {
			user = twitter.verifyCredentials();
			System.out.println(user.getName());
			System.out.println(user.getScreenName());
			System.out.println(user.getFriendsCount());
			System.out.println(user.getFollowersCount());
			twitter.updateStatus(msg);
			return true;
		} catch (TwitterException e) {
			Bukkit.getLogger().warning(e.getLocalizedMessage());
			return false;
		}
	}

	/**
	 * DMを送信
	 * @param toUser アットなしユーザーID
	 * @param message 送る内容 \nで改行
	 * @return 成功ならtrue
	 */
	public static boolean sendDirectMessage(String toUser , String message) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(OAuthConsumerKey);
		cb.setOAuthConsumerSecret(OAuthConsumerSecret);
		cb.setOAuthAccessToken(OAuthAccessToken);
		cb.setOAuthAccessTokenSecret(OAuthAccessTokenSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		try {
			twitter.sendDirectMessage(toUser, message);
			return true;
		} catch (IllegalStateException | TwitterException e) {
			Bukkit.getLogger().warning(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return false;
	}

	//config系
	private static FileConfiguration config;
	public static void getKeys(Plugin plugin) {
		config = plugin.getConfig();
		OAuthConsumerKey = config.getString("OAuthConsumerKey", "");
		OAuthConsumerSecret = config.getString("OAuthConsumerSecret", "");
		OAuthAccessToken = config.getString("OAuthAccessToken", "");
		OAuthAccessTokenSecret = config.getString("OAuthAccessTokenSecret", "");
		plugin.saveConfig();
	}


}
