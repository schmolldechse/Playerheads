package com.github.schmolldechse.playerheads;

import com.github.schmolldechse.playerheads.listener.AsyncChatListener;
import com.google.inject.Singleton;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@Singleton
@DefaultQualifier(NonNull.class)
public final class Playerheads extends JavaPlugin {

    public ImageService imageService;

    //private UUIDFetcher uuidFetcher = new UUIDFetcher();

    @Override
    public void onEnable() {
        // Plugin startup logic

        this.imageService = new ImageService(this);

        getServer().getPluginManager().registerEvents(new AsyncChatListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}
