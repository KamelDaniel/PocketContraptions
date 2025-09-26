package io.github.kameldaniel.world.dimension;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.ModBlocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

public class PocketDimension {
    public static final Logger LOGGER = LoggerFactory.getLogger(PocketContraptions.MOD_ID);

    public static final RegistryKey<DimensionOptions> DIM = RegistryKey.of(RegistryKeys.DIMENSION,
            Identifier.of(PocketContraptions.MOD_ID, "pocket_dimension"));
    public static final RegistryKey<World> DIMENSION = RegistryKey.of(RegistryKeys.WORLD,
            Identifier.of(PocketContraptions.MOD_ID, DIM.getValue().getPath()));
    private static final RegistryKey<DimensionType> DIMENSION_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            Identifier.of(PocketContraptions.MOD_ID, "pocket_dimension_type"));
    private static final RegistryKey<Biome> BIOME = RegistryKey.of(RegistryKeys.BIOME,
            Identifier.of(PocketContraptions.MOD_ID, "pocket_dimension_biome"));

    public static void initialize() {
    }

    public static ServerWorld createPocketDimension(MinecraftServer server, String dimID) {
        Fantasy fantasy = Fantasy.get(server);

        RuntimeWorldConfig config = new RuntimeWorldConfig()
                .setDimensionType(DIMENSION_TYPE)
//                .setDifficulty(Difficulty.HARD)
                .setGameRule(GameRules.DO_DAYLIGHT_CYCLE, true)
                .setTimeOfDay(server.getOverworld().getTimeOfDay())
                .setGenerator(server.getWorld(DIMENSION).getChunkManager().getChunkGenerator());
//                .setSeed(server.getOverworld().getSeed());

        RuntimeWorldHandle worldHandle = fantasy.getOrOpenPersistentWorld(Identifier.of(PocketContraptions.MOD_ID, dimID), config);
        ServerWorld world = worldHandle.asWorld();
        world.setBlockState(BlockPos.ORIGIN.up(83), ModBlocks.COMPONENT_BLOCK.getDefaultState());
        LOGGER.info("Origin: {}", BlockPos.ORIGIN);
        return world;
    }
}
