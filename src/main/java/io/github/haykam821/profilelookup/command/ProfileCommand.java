package io.github.haykam821.profilelookup.command;

import java.util.Collection;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ProfileCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		ProfileCommand.registerProfileSubcommand("uuid", dispatcher, ProfileCommand::executeUuid);
		ProfileCommand.registerProfileSubcommand("operator", dispatcher, ProfileCommand::executeOperator);
		ProfileCommand.registerProfileSubcommand("whitelisted", dispatcher, ProfileCommand::executeWhitelisted);
	}

	private static void registerProfileSubcommand(String literal, CommandDispatcher<ServerCommandSource> dispatcher, ProfileSubcommand command) {
		dispatcher.register(CommandManager.literal("profile")
			.requires(source -> {
				return source.hasPermissionLevel(2);
			})
			.then(CommandManager.literal(literal)
			.then(CommandManager.argument("profiles", GameProfileArgumentType.gameProfile())
			.executes(context -> {
				Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "profiles");
				for (GameProfile profile : profiles) {
					command.run(profile, context);
				}

				return profiles.size();
			}))));
	}

	private static Text getCopyToClipboardText(String string) {
		return new LiteralText(string).styled(style -> {
			return style
				.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, string))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy.click")))
				.withInsertion(string);
		});
	}

	private static void executeUuid(GameProfile profile, CommandContext<ServerCommandSource> context) {
		Text copyToClipboardText = ProfileCommand.getCopyToClipboardText(profile.getId().toString());
		Text feedback = new TranslatableText("command.profilelookup.profile.uuid.success", profile.getName(), copyToClipboardText);
		context.getSource().sendFeedback(feedback, false);
	}

	private static void executeOperator(GameProfile profile, CommandContext<ServerCommandSource> context) {
		boolean operator = context.getSource().getMinecraftServer().getPlayerManager().isOperator(profile);
		String suffix = operator ? "operator" : "not_operator";

		Text feedback = new TranslatableText("command.profilelookup.profile.operator." + suffix, profile.getName());
		context.getSource().sendFeedback(feedback, false);
	}

	private static void executeWhitelisted(GameProfile profile, CommandContext<ServerCommandSource> context) {
		boolean whitelisted = context.getSource().getMinecraftServer().getPlayerManager().isWhitelisted(profile);
		String suffix = whitelisted ? "whitelisted" : "not_whitelisted";

		Text feedback = new TranslatableText("command.profilelookup.profile.whitelisted." + suffix, profile.getName());
		context.getSource().sendFeedback(feedback, false);
	}

	private static interface ProfileSubcommand {
		public void run(GameProfile profile, CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
	}
}
