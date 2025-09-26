package io.github.kameldaniel;

import io.github.kameldaniel.block.ModBlocks;
import io.github.kameldaniel.item.ModItems;
import io.github.kameldaniel.world.dimension.PocketDimension;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class PocketContraptions implements ModInitializer {
    private int nextDimID = 0;

	public static final String MOD_ID = "pocket-contraptions";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
        ModItems.initialize();
        ModBlocks.initialize();
        PocketDimension.initialize();
		LOGGER.info("Hello Fabric world!");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("createPocketDimension").executes(context -> {
                try {
                    LOGGER.info("Command Received");
                    ServerWorld newDim = PocketDimension.createPocketDimension(context.getSource().getServer(), "pocket_dimension_" + nextDimID++);
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    assert player != null;
                    LOGGER.info("[Before Teleport] World: {}, Pos: {}",
                            player.getWorld().getDimension(), player.getPos());
//                    ServerWorld dim = player.getServer().getWorld(PocketDimension.newestDim);
                    Set<PositionFlag> flags = Collections.emptySet();
                    player.teleport(newDim, 0, 68, 0, flags, 0.0f, 0.0f, false);
                    LOGGER.info("[After Teleport] World: {}, Pos: {}",
                            player.getWorld().getDimension(), player.getPos());
                    return 1;
                } catch(Exception e) {
                    LOGGER.info("Exception caught!!");
                    LOGGER.info("Message: {}", e.getMessage());
                    LOGGER.info("Stack Trace:");
                    for (StackTraceElement line : e.getStackTrace()) {
                        LOGGER.info(line.toString());
                    }
                    return 0;
                }
            }));
        });
	}
}