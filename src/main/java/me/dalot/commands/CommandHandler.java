package me.dalot.commands;


import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.velocitypowered.api.command.CommandSource;
import me.dalot.ProxyWhitelist;
import me.dalot.config.Configs;
import me.dalot.whitelist.WhitelistHelper;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CommandHandler {

    private final ProxyWhitelist proxyWhitelist;
    private final String prefix;

    public CommandHandler(ProxyWhitelist proxyWhitelist) {
        this.proxyWhitelist = proxyWhitelist;
        this.prefix = proxyWhitelist.PREFIX;
    }

    private void send(CommandSource source, String serializedString) {
        source.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(serializedString));
    }

    public int about(CommandContext<CommandSource> commandSourceCommandContext) {
        CommandSource source = commandSourceCommandContext.getSource();
        String status = Configs.getConfig().isEnabled() ? "&2&lTIL" : "&c&lFRA";
        send(source,"&a" + prefix + "Whitelist er lige nu " + status);
        send(source,"&a" + prefix + "ProxyWhitelist af Dalot421");
        return 1;
    }

    public int turnOn(CommandContext<CommandSource> commandSourceCommandContext) {
        CommandSource source = commandSourceCommandContext.getSource();
        if(Configs.getConfig().isEnabled()) {
            send(source,"&c" + prefix + "Whitelist er allerede slået til.");
        } else {
            Configs.getConfig().setEnabled(true);
            Configs.saveConfig(proxyWhitelist);
            send(source,"&a" + prefix + "Whitelist er nu slået &2&lTIL");
        }
        return 1;
    }

    public int turnOff(CommandContext<CommandSource> commandSourceCommandContext) {
        CommandSource source = commandSourceCommandContext.getSource();
        if(!Configs.getConfig().isEnabled()) {
            send(source,"&c" + prefix + "Whitelist er allerede slået fra.");
        } else {
            Configs.getConfig().setEnabled(false);
            Configs.saveConfig(proxyWhitelist);
            send(source,"&a" + prefix + "Whitelist er nu slået &c&lFRA");
        }
        return 1;
    }

    public int add(CommandContext<CommandSource> commandSourceCommandContext) {
        CommandSource source = commandSourceCommandContext.getSource();
        ParsedArgument<CommandSource, ?> username = commandSourceCommandContext.getArguments().get("username");
        if(username == null) {
            send(source,"&c" + prefix + "Syntax: /proxywhitelist add <spiller>");
            return 1;
        }

        new WhitelistHelper(proxyWhitelist, source).add((String) username.getResult());
        return 1;
    }

    public int remove(CommandContext<CommandSource> commandSourceCommandContext) {
        CommandSource source = commandSourceCommandContext.getSource();
        ParsedArgument<CommandSource, ?> username = commandSourceCommandContext.getArguments().get("username");
        if(username == null) {
            send(source,"&c" + prefix + "Syntax: /proxywhitelist remove <spiller>");
            return 1;
        }

        new WhitelistHelper(proxyWhitelist, source).remove((String) username.getResult());
        return 1;
    }

    public int reload(CommandContext<CommandSource> commandSourceCommandContext) {
        Configs.loadConfigs(proxyWhitelist);
        CommandSource source = commandSourceCommandContext.getSource();
        send(source,"&a" + prefix + "ProxyWhitelist er nu reloadet.");
        send(source,"&a" + prefix + "Lavet af Dalot421.");
        return 1;
    }
}
