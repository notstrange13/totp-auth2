package me.strange.totpAuth2;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class AuthCommand {

    private final TotpAuth2 modInstance;

    public AuthCommand(TotpAuth2 modInstance) {
        this.modInstance = modInstance;
    }

    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("auth")
                            .then(CommandManager.argument("code", StringArgumentType.word())
                                    .executes(context -> {
                                        String code = StringArgumentType.getString(context, "code");
                                        ServerPlayerEntity player = context.getSource().getPlayer();

                                        if (player == null) {
                                            context.getSource().sendFeedback(() -> Text.literal("Command can only be used by a player."), false);

                                            return 1;
                                        }

                                        boolean success = modInstance.verifyCodeAndAuthenticate(player, code);
                                        if (success) {
                                            context.getSource().sendFeedback(() -> Text.literal("Authentication successful! You can now play."), false);
                                        } else {
                                            context.getSource().sendFeedback(() ->Text.literal("Invalid authentication code, please try again."), false);
                                        }

                                        return 1;
                                    }))


            );
        });
    }

}
