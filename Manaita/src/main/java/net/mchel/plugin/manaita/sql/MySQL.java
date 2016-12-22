package net.mchel.plugin.manaita.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.util.MyLogger;

public class MySQL {

	private Manaita plugin;
	private String db;
	private String user;
	private String pw;
	private String url;
	private MyLogger logger;
	public MySQL(Manaita manaita , String url , String db , String user , String password) {
		this.plugin = manaita;
		this.user = user;
		this.pw = password;
		this.url = url;
		this.db = db;
		this.logger = plugin.getMyLogger();
		SQLConnect();
	}

	private Connection connection = null;
	private Statement statement = null;


	public void SQLConnect() {
		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					connection = DriverManager.getConnection("jdbc:mysql://" + url + ":3306/" + db , user , pw);
					statement = connection.createStatement();
					log("DBの接続に成功しました。");
					plugin.setAvailable(true);
				} catch (SQLException e) {
					logger.error(e);
					log("DBに接続できませんでした。");
					log("プラグインを無効化します。");
					plugin.setAvailable(false);
					return;
				}
				SQLExecuteUpdate("CREATE TABLE IF NOT EXISTS Mn_info(player_id int PRIMARY KEY , uuid varchar(36) not null , "
						+ "latest_name CHAR(16), block int default 0 , rank tinyint default 0 , time datetime , UNIQUE (player_id , uuid));");
				SQLExecuteUpdate("CREATE TABLE IF NOT EXISTS Mn_login(log_id INT(10) NOT NULL AUTO_INCREMENT , player_id int , uuid varchar(36) not null , "
						+ "player_name char(16) not null , ip_address text not null , time datetime , action tinyint , UNIQUE(log_id));");
				SQLExecuteUpdate("CREATE TABLE IF NOT EXISTS Mn_blocklog(log_id INT(10) NOT NULL AUTO_INCREMENT , player_id int, uuid varchar(36) not null , "
						+ "latest_name CHAR(16), world CHAR(50) not null , x int not null , y int not null , z int not null "
						+ ", block_id int not null , block_meta int not null , result bit , epoch int(10) , UNIQUE(log_id));");
				SQLExecuteUpdate("CREATE TABLE IF NOT EXISTS Mn_ranklog(log_id INT(10) NOT NULL AUTO_INCREMENT , player_id int, uuid varchar(36) not null , "
						+ "latest_name CHAR(16), rank_old int not null , rank_aft int not null , block int not null "
						+ ", result bit , time datetime , UNIQUE(log_id));");
				SQLExecuteUpdate("CREATE TABLE IF NOT EXISTS Mn_home(player_id int not null , uuid varchar(36) not null , "
						+ "latest_name CHAR(16), world CHAR(50) not null , x double not null , y double not null , z double not null "
						+ ", pitch float not null , yaw float not null , time datetime);");
			}
		});
	}

	//SQLアップデート
	public boolean SQLExecuteUpdate(String query) {
		if (connection != null) {
			try {
				statement.executeUpdate(query);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error(e);
				return false;
			}
		} else {
			if (refSQL()) {
				return SQLExecuteUpdate(query);
			} else {
				return false;
			}
		}
	}

	//SQLクエリの受け取り
	public ResultSet SQLExecuteQuery(String query) {
		ResultSet rs = null;
		if (connection != null) {
			try {
				rs = statement.executeQuery(query);
			} catch (SQLException e) {
				logger.error(e);
			}
		} else {
			if (refSQL()) {
				return SQLExecuteQuery(query);
			} else {
				log("connection is null");
			}
		}
		return rs;
	}

	//接続
	private boolean refSQL() {
		SQLClose();
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + url + ":3306/" + db , user , pw);
			statement = connection.createStatement();
			return true;
		} catch (SQLException e) {
			logger.error(e);
			return false;
		}
	}

	//SQL切断
	public void SQLClose() {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
			if (statement != null) {
				statement.close();
				statement = null;
			}
		} catch (SQLException e) {
			logger.error(e);
		}
	}

	public Connection getConnection() {
		if (connection == null) {
			refSQL();
		}
		return connection;
	}

	public Connection getNewConnection() {
		try {
			return DriverManager.getConnection("jdbc:mysql://" + url + ":3306/" + db , user , pw);
		} catch (SQLException e) {
			logger.error(e);
		}
		return null;
	}

	public void log(String msg) {
		Bukkit.getLogger().info("[ManaitaDB] " + msg);
	}

	public boolean close(PreparedStatement st) {
		if (st == null) {
			return false;
		}
		try {
			st.close();
			return true;
		} catch (SQLException e) {
			log(e.getLocalizedMessage());
		}
		return false;
	}
	public boolean close(ResultSet rs) {
		if (rs == null) {
			return false;
		}
		try {
			rs.close();
			return true;
		} catch (SQLException e) {
			log(e.getLocalizedMessage());
		}
		return false;
	}
	public boolean close(Connection conn) {
		if (conn == null) {
			return false;
		}
		try {
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
