package me.strange.totpAuth2;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TotpAuth2 implements ModInitializer {

    private final Set<UUID> unauthenticatedPlayers = new HashSet<>();

    @Override
    public void onInitialize() {
        // Register login event listener
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            System.out.println("Player logged in: " + player.getName().getString());
            // Add to unauthenticated list and prompt for auth
            unauthenticatedPlayers.add(player.getUuid());
            // TODO: send auth prompt to player
        });

        // Register player tick event to block movement if unauthenticated
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (unauthenticatedPlayers.contains(player.getUuid())) {
                    player.requestTeleport(player.getX(), player.getY(), player.getZ());


                    // remind player
                }
            }
        });

    }
    public void onPlayerAuthenticated(ServerPlayerEntity player) {
        unauthenticatedPlayers.remove(player.getUuid());
        System.out.println("Player authenticated: " + player.getName().getString());
    }
}
