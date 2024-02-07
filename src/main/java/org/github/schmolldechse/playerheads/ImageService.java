package org.github.schmolldechse.playerheads;

import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

public class ImageService {

    @Inject
    private Playerheads plugin;

    ImageService(Playerheads plugin) {
        this.plugin = plugin;
    }

    private final TextColor[][] DEFAULT_HEAD_TEXTURE = new TextColor[][] {
            { TextColor.color(50,37,17), TextColor.color(50,37,17), TextColor.color(62,42,20), TextColor.color(62,42,20), TextColor.color(62,42,20), TextColor.color(62,42,20), TextColor.color(50,37,17), TextColor.color(42,30,12) },
            { TextColor.color(37,25,8), TextColor.color(50,37,17), TextColor.color(50,37,17), TextColor.color(62,42,20), TextColor.color(62,42,20), TextColor.color(50,37,17), TextColor.color(62,42,20), TextColor.color(50,37,17) },
            { TextColor.color(42,30,12), TextColor.color(155,98,72), TextColor.color(179,121,94), TextColor.color(182,130,107), TextColor.color(179,121,94), TextColor.color(170,115,88), TextColor.color(155,98,72), TextColor.color(53,36,18) },
            { TextColor.color(155,98,72), TextColor.color(170,115,88), TextColor.color(179,121,94), TextColor.color(179,121,94), TextColor.color(170,115,88), TextColor.color(170,115,88), TextColor.color(170,115,88), TextColor.color(155,98,72) },
            { TextColor.color(170,115,88), TextColor.color(255,254,254), TextColor.color(83,60,137), TextColor.color(170,115,88), TextColor.color(155,98,72), TextColor.color(83,60,137), TextColor.color(255,254,254), TextColor.color(170,115,88) },
            { TextColor.color(155,98,72), TextColor.color(170,115,88), TextColor.color(170,115,88), TextColor.color(106,65,49), TextColor.color(106,65,49), TextColor.color(170,115,88), TextColor.color(170,115,88), TextColor.color(155,98,72) },
            { TextColor.color(144,88,62), TextColor.color(142,95,62), TextColor.color(72,36,16), TextColor.color(119,67,52), TextColor.color(119,67,52), TextColor.color(66,29,10), TextColor.color(142,95,62), TextColor.color(129,83,57) },
            { TextColor.color(149,96,63), TextColor.color(129,83,57), TextColor.color(66,29,10), TextColor.color(72,36,16), TextColor.color(66,29,10), TextColor.color(72,36,16), TextColor.color(129,83,57), TextColor.color(142,95,62) },
    };

    public Optional<TextColor[][]> load(Player player) {
        final GameProfile gameProfile = ((CraftPlayer) player).getProfile();
        if (gameProfile == null) return Optional.of(DEFAULT_HEAD_TEXTURE);

        Collection<Property> property = gameProfile.getProperties().get("textures");
        if (property.isEmpty()) return Optional.of(DEFAULT_HEAD_TEXTURE);

        String texture = property.iterator().next().value();
        String url = JsonParser.parseString(new String(Base64.getDecoder().decode(texture)))
                .getAsJsonObject()
                .getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url").getAsString();

        final BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new URL(url));
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to load image for " + player.getName() + " | " + player.getUniqueId());
            return Optional.of(DEFAULT_HEAD_TEXTURE);
        }

        final TextColor[][] textColor = new TextColor[8][8];
        for (int x = 8; x < 16; x++) {
            for (int y = 8; y < 16; y++) {
                int rgb = bufferedImage.getRGB(x, y);
                textColor[y - 8][x - 8] = TextColor.color(rgb & 0xffffff);
            }
        }

        return Optional.of(textColor);
    }
}
