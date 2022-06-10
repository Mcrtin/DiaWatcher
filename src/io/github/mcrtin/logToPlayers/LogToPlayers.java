package io.github.mcrtin.logToPlayers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LogToPlayers {
	public static final List<CommandSender> SEND_FEEDBACK = new ArrayList<>();

	public static void log(String message) {
		SEND_FEEDBACK.forEach(sender -> sender.sendMessage(message));
	}

	public static void info(String message, Object... args) {
		String[] split = message.split(Pattern.quote("{}"), args.length + 1);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < split.length; i++) {
			sb.append(split[i]);
			if (i != args.length)
				sb.append(args[i]);
		}
		log(sb.toString());
		log.info(message, args);
	}
}
