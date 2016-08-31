package com.firestar311.multiprefix;

import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
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
	private boolean useFormatting = true;
	private boolean usePermissionPrefix = true;
	private boolean usePermissionSuffix = true;
	private boolean useEssentials = false;
	private Essentials essentials;
	private TreeMap<String, Group> groups = new TreeMap<String, Group>();

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
		usePermissionPrefix = this.getConfig().getBoolean("permissions-prefix");
		usePermissionSuffix = this.getConfig().getBoolean("permissions-suffix");
		useEssentials = this.getConfig().getBoolean("using-essentials");
		if (useEssentials)
			essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
		this.getServer().getPluginManager().registerEvents(this, this);

		World main = Bukkit.getWorld(this.getConfig().getString("main-world"));

		Set<String> gs = this.getConfig().getConfigurationSection("groups").getKeys(false);
		
		for(String s : gs) {
			String prefix = "";
			String suffix = "";
			String cformat = "";
			int priority = 0;
			if(usePermissionPrefix) {
				prefix = chat.getGroupPrefix(main, s);
			}else{
				prefix = this.getConfig().getString("groups." + s + ".prefix");
			}
			
			if(usePermissionSuffix) {
				suffix = chat.getGroupSuffix(main, s);
			}else{
				suffix = this.getConfig().getString("groups." + s + ".suffix");
			}
			
			if(useFormatting) {
				cformat = this.getConfig().getString("groups." + s + ".chat-format");
			}
			
			priority = this.getConfig().getInt("groups." + s + ".priority");
			
			Group group = new Group(s, prefix, suffix, cformat, priority);
			
			groups.put(s, group);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("multiprefix")) {
			if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
				this.reloadConfig();
				useFormatting = this.getConfig().getBoolean("chat-formatting");
				usePermissionPrefix = this.getConfig().getBoolean("permissions-prefix");
				usePermissionSuffix = this.getConfig().getBoolean("permissions-suffix");
				useEssentials = this.getConfig().getBoolean("using-essentials");
				if (useEssentials)
					essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
				this.getServer().getPluginManager().registerEvents(this, this);

				World main = Bukkit.getWorld(this.getConfig().getString("main-world"));

				Set<String> gs = this.getConfig().getConfigurationSection("groups").getKeys(false);
				
				groups.clear();
				
				for(String s : gs) {
					String prefix = "";
					String suffix = "";
					String cformat = "";
					int priority = 0;
					if(usePermissionPrefix) {
						prefix = chat.getGroupPrefix(main, s);
					}else{
						prefix = this.getConfig().getString("groups." + s + ".prefix");
					}
					
					if(usePermissionSuffix) {
						suffix = chat.getGroupSuffix(main, s);
					}else{
						suffix = this.getConfig().getString("groups." + s + ".suffix");
					}
					
					if(useFormatting) {
						cformat = this.getConfig().getString("groups." + s + ".chat-format");
					}
					
					priority = this.getConfig().getInt("groups." + s + ".priority");
					
					Group group = new Group(s, prefix, suffix, cformat, priority);
					
					groups.put(s, group);
				}
				String p = "§7[§9MultiPrefix§7] ";
				sender.sendMessage(p + "§aConfiguration reloaded.");
			}
		}
		return true;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		String[] playerGroups = permission.getPlayerGroups(player);
		TreeMap<Integer, Group> playerPrefixList = new TreeMap<Integer, Group>();
		for(String pg : playerGroups) {
			Group group = groups.get(pg);
			playerPrefixList.put(group.getPriority(), group);
		}
		
		String prefix = "";
		String suffix = "";
		for(int i : playerPrefixList.keySet()) {
			Group group = playerPrefixList.get(i);
			prefix += group.getPrefix();
			suffix += group.getSuffix();
		}
		
		String playerName = "";
		
		if(useEssentials) {
			User user = new User(player, essentials);
			playerName = user.getNick(false);
		}else{
			playerName = player.getName();
		}
		if(!(suffix == ""))
			player.setDisplayName(prefix + playerName + " " + suffix);
		else {
			player.setDisplayName(prefix + playerName);
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		String[] playerGroups = permission.getPlayerGroups(player);
		TreeMap<Integer, Group> playerPrefixList = new TreeMap<Integer, Group>();
		for(String pg : playerGroups) {
			Group group = groups.get(pg);
			playerPrefixList.put(group.getPriority(), group);
		}
		
		String prefix = "";
		String suffix = "";
		for(int i : playerPrefixList.keySet()) {
			Group group = playerPrefixList.get(i);
			prefix += group.getPrefix();
			suffix += group.getSuffix();
		}
		
		String playerName = "";
		
		if(useEssentials) {
			User user = new User(player, essentials);
			playerName = user.getNick(false);
		}else{
			playerName = player.getName();
		}
		if(!(suffix == ""))
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
}