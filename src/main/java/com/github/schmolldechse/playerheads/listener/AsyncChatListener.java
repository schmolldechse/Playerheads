package com.github.schmolldechse.playerheads.listener;

import com.github.schmolldechse.playerheads.Playerheads;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

@Singleton
public class AsyncChatListener implements Listener, ChatRenderer {

    @Inject
    private Playerheads plugin;

    public AsyncChatListener(Playerheads plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listen(AsyncChatEvent event) {
        event.renderer(this::render);
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        try {
            Component component = Component.empty()
                    .append(this.plugin.imageService.component(source.getUniqueId()))
                    .append(sourceDisplayName)
                    .append(Component.text(": "))
                    .append(message);

            return component;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
