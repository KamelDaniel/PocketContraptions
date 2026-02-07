package io.github.kameldaniel.screenhandler;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.payload.BlockPosPayload;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static final ScreenHandlerType<BlueprintBuilderScreenHandler> BLUEPRINT_BUILDER =
            register("blueprint_builder_screen_handler", BlueprintBuilderScreenHandler::new, BlockPosPayload.PACKET_CODEC);

    public static <T extends ScreenHandler, U extends CustomPayload> ExtendedScreenHandlerType<T, U>
        register(String name, ExtendedScreenHandlerType.ExtendedFactory<T, U> screenHandlerConstructor,
                 PacketCodec<? super RegistryByteBuf, U> codec) {
        return Registry.register(Registries.SCREEN_HANDLER, PocketContraptions.id(name), new ExtendedScreenHandlerType<>(screenHandlerConstructor, codec));
    }

    public static void initialize() {}
}
