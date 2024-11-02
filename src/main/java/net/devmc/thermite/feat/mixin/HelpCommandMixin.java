package net.devmc.thermite.feat.mixin;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.HelpCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(HelpCommand.class)
public abstract class HelpCommandMixin {

	@Unique
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.help.failed"));

	@Inject(at = @At("HEAD"), method = "register", cancellable = true)
	private static void addSuggestions(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo ci) {
		dispatcher.register(CommandManager.literal("help").executes((context) -> {
			Map<CommandNode<ServerCommandSource>, String> map = dispatcher.getSmartUsage(dispatcher.getRoot(), (ServerCommandSource)context.getSource());

			for(String string : map.values()) {
				context.getSource().sendFeedback(() -> Text.literal("/" + string), false);
			}

			return map.size();
		}).then(CommandManager.argument("command", StringArgumentType.greedyString()).executes((context) -> {
			ParseResults<ServerCommandSource> parseResults = dispatcher.parse(StringArgumentType.getString(context, "command"), context.getSource());
			if (parseResults.getContext().getNodes().isEmpty()) {
				throw FAILED_EXCEPTION.create();
			} else {
				Map<CommandNode<ServerCommandSource>, String> map = dispatcher.getSmartUsage(((ParsedCommandNode) Iterables.getLast(parseResults.getContext().getNodes())).getNode(), context.getSource());

				for(String string : map.values()) {
					context.getSource().sendFeedback(() -> {
						String command = parseResults.getReader().getString();
						Text description = MutableText.of(new TranslatableTextContent(
								String.format("command.%s.description", command),
								"Command has no description",
								TranslatableTextContent.EMPTY_ARGUMENTS));
						return Text.literal("/" + command + " " + string + description);
					}, false);
				}

				return map.size();
			}
		})));
		ci.cancel();
	}
}
