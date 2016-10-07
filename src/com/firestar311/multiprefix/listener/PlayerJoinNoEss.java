package com.firestar311.multiprefix.listener;

import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.firestar311.multiprefix.Group;
import com.firestar311.multiprefix.MultiPrefix;

public class PlayerJoinNoEss implements Listener {

	private MultiPrefix plugin;

	public PlayerJoinNoEss(MultiPrefix plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		String[] playerGroups = plugin.getPermission().getPlayerGroups(player);
		TreeMap<Integer, Group> playerPrefixList = new TreeMap<Integer, Group>();
		boolean playerSuffixes = plugin.getConfig().getBoolean("player-suffixes");
		for (String pg : playerGroups) {
			Group group = plugin.getGroups().get(pg);
			playerPrefixList.put(group.getPriority(), group);
		}

		String prefix = "";
		String suffix = "";
		if(playerSuffixes) {
			suffix += plugin.getChat().getPlayerSuffix(player);
		}
		
		for (int i : playerPrefixList.keySet()) {
			Group group = playerPrefixList.get(i);
			prefix += group.getPrefix();
			suffix += group.getSuffix();
		}
		
		if(!playerSuffixes) {
			suffix += plugin.getChat().getPlayerSuffix(player);
		}

		String playerName = player.getName();
		if (!(suffix == "")) {
			String dn = prefix + playerName + " " + suffix;
			dn = ChatColor.translateAlternateColorCodes('&', dn);
			player.setDisplayName(dn);
		} else {
			String dn = prefix + playerName;
			dn = ChatColor.translateAlternateColorCodes('&', dn);
			player.setDisplayName(dn);
		}
	}

	public String updateDisplayName(Player player) {
		String[] playerGroups = plugin.getPermission().getPlayerGroups(player);
		TreeMap<Integer, Group> playerPrefixList = new TreeMap<Integer, Group>();
		for (String pg : playerGroups) {
			Group group = plugin.getGroups().get(pg);
			playerPrefixList.put(group.getPriority(), group);
		}

		String prefix = "";
		String suffix = "";
		for (int i : playerPrefixList.keySet()) {
			Group group = playerPrefixList.get(i);
			prefix += group.getPrefix();
			suffix += group.getSuffix();
		}

		String playerName = "";

		if (!(suffix == "")) {
			String dn = prefix + playerName + " " + suffix;
			dn = ChatColor.translateAlternateColorCodes('&', dn);
			return dn;
		} else {
			String dn = prefix + playerName;
			dn = ChatColor.translateAlternateColorCodes('&', dn);
			return dn;
		}
	}
}