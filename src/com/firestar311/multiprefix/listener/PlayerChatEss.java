package com.firestar311.multiprefix.listener;

import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.firestar311.multiprefix.Group;
import com.firestar311.multiprefix.MultiPrefix;

public class PlayerChatEss implements Listener {

	private MultiPrefix plugin;
	private Essentials essentials;

	public PlayerChatEss(MultiPrefix plugin) {
		this.plugin = plugin;
		this.essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
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

		String playerName = "";

		User user = new User(player, essentials);
		playerName = user.getNick(false);
		playerName = player.getName();
		if (!(suffix == "")) {
			String dn = prefix + playerName + " " + suffix;
			dn = ChatColor.translateAlternateColorCodes('&', dn);
			player.setDisplayName(dn);
			user.setNickname(dn);
		} else {
			String dn = prefix + playerName;
			dn = ChatColor.translateAlternateColorCodes('&', dn);
			player.setDisplayName(dn);
			user.setNickname(dn);
		}

		String format = playerPrefixList.get(playerPrefixList.firstKey()).getFormat();
		format = format.replace("%displayname%", player.getDisplayName());
		format = format.replace("%message%", e.getMessage());
		format = ChatColor.translateAlternateColorCodes('&', format);
		e.setFormat(format);
	}
}