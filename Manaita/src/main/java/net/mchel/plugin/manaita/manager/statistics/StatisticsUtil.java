package net.mchel.plugin.manaita.manager.statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.UUID;

import net.mchel.plugin.manaita.sql.MySQL;

/**
 * @author chelcy
 */
public class StatisticsUtil {

	public interface StatisticsCallback {
		public void callbackMethod(UUID taskid , int typeid , int number);
	}

	private StatisticsCallback stcallback;

	private MySQL sql;
	public StatisticsUtil(MySQL sql) {
		this.sql = sql;
	}

	public void setCallbacks(StatisticsCallback stc) {
		stcallback = stc;
	}


	/**
	 * ここ24時間で掘ったブロック数
	 * @param player_id
	 * @return
	 */
	public void get24H(UUID taskid , int player_id) {
		Connection conn = sql.getNewConnection();
		if (conn == null) {
			stcallback.callbackMethod(taskid, 1, -1);
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		long time = cal.getTimeInMillis()/1000;
		int result = -1;
		try {
			PreparedStatement ps = conn.prepareStatement("select count(log_id) as num from Mn_blocklog where epoch>=? and player_id=?;");
			ps.setLong(1, time);
			ps.setInt(2, player_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			sql.close(conn);
		}
		stcallback.callbackMethod(taskid, 1, result);
	}

	/**
	 * ここ1週間で掘ったブロック数
	 * @param player_id
	 * @return
	 */
	public void get1Week(UUID taskid , int player_id) {
		Connection conn = sql.getNewConnection();
		if (conn == null) {
			stcallback.callbackMethod(taskid, 1, -1);
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		long time = cal.getTimeInMillis()/1000;
		int result = -1;
		try {
			PreparedStatement ps = conn.prepareStatement("select count(log_id) as num from Mn_blocklog where epoch>=? and player_id=?;");
			ps.setLong(1, time);
			ps.setInt(2, player_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			sql.close(conn);
		}
		stcallback.callbackMethod(taskid, 2, result);
	}

	/**
	 * ここ1ヶ月で掘ったブロック数
	 * @param player_id
	 * @return
	 */
	public void get1Month(UUID taskid , int player_id) {
		Connection conn = sql.getNewConnection();
		if (conn == null) {
			stcallback.callbackMethod(taskid, 1, -1);
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		long time = cal.getTimeInMillis()/1000;
		int result = -1;
		try {
			PreparedStatement ps = conn.prepareStatement("select count(log_id) as num from Mn_blocklog where epoch>=? and player_id=?;");
			ps.setLong(1, time);
			ps.setInt(2, player_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			sql.close(conn);
		}
		stcallback.callbackMethod(taskid, 3, result);
	}

	/**
	 * 今日掘ったブロック数
	 * @param player_id
	 * @return
	 */
	public void getToday(UUID taskid , int player_id) {
		Connection conn = sql.getNewConnection();
		if (conn == null) {
			stcallback.callbackMethod(taskid, 1, -1);
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
		long time = cal.getTimeInMillis()/1000;
		int result = -1;
		try {
			PreparedStatement ps = conn.prepareStatement("select count(log_id) as num from Mn_blocklog where epoch>=? and player_id=?;");
			ps.setLong(1, time);
			ps.setInt(2, player_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			sql.close(conn);
		}
		stcallback.callbackMethod(taskid, 4, result);
	}

	/**
	 * 今週掘ったブロック数
	 * @param player_id
	 * @return
	 */
	@SuppressWarnings("static-access")
	public void getThisWeek(UUID taskid , int player_id) {
		Connection conn = sql.getNewConnection();
		if (conn == null) {
			stcallback.callbackMethod(taskid, 1, -1);
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
		cal.add(cal.DATE, -6);
		long time = cal.getTimeInMillis()/1000;
		int result = -1;
		try {
			PreparedStatement ps = conn.prepareStatement("select count(log_id) as num from Mn_blocklog where epoch>=? and player_id=?;");
			ps.setLong(1, time);
			ps.setInt(2, player_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			sql.close(conn);
		}
		stcallback.callbackMethod(taskid, 5, result);
	}

	/**
	 * 今月掘ったブロック数
	 * @param player_id
	 * @return
	 */
	public void getThisMonth(UUID taskid , int player_id) {
		Connection conn = sql.getNewConnection();
		if (conn == null) {
			stcallback.callbackMethod(taskid, 1, -1);
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 0, 0, 0, 0);
		long time = cal.getTimeInMillis()/1000;
		int result = -1;
		try {
			PreparedStatement ps = conn.prepareStatement("select count(log_id) as num from Mn_blocklog where epoch>=? and player_id=?;");
			ps.setLong(1, time);
			ps.setInt(2, player_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			sql.close(conn);
		}
		stcallback.callbackMethod(taskid, 6, result);
	}



}
