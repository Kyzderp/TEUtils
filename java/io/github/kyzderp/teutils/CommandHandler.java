package io.github.kyzderp.teutils;

import io.github.kyzderp.teutils.task.OpenConfigTask;

import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;

public class CommandHandler 
{
	private LiteModTEUtils mod;
	private TEUtils util;

	public CommandHandler(LiteModTEUtils mod, TEUtils util)
	{
		this.mod = mod;
		this.util = util;
	}

	public boolean handleCommand(String message)
	{
		String[] tokens = message.split(" ");
		
		if (tokens[0].equalsIgnoreCase("/worldback"))
		{
			if (this.util.getLastServer() == null || this.util.getLastServer().equals(""))
				this.mod.logError("You don't have a last world recorded!");
			else
				Minecraft.getMinecraft().thePlayer.sendChatMessage("/deltaessentials:moveto " 
						+ this.util.getLastServer());
			return false;
		} // worldback

		if (!tokens[0].equalsIgnoreCase("/teu") && !tokens[0].equalsIgnoreCase("/teutils"))
			return true; // Not a TE Utils command


		/////////////////// TE UTILS MAIN COMMAND ///////////////////

		if (tokens.length == 1)
		{
			this.mod.logMessage("§2" + this.mod.getName() + " §8[§2v" + this.mod.getVersion() + "§8] §aby Kyzeragon", false);
			this.mod.logMessage("Type §2/teutils help §aor §2/teu help §afor commands.", false);
			return false;
		} // teu

		else if (tokens[1].equalsIgnoreCase("help"))
		{
			this.mod.logMessage("§2" + this.mod.getName() + " §8[§2v" + this.mod.getVersion() + "§8] §acommands (alias /channelfilter)", false);
			String[] commands = {"worldback §7- §aSees the list of currently ignored factions.",
					"teu script §7- §aOpens the config screen for login macros.",
			"teu help §7- §aDisplays this help message. Herpaderp."};
			for (String command: commands)
				this.mod.logMessage("/" + command, false);
			return false;
		} // teu help
		
		else if (tokens[1].equalsIgnoreCase("script"))
		{
			this.mod.scheduler.schedule(new OpenConfigTask(this.mod, this.util), 100, TimeUnit.MILLISECONDS);
//			Minecraft.getMinecraft().displayGuiScreen(new ScriptConfigScreen(this.util.getScriptHolder()));
			return false;
		} // teu script

		this.mod.logMessage("§2" + this.mod.getName() + " §8[§2v" + this.mod.getVersion() + "§8] §aby Kyzeragon", false);
		this.mod.logMessage("Type §2/teutils help §aor §2/teu help §afor commands.", false);
		return false;
	}
}
