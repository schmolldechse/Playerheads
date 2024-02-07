package org.github.schmolldechse.playerheads;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Singleton
@DefaultQualifier(NonNull.class)
public final class Playerheads extends JavaPlugin {

    @Inject
    ImageService imageService;

    private TextColor[][] DEFAULT_HEAD_TEXTURE = new TextColor[][]{
            {TextColor.color(50, 37, 17), TextColor.color(50, 37, 17), TextColor.color(62, 42, 20), TextColor.color(62, 42, 20), TextColor.color(62, 42, 20), TextColor.color(62, 42, 20), TextColor.color(50, 37, 17), TextColor.color(42, 30, 12)},
            {TextColor.color(37, 25, 8), TextColor.color(50, 37, 17), TextColor.color(50, 37, 17), TextColor.color(62, 42, 20), TextColor.color(62, 42, 20), TextColor.color(50, 37, 17), TextColor.color(62, 42, 20), TextColor.color(50, 37, 17)},
            {TextColor.color(42, 30, 12), TextColor.color(155, 98, 72), TextColor.color(179, 121, 94), TextColor.color(182, 130, 107), TextColor.color(179, 121, 94), TextColor.color(170, 115, 88), TextColor.color(155, 98, 72), TextColor.color(53, 36, 18)},
            {TextColor.color(155, 98, 72), TextColor.color(170, 115, 88), TextColor.color(179, 121, 94), TextColor.color(179, 121, 94), TextColor.color(170, 115, 88), TextColor.color(170, 115, 88), TextColor.color(170, 115, 88), TextColor.color(155, 98, 72)},
            {TextColor.color(170, 115, 88), TextColor.color(255, 254, 254), TextColor.color(83, 60, 137), TextColor.color(170, 115, 88), TextColor.color(155, 98, 72), TextColor.color(83, 60, 137), TextColor.color(255, 254, 254), TextColor.color(170, 115, 88)},
            {TextColor.color(155, 98, 72), TextColor.color(170, 115, 88), TextColor.color(170, 115, 88), TextColor.color(106, 65, 49), TextColor.color(106, 65, 49), TextColor.color(170, 115, 88), TextColor.color(170, 115, 88), TextColor.color(155, 98, 72)},
            {TextColor.color(144, 88, 62), TextColor.color(142, 95, 62), TextColor.color(72, 36, 16), TextColor.color(119, 67, 52), TextColor.color(119, 67, 52), TextColor.color(66, 29, 10), TextColor.color(142, 95, 62), TextColor.color(129, 83, 57)},
            {TextColor.color(149, 96, 63), TextColor.color(129, 83, 57), TextColor.color(66, 29, 10), TextColor.color(72, 36, 16), TextColor.color(66, 29, 10), TextColor.color(72, 36, 16), TextColor.color(129, 83, 57), TextColor.color(142, 95, 62)},
    };

    @Override
    public void onEnable() {
        // Plugin startup logic

        this.imageService = new ImageService(this);

        getCommand("test").setExecutor(this::onCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) return true;

        Optional<TextColor[][]> loaded = this.imageService.load(player);
        if (loaded.isEmpty()) {
            Bukkit.broadcast(Component.text("error occurred while loading data", NamedTextColor.RED));
            return true;
        }

        Component component = Component.empty();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                component = component.append(Component.text("" + (char) +(((int) '\uF810') + y)).style(Style.empty().color(loaded.get()[y][x]).font(Key.key("chatheads", "pixel"))))
                        .append(Component.text("\uE001").style(Style.empty().font(Key.key("chatheads", "pixel"))));
            }
            component = component.append(Component.text("\uE008").style(Style.empty().font(Key.key("chatheads", "pixel"))));
        }

        Bukkit.broadcast(component);


        return true;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}
