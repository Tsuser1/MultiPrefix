package com.firestar311.multiprefix.listener;

import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.firestar311.multiprefix.Group;
import com.firestar311.multiprefix.MultiPrefix;

public class PlayerChatNoEss implements Listener {

	private MultiPrefix plugin;

	public PlayerChatNoEss(MultiPrefix plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
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

		String playerName = player.getName();
		
		if (!(suffix == ""))
			player.setDisplayName(prefix + playerName + " " + suffix);
		else {
			player.setDisplayName(prefix + playerName);
		}

		String format = playerPrefixList.get(playerPrefixList.firstKey()).getFormat();
		format = format.replace("%displayname%", player.getDisplayName());
		format = format.replace("%message%", e.getMessage());
		format = ChatColor.translateAlternateColorCodes('&', format);
		e.setFormat(format);
	}
}