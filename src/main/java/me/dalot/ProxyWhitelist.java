package me.dalot;

import com.google.inject.Inject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.dalot.commands.CommandBuilder;
import me.dalot.commands.CommandHandler;
import me.dalot.config.Configs;
import me.dalot.listeners.JoinListener;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "ProxyWhitelist",
        name = "ProxyWhitelist",
        version = "1.1",
        authors = {"Dalot421"}
)
public class ProxyWhitelist {

    public final String PREFIX = "[ProxyWhitelist] ";
    @Getter
    private final ProxyServer server;
    @Getter
    private final Logger logger;
    @Getter
    private final Path dataDirectory;

    @Inject
    public ProxyWhitelist(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Configs.loadConfigs(this);

        server.getEventManager().register(this, new JoinListener());

        final CommandHandler handler = new CommandHandler(this);
        LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder.<CommandSource>literal("proxywhitelist").build();

        CommandBuilder.register(this);
    }
}
