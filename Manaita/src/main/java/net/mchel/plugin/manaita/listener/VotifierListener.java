package net.mchel.plugin.manaita.listener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.api.UidAPI;
import net.mchel.plugin.manaita.api.UidAPI.GetUid;
import net.mchel.plugin.manaita.util.MyLogger;
import net.mchel.plugin.pointapi.PointAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VotifierListener implements VoteListener , Listener{

	private Manaita plugin;
	private PointAPI pointapi;
	private MyLogger logger;
	private UidAPI uidapi;
	private String prefix;
	public VotifierListener(Manaita manaita) {
		this.plugin= manaita;
		this.pointapi = plugin.getPointAPI();
		this.logger = plugin.getMyLogger();
		this.uidapi = plugin.getUidAPI();
		this.prefix = plugin.getPrefix();
	}
	@Override
	public void voteMade(Vote vote) {
		logger.info("Received : " + vote);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVotifierEvent(VotifierEvent e) {
		Vote vote = e.getVote();
		String player_name = vote.getUsername();
		@SuppressWarnings("deprecation")
		Player p = plugin.getServer().getPlayer(player_name);
		String uuid;
		if (p == null) {
			procUid(player_name);
			return;
		} else {
			uuid = p.getUniqueId().toString();
		}
		boolean result = pointapi.addPoint(uuid, player_name, 3, "[Manaita]Daily vote point");
		if (!result) {
			logger.warn("Daily vote point is missing. Player name is " + player_name);
			return;
		} else if (p != null) {
			p.sendMessage(prefix + "投票を受け付けました。ポイントが3Chell追加されます。");
		}
	}


	private void procUid(final String player_name) {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		GetUid task = uidapi.new GetUid(player_name);
		final Future<String> response = executor.submit(task);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				String recieveuuid;
				try {
					recieveuuid = response.get();
					if (recieveuuid != null) {
						boolean result = pointapi.addPoint(recieveuuid, player_name, 3, "[Manaita]Daily vote point");
						if (!result) {
							logger.warn("Daily vote point is missing. Player name is " + player_name);
							return;
						}
					}
				} catch (InterruptedException | ExecutionException e) {
					logger.error(e);
				}
			}
		});
	}


}
