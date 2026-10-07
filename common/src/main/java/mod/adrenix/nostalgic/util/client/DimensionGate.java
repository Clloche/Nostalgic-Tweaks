package mod.adrenix.nostalgic.util.client;

import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Restricts Nostalgic Tweaks to a list of dimensions read from config/nostalgic_tweaks_dimensions.txt
 * (one dimension id per line, e.g. "beta173dimension:beta"). When the list is empty, the mod is active everywhere.
 */
public final class DimensionGate
{
    private static final Path FILE = Platform.getConfigFolder().resolve("nostalgic_tweaks_dimensions.txt");
    private static Set<String> allowed;

    private DimensionGate()
    {
    }

    private static Set<String> load()
    {
        Set<String> ids = new HashSet<>();

        try
        {
            if (Files.notExists(FILE))
            {
                Files.write(FILE, List.of(
                    "# Nostalgic Tweaks will only be active in the dimensions listed below (one id per line).",
                    "# Use F3 in game to see the current dimension id, e.g. minecraft:overworld.",
                    "# If no dimension is listed, Nostalgic Tweaks is active everywhere."
                ));
            }

            for (String line : Files.readAllLines(FILE))
            {
                String id = line.trim();

                if (!id.isEmpty() && !id.startsWith("#"))
                    ids.add(id);
            }
        }
        catch (IOException ignored)
        {
        }

        return ids;
    }

    /**
     * @return Whether tweaks should be active in the dimension the local player is currently in.
     */
    public static boolean isAllowed()
    {
        if (allowed == null)
            allowed = load();

        if (allowed.isEmpty())
            return true;

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft == null || minecraft.level == null)
            return false;

        return allowed.contains(minecraft.level.dimension().location().toString());
    }
}
