package io.github.kameldaniel.screenhandler;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.block.BlueprintBuildingTable;
import io.github.kameldaniel.payload.BlockPosPayload;
import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static final ScreenHandlerType<BlueprintBuildingTableScreenHandler> BLUEPRINT_BUILDING_TABLE =
            register("blueprint_building_table", BlueprintBuildingTableScreenHandler::new);
    public static final ScreenHandlerType<BlueprintBuilderScreenHandler> BLUEPRINT_BUILDER =
            register("blueprint_builder_screen_handler", BlueprintBuilderScreenHandler::new, BlockPosPayload.PACKET_CODEC);

    public static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, PocketContraptions.id(name), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static <T extends ScreenHandler, U extends CustomPayload> ExtendedScreenHandlerType<T, U>
        register(String name,
                 ExtendedScreenHandlerType.ExtendedFactory<T, U> screenHandlerConstructor,
                 PacketCodec<? super RegistryByteBuf, U> codec) {
        return Registry.register(Registries.SCREEN_HANDLER,
                PocketContraptions.id(name),
                new ExtendedScreenHandlerType<>(screenHandlerConstructor, codec));
    }

    public static void initialize() {}
}
