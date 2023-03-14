package me.dalot.whitelist;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import me.dalot.ProxyWhitelist;
import me.dalot.config.Configs;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.UUID;

public class WhitelistHelper {

    private ProxyWhitelist proxyWhitelist;
    private CommandSource source;
    private MinecraftApi minecraftApi;

    public WhitelistHelper(ProxyWhitelist proxyWhitelist, CommandSource source) {
        this.proxyWhitelist = proxyWhitelist;
        this.source = source;
        this.minecraftApi = new MinecraftApi(proxyWhitelist);
    }

    private void send(CommandSource source, String serializedString) {
        source.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(serializedString));
    }

    public static boolean check(Player player) {
        return player.hasPermission("proxywhitelist.bypass") || Configs.getWhitelisted().contains(player.getUniqueId());
    }

    public void add(String username) {
        proxyWhitelist.getServer().getScheduler().buildTask(proxyWhitelist, () -> {
            UUID uuid = minecraftApi.getUUID(username);
            if (uuid == null) {
                send(source, "&c" + proxyWhitelist.PREFIX + username + " er ikke et gyldigt navn.");
            } else if (Configs.getWhitelisted().contains(uuid)) {
                send(source, "&a" + proxyWhitelist.PREFIX + username + " er allerede whitelisted.");
            } else {
                Configs.getWhitelisted().add(uuid);
                Configs.saveWhitelist(proxyWhitelist);
                send(source, "&a" + proxyWhitelist.PREFIX + username + " er blevet tilføjet til whitelisten.");
            }
        }).schedule();
    }

    public void remove(String username) {
        proxyWhitelist.getServer().getScheduler().buildTask(proxyWhitelist, () -> {
            UUID uuid = minecraftApi.getUUID(username);
            if (uuid == null) {
                send(source, "&c" + proxyWhitelist.PREFIX + username + " er ikke et gyldigt navn.");
            } else if (!Configs.getWhitelisted().contains(uuid)) {
                send(source, "&a" + proxyWhitelist.PREFIX + username + " er ikke whitelisted.");
            } else {
                Configs.getWhitelisted().remove(uuid);
                Configs.saveWhitelist(proxyWhitelist);
                send(source, "&a" + proxyWhitelist.PREFIX + username + " er blevet fjernet fra whitelisten.");
            }
        }).schedule();
    }

}
