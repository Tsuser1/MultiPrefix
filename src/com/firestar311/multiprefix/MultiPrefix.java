package com.firestar311.multiprefix;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class MultiPrefix extends JavaPlugin implements Listener {
	private Permission permission = null;
	private Chat chat = null;
	private Set<String> configGroups;
	private HashMap<String, Group> groups = new HashMap<String, Group>();
	private boolean useFormatting = true;
	private boolean usePrefixes = true;
	private boolean useEssentials = false;
	private Essentials essentials;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		if (!this.setupPermissions()) {
			this.getLogger().log(Level.SEVERE, "Vault hook failed. Plugin disabled");
			this.getServer().getPluginManager().disablePlugin(this);
		}
		if (!this.setupChat()) {
			this.getLogger().log(Level.SEVERE, "Vault hook failed. Plugin disabled");
			this.getServer().getPluginManager().disablePlugin(this);
		}
		useFormatting = this.getConfig().getBoolean("chat-formatting");
		usePrefixes = this.getConfig().getBoolean("prefixes");
		useEssentials = this.getConfig().getBoolean("using-essentials");
		if (useEssentials)
			essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
		this.getServer().getPluginManager().registerEvents(this, this);
		configGroups = this.getConfig().getConfigurationSection("groups").getKeys(false);
		groups = new HashMap<String, Group>();
		for (String s : configGroups) {
			String prefix = this.getConfig().getString("groups." + s + ".prefix");
			String suffix = this.getConfig().getString("groups." + s + ".suffix");
			String format = this.getConfig().getString("groups." + s + ".chat-format");
			int priority = this.getConfig().getInt("groups." + s + ".priority");
			Group group = new Group(s, prefix, suffix, format, priority);
			groups.put(s, group);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("multiprefix")) {
			if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
				this.reloadConfig();
				configGroups = this.getConfig().getConfigurationSection("groups").getKeys(false);
				groups = new HashMap<String, Group>();
				for (String s : configGroups) {
					String prefix = this.getConfig().getString("groups." + s + ".prefix");
					String suffix = this.getConfig().getString("groups." + s + ".suffix");
					String format = this.getConfig().getString("groups." + s + ".chat-format");
					int priority = this.getConfig().getInt("groups." + s + ".priority");
					Group group = new Group(s, prefix, suffix, format, priority);
					groups.put(s, group);
				}
				useFormatting = this.getConfig().getBoolean("chat-formatting");
				usePrefixes = this.getConfig().getBoolean("prefixes");
				useEssentials = this.getConfig().getBoolean("using-essentials");
				if (useEssentials)
					essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
				String p = "§7[§9MultiPrefix§7] ";
				sender.sendMessage(p + "§aConfiguration reloaded.");
			}
		}
		return true;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		this.setDisplayName(e.getPlayer());
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		this.setDisplayName(e.getPlayer());
		if (useFormatting) {
			String[] playerGroups = permission.getPlayerGroups(player);
			TreeMap<Integer, Group> playerGroupList = new TreeMap<Integer, Group>();
			for (String s : playerGroups) {
				Group pg = groups.get(s);
				playerGroupList.put(pg.getPriority(), pg);
			}
			Group mainGroup = playerGroupList.get(playerGroupList.firstKey());
			String format = mainGroup.getFormat();
			format = format.replace("%displayname%", player.getDisplayName());
			format = format.replace("%suffix%", mainGroup.getSuffix());
			format = format.replace("%message%", e.getMessage());
			format = format.replace("&", "§");
			System.out.println(format);
			e.setFormat(format);
		}
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}

		return (chat != null);
	}

	private void setDisplayName(Player player) {
		String[] playerGroups = permission.getPlayerGroups(player);
		TreeMap<Integer, Group> playerGroupList = new TreeMap<Integer, Group>();
		for (String s : playerGroups) {
			Group pg = groups.get(s);
			playerGroupList.put(pg.getPriority(), pg);
		}
		String prefixes = "";
		if (!usePrefixes) {
			for (int g : playerGroupList.keySet()) {
				prefixes += chat.getGroupPrefix(player.getWorld(), playerGroupList.get(g).getName());
			}
		} else {
			for (int g : playerGroupList.keySet()) {
				prefixes += playerGroupList.get(g).getPrefix();
			}
		}
		prefixes = prefixes.replace("&", "§");
		if (useEssentials) {
			if(!essentials.getSettings().addPrefixSuffix()) {
				User user = new User(player, essentials);
				player.setDisplayName(prefixes + user.getNick(false));
			}else{
				player.setDisplayName(prefixes + player.getName());
			}
		} else {
			player.setDisplayName(prefixes + player.getName());
		}
	}
}