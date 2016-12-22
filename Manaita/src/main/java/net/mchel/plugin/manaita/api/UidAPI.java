package net.mchel.plugin.manaita.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;

import net.mchel.plugin.manaita.Manaita;
import net.mchel.plugin.manaita.util.MyLogger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UidAPI {

	private Manaita plugin;
	private MyLogger logger;
	public UidAPI(Manaita mn) {
		this.plugin = mn;
		this.logger = plugin.getMyLogger();
	}


	public class GetUid implements Callable<String> {

		private String name;
		public GetUid(String name) {
			this.name = name;
		}

		@Override
		public String call() throws Exception {
			URL url;
			try {
				url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name + "?at=" + System.currentTimeMillis());
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
				String input;
				String msg = "";
				while ((input = in.readLine()) != null) {
					msg += input;
				}
				if (msg == null || msg.equalsIgnoreCase("")) {
					return null;
				}
				JsonParser parser = new JsonParser();
				JsonObject o = (JsonObject)parser.parse(msg);
				JsonElement uuid = o.get("id");
				String uid = uuid.getAsString();
				return convertUUIDtoHypenUUID(uid);
			} catch (IOException e) {
				logger.error(e);
			}
			return null;
		}
		public String convertUUIDtoHypenUUID(String uuid) {
			return uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})","$1-$2-$3-$4-$5");
		}
	}


	/*
	public int getInint(String name) {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		GetUidCore task = new GetUidCore(name);
		final Future<Integer> response = executor.submit(task);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				int i = 0;
				try {
					i = response.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
	}*/


}
