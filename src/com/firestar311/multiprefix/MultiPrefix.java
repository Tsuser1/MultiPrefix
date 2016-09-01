package com.firestar311.multiprefix;

import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.firestar311.multiprefix.listener.PlayerChatEss;
import com.firestar311.multiprefix.listener.PlayerChatNoEss;
import com.firestar311.multiprefix.listener.PlayerJoinEss;
import com.firestar311.multiprefix.listener.PlayerJoinNoEss;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class MultiPrefix extends JavaPlugin {
	private Permission permission = null;
	private Chat chat = null;
	private boolean useFormatting = true;
	private boolean usePermissionPrefix = true;
	private boolean usePermissionSuffix = true;
	private boolean useEssentials = false;
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
		if (useEssentials) {
			if (this.getServer().getPluginManager().getPlugin("Essentials") != null) {
				this.getServer().getPluginManager().registerEvents(new PlayerChatEss(this), this);
				this.getServer().getPluginManager().registerEvents(new PlayerJoinEss(this), this);
			} else {
				this.getServer().getPluginManager().registerEvents(new PlayerChatNoEss(this), this);
				this.getServer().getPluginManager().registerEvents(new PlayerJoinNoEss(this), this);
				useEssentials = false;
			}
		} else {
			this.getServer().getPluginManager().registerEvents(new PlayerChatNoEss(this), this);
			this.getServer().getPluginManager().registerEvents(new PlayerJoinNoEss(this), this);
			useEssentials = false;
		}

		World main = Bukkit.getWorld(this.getConfig().getString("main-world"));

		Set<String> gs = this.getConfig().getConfigurationSection("groups").getKeys(false);

		for (String s : gs) {
			String prefix = "";
			String suffix = "";
			String cformat = "";
			int priority = 0;
			if (usePermissionPrefix) {
				prefix = chat.getGroupPrefix(main, s);
			} else {
				prefix = this.getConfig().getString("groups." + s + ".prefix");
			}

			if (usePermissionSuffix) {
				suffix = chat.getGroupSuffix(main, s);
			} else {
				suffix = this.getConfig().getString("groups." + s + ".suffix");
			}

			if (useFormatting) {
				cformat = this.getConfig().getString("groups." + s + ".chat-format");
			}

			priority = this.getConfig().getInt("groups." + s + ".priority");

			Group group = new Group(s, prefix, suffix, cformat, priority);

			groups.put(s, group);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(useEssentials) {
				player.setDisplayName(new PlayerJoinEss(this).updateDisplayName(player));
			}else{
				player.setDisplayName(new PlayerJoinNoEss(this).updateDisplayName(player));
			}
		}
		if (cmd.getName().equalsIgnoreCase("multiprefix")) {
			if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
				this.reloadConfig();
				useFormatting = this.getConfig().getBoolean("chat-formatting");
				usePermissionPrefix = this.getConfig().getBoolean("permissions-prefix");
				usePermissionSuffix = this.getConfig().getBoolean("permissions-suffix");
				useEssentials = this.getConfig().getBoolean("using-essentials");

				World main = Bukkit.getWorld(this.getConfig().getString("main-world"));

				Set<String> gs = this.getConfig().getConfigurationSection("groups").getKeys(false);

				groups.clear();

				for (String s : gs) {
					String prefix = "";
					String suffix = "";
					String cformat = "";
					int priority = 0;
					if (usePermissionPrefix) {
						prefix = chat.getGroupPrefix(main, s);
					} else {
						prefix = this.getConfig().getString("groups." + s + ".prefix");
					}

					if (usePermissionSuffix) {
						suffix = chat.getGroupSuffix(main, s);
					} else {
						suffix = this.getConfig().getString("groups." + s + ".suffix");
					}

					if (useFormatting) {
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
	
	public Permission getPermission() {
		return permission;
	}

	public Chat getChat() {
		return chat;
	}

	public boolean isUseFormatting() {
		return useFormatting;
	}

	public boolean isUsePermissionPrefix() {
		return usePermissionPrefix;
	}

	public boolean isUsePermissionSuffix() {
		return usePermissionSuffix;
	}

	public TreeMap<String, Group> getGroups() {
		return groups;
	}
}