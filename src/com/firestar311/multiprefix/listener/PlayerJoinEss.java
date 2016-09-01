package com.firestar311.multiprefix.listener;

import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.firestar311.multiprefix.Group;
import com.firestar311.multiprefix.MultiPrefix;

public class PlayerJoinEss implements Listener {
	
	private MultiPrefix plugin;
	private Essentials essentials;
	
	public PlayerJoinEss(MultiPrefix plugin) {
		this.plugin = plugin;
		this.essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
	}
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
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
		User user = new User(player, essentials);
		playerName = user.getNick(false);
		
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