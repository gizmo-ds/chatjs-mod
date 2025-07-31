package dev.aika.chatjs.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.aika.chatjs.ChatJS;
import dev.aika.chatjs.ChatJSUtil;
import dev.aika.chatjs.api.OpenAIClient;
import dev.aika.chatjs.api.resources.ModelObject;
import dev.aika.chatjs.kubejs.OpenAIWrapper;
import dev.aika.chatjs.server.SecretManager;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

public class ChatJSCommands {
    private static final Logger log = ChatJS.LOGGER;
    private static final Marker marker = MarkerFactory.getMarker("ChatJSCommands");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        Predicate<CommandSourceStack> op = (source) -> source.getServer().isSingleplayer() || source.hasPermission(2);
        dispatcher.register(Commands.literal("chatjs")
                .then(Commands.literal("reload").executes(ChatJSCommands::reload))
                .then(Commands.literal("set_apikey")
                        .requires(op)
                        .then(Commands
                                .argument("apikey", StringArgumentType.string())
                                .executes(ChatJSCommands::setApiKey)
                        )
                )
                .then(Commands.literal("models").requires(op).executes(ChatJSCommands::models))
        );
    }

    private static int reload(CommandContext<CommandSourceStack> ctx) {
        try {
            SecretManager.INSTANCE.load();
            ChatJS.CONFIG.load();
            OpenAIWrapper.setProvider(ChatJSUtil.getProvider());
        } catch (IOException e) {
            log.error(marker, "Failed to load secret file", e);
            throw new CommandRuntimeException(Component.translatable("command.chatjs.msg.failed", e.getMessage()));
        }
        ctx.getSource().sendSystemMessage(Component.translatable("command.chatjs.msg.execution_success"));
        return Command.SINGLE_SUCCESS;
    }

    private static int setApiKey(CommandContext<CommandSourceStack> ctx) {
        String apikey = StringArgumentType.getString(ctx, "apikey");
        SecretManager.INSTANCE.setProperty("apikey", apikey);
        OpenAIWrapper.setApiKey(apikey);
        ctx.getSource().sendSystemMessage(Component.translatable("command.chatjs.msg.execution_success"));
        return Command.SINGLE_SUCCESS;
    }

    private static int models(CommandContext<CommandSourceStack> ctx) {
        OpenAIClient client = new OpenAIClient().setProvider(ChatJSUtil.getProvider()).setApiKey(ChatJSUtil.getApiKey());

        List<ModelObject> result;
        try {
            result = client.models();
        } catch (Exception e) {
            throw new CommandRuntimeException(Component.translatable("command.chatjs.msg.failed", e.getMessage()));
        }

        StringBuilder sb = new StringBuilder();
        for (ModelObject m : result) {
            sb.append("   > ").append(m.id).append("\n");
        }
        ctx.getSource().sendSystemMessage(Component.translatable("command.chatjs.msg.models", sb.substring(0, sb.toString().length() - 1)));
        return Command.SINGLE_SUCCESS;
    }
}
