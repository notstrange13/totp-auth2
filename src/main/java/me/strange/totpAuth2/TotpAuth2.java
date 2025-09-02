package me.strange.totpAuth2;

import net.fabricmc.api.ModInitializer;         //creating problems here
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TotpAuth2 implements ModInitializer {          //creating problems here

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
    public boolean verifyCodeAndAuthenticate(ServerPlayerEntity player, String submittedCode) {
        String secret = getPlayerSecret(player.getUuid());  // Retrieve secret from wherever you store it
        if (secret == null) {
            player.sendMessage(net.minecraft.text.Text.literal("You are not registered. Contact an admin."), false);
            return false;
        }

        boolean valid = verifyTotpCode(secret, submittedCode);  // Verify TOTP code with library

        if (valid) {
            unauthenticatedPlayers.remove(player.getUuid());
            System.out.println("Player authenticated: " + player.getName().getString());
        }

        return valid;
    }

    // Example stub to get player's secret - replace with real DB or file lookup
    private String getPlayerSecret(UUID uuid) {
        // TODO: Lookup secret for uuid here
        return "JBSWY3DPEHPK3PXP";  // Example static secret for testing
    }

    // Example stub for TOTP verification using your TOTP library
    private boolean verifyTotpCode(String secret, String code) {
        // TODO: Use dev.samstevens.totp or similar library for actual verification
        return false;  // Placeholder always false
    }
}
