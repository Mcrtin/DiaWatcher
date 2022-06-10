package io.github.mcrtin.logToPlayers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class DiaFeedback implements TabExecutor {

	private static final String TRUE = "true";
	private static final String FALSE = "false";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (args.length) {
		case 0:
			sender.sendMessage("sendDiaFeedback: " + LogToPlayers.SEND_FEEDBACK.contains(sender));
			return true;
		case 1:
			switch (args[0]) {
			case TRUE:
				LogToPlayers.SEND_FEEDBACK.add(sender);
				sender.sendMessage("set sendDiaFeedback to true");
				return true;
			case FALSE:
				LogToPlayers.SEND_FEEDBACK.remove(sender);
				sender.sendMessage("set sendDiaFeedback to false");
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> complete = new ArrayList<>();
		if (args.length != 1)
			return complete;
		final String prefix = args[0].toLowerCase();
		if (TRUE.startsWith(prefix))
			complete.add(TRUE);
		else if (FALSE.startsWith(prefix))
			complete.add(FALSE);
		return complete;

	}

}
