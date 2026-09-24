package io.github.kameldaniel.world.dimension;

import io.github.kameldaniel.ModData;
import io.github.kameldaniel.PocketContraptions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

// WORLD BORDER: -29999983, 29999983

@SuppressWarnings("unused")
public class PocketDimension {
    private static MinecraftServer server;

    // Retrieve pocket dimension resources
    public static final RegistryKey<DimensionOptions> DIM = RegistryKey.of(RegistryKeys.DIMENSION,
            Identifier.of(PocketContraptions.MOD_ID, "pocket_dimension"));
    public static final RegistryKey<World> DIMENSION = RegistryKey.of(RegistryKeys.WORLD,
            Identifier.of(PocketContraptions.MOD_ID, DIM.getValue().getPath()));
    private static final RegistryKey<DimensionType> DIMENSION_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            Identifier.of(PocketContraptions.MOD_ID, "pocket_dimension_type"));

    /**
     * Returns the RuntimeWorldHandle with id pocket_dimension_[dimKey].
     * If the RuntimeWorldHandle does not yet exist, it is created and returned.
     * @param dimKey
     * the dimKey of the pocket dimension (most likely from a QuantumCore)
     * @return
     * the RuntimeWorldHandle of the pocket dimension if it exists, or if it does not,
     * creates a new on and returns it.
     */
    public static ServerWorld getDim(int dimKey) {
        if (dimKey < 0) throw new IllegalArgumentException("dimKey must be non-negative");
        String dimName = "pocket_dimension_" + dimKey;
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
     * Builds an air cube surrounded by blocks
     * @param dim
     * the ServerWorld to build the cube in
     * @param bottomSouthWest
     * the bottom south-west corner of the cube
     *  - this block will be air because edges are not filled
     * @param state
     * the BlockState the cube should be made of
     * @param length
     * the side length of air cube contained by the generated cube
     */
    public static void buildCube(ServerWorld dim, BlockPos bottomSouthWest, BlockState state, int length) {
        BlockPos topNorthEast = bottomSouthWest.up(++length).north(length).east(length);
        bottomSouthWest.up();
        for (int x = 1; x <= 16; x++) {
            for (int z = 1; z <= 16; z++) {
                dim.setBlockState(bottomSouthWest.north(x).east(z), state);
                dim.setBlockState(bottomSouthWest.north(x).up(z), state);
                dim.setBlockState(bottomSouthWest.east(x).up(z), state);
                dim.setBlockState(topNorthEast.south(x).west(z), state);
                dim.setBlockState(topNorthEast.south(x).down(z), state);
                dim.setBlockState(topNorthEast.west(x).down(z), state);
            }
        }
    }

    /**
     * Creates a ServerWorld for a new pocket dimension, and returns its associated dimKey.
     * Makes a hollow box of Component Blocks with a floor centered around 0 64 0.
     * @param server
     * the MinecraftServer to create the dimension in
     * @return
     * the dimKey of the dimension. The dimension tag will be pocket_dimension_[dimKey].
     */
    public static int createPocketDimension(MinecraftServer server) {
        return ModData.getModData(server).getDimKey();
    }

    /**
     * Teleports entity to 0 66 0 in pocket_dimension_[dimKey]
     * @param entity
     * the entity to teleport
     * @param dimID
     * the dimension id of the pocket dimension to teleport the entity to
     */
    public static void tpEntity(Entity entity, int dimID) {
        Set<PositionFlag> flags = Collections.emptySet();
        entity.teleport(PocketDimension.getDim(dimID),
                0, 66, 0, flags, entity.getYaw(), entity.getPitch(), false);
        if (entity instanceof PlayerEntity player)
            player.sendMessage(Text.literal("Welcome to Pocket Dimension " + dimID), false);
    }

    public static void initialize() {
        // This allows the entity to spawn in a pocket dimension if they logged off in one
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PocketDimension.server = server;
            for (int dimID = 0; dimID < ModData.getModData(server).nextDimKey; dimID++) {
                PocketDimension.getDim(dimID);
            }
        });
    }
}
