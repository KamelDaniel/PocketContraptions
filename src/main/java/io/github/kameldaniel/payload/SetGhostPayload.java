package io.github.kameldaniel.payload;

import io.github.kameldaniel.PocketContraptions;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetGhostPayload(boolean isGhost) implements CustomPayload {
    public static final Id<SetGhostPayload> ID = new Id<>(PocketContraptions.id("set_ghost"));

    public static final PacketCodec<RegistryByteBuf, SetGhostPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN,
            SetGhostPayload::isGhost,
            SetGhostPayload::new
    );

    @Override
    public Id<SetGhostPayload> getId() {
        return ID;
    }
}
