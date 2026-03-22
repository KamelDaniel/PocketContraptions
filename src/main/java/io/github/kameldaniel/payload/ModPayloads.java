package io.github.kameldaniel.payload;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModPayloads {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(SetGhostPayload.ID, SetGhostPayload.CODEC);
    }
}
