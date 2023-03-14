package me.dalot.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.dalot.ProxyWhitelist;

import java.util.concurrent.CompletableFuture;

public class CommandBuilder {

    private static ProxyServer server;

    public static void register(ProxyWhitelist proxyWhitelist) {
        server = proxyWhitelist.getServer();
        final CommandHandler handler = new CommandHandler(proxyWhitelist);
        server.getCommandManager().register(server.getCommandManager().metaBuilder("proxywhitelist").build(), new BrigadierCommand(
                LiteralArgumentBuilder.<CommandSource>literal("proxywhitelist").requires(sender -> sender.hasPermission("proxywhitelist.admin")).executes(handler::about)
                        .then(LiteralArgumentBuilder.<CommandSource>literal("on").executes(handler::turnOn))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("off").executes(handler::turnOff))

                        .then(LiteralArgumentBuilder.<CommandSource>literal("add").executes(handler::add))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("add")
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("username", StringArgumentType.word())
                                        .suggests(CommandBuilder::allPlayers)
                                        .executes(handler::add)))

                        .then(LiteralArgumentBuilder.<CommandSource>literal("remove").executes(handler::remove))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("remove")
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("username", StringArgumentType.word())
                                        .suggests(CommandBuilder::allPlayers)
                                        .executes(handler::remove)))

                        .then(LiteralArgumentBuilder.<CommandSource>literal("reload").requires(source -> source.hasPermission("proxywhitelist.admin")).executes(handler::reload))
        ));
    }

    private static CompletableFuture<Suggestions> allPlayers(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        for (Player player : server.getAllPlayers()) {
            String username = player.getUsername();
            if (username.toLowerCase().startsWith(context.getInput().toLowerCase()) || username.equalsIgnoreCase(context.getInput())) {
                builder.suggest(username);
            }
        }
        return builder.buildFuture();
    }
}
