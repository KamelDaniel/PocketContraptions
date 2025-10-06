package io.github.kameldaniel.world.dimension;

import io.github.kameldaniel.ModData;
import io.github.kameldaniel.PocketContraptions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

import java.util.Objects;

public class PocketDimension {
    // Retrieve pocket dimension resources
    public static final RegistryKey<DimensionOptions> DIM = RegistryKey.of(RegistryKeys.DIMENSION,
            Identifier.of(PocketContraptions.MOD_ID, "pocket_dimension"));
    public static final RegistryKey<World> DIMENSION = RegistryKey.of(RegistryKeys.WORLD,
            Identifier.of(PocketContraptions.MOD_ID, DIM.getValue().getPath()));
    private static final RegistryKey<DimensionType> DIMENSION_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            Identifier.of(PocketContraptions.MOD_ID, "pocket_dimension_type"));

    /**
     * Returns the RuntimeWorldHandle on MinecraftServer server with id pocket_dimension_[dimID].
     * If the RuntimeWorldHandle does not yet exist, it is created and returned.
     * @param server
     * the MinecraftServer containing the dimension
     * @param dimID
     * the dimID of the pocket dimension (most likely from a QuantumCore)
     * @return
     * the RuntimeWorldHandle of the pocket dimension if it exists, or if it does not,
     * creates a new on and returns it.
     */
    public static ServerWorld getDim(MinecraftServer server, int dimID) {
        String dimName = "pocket_dimension_" + dimID;
        Fantasy fantasy = Fantasy.get(server);
        RuntimeWorldConfig config =  new RuntimeWorldConfig()
                .setDimensionType(DIMENSION_TYPE)
                .setMirrorOverworldDifficulty(true)
                .setMirrorOverworldGameRules(true)
                .setTimeOfDay(server.getOverworld().getTimeOfDay())
                .setGenerator(Objects.requireNonNull(server.getWorld(DIMENSION)).getChunkManager().getChunkGenerator());
        return fantasy.getOrOpenPersistentWorld(Identifier.of(PocketContraptions.MOD_ID, dimName), config).asWorld();
    }

    /**
     * Creates a ServerWorld for a new pocket dimension, and returns its associated dimID.
     * @param server
     * the MinecraftServer to create the dimension in
     * @return
     * the dimID of the dimension. The dimension tag will be pocket_dimension_[dimID].
     */
    public static int createPocketDimension(MinecraftServer server) {
        int dimID = ModData.getModData(server).getDimID();
        PocketDimension.getDim(server, dimID);
        return dimID;
    }

    public static void initialize() {
        // This allows the player to spawn in a pocket dimension if they logged off in one
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (int dimID = 0; dimID < ModData.getModData(server).nextDimID; dimID++) {
                PocketDimension.getDim(server, dimID);
            }
        });
    }
}
