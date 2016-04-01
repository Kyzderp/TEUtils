package io.github.kyzderp.teutils;

import io.github.kyzderp.teutils.loginscript.LoginScript;
import io.github.kyzderp.teutils.loginscript.ScriptConfigScreen;
import io.github.kyzderp.teutils.loginscript.ScriptHolder;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

public class TEUtils 
{
	private LiteModTEUtils mod;

	private ScriptHolder scriptHolder;
	private ScriptConfigScreen configScreen;

	private String lastServer;
	private String currentServer;

	public TEUtils(LiteModTEUtils mod, ScriptHolder scriptHolder)
	{
		this.mod = mod;
		this.lastServer = "";
		this.currentServer = "";
		this.scriptHolder = scriptHolder;
		this.scriptHolder.loadScripts();
		this.scriptHolder.saveScripts();

		this.configScreen = new ScriptConfigScreen(this.scriptHolder);
	}

	/////////////////// UTILS //////////////////////

	/**
	 * Converts world name to the server name
	 * @param world
	 * @return
	 */
	public String convertWorldToServer(String world)
	{
		world = world.toLowerCase();
		if (world.equals("mecha"))
			return "build4";
		return world;
	}
	
	/**
	 * Get the server that the player is currently on, using the tablist
	 * @return
	 */
	public String getServer()
	{
		Collection playerInfo = Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap();
		for (Object obj: playerInfo)
		{
			if (obj instanceof NetworkPlayerInfo)
			{
				NetworkPlayerInfo info = (NetworkPlayerInfo)obj;
				if (info != null && info.getDisplayName() != null)
				{
					String text = info.getDisplayName().getFormattedText();
					if (text.matches("§r§f §r§6[A-Za-z0-9]+§r"))
						return info.getDisplayName().getUnformattedText().toLowerCase().trim();
				}
			}
		}
		return "";
	}

	/**
	 * Run the login script
	 * @param servername
	 */
	public void runScript(String servername)
	{
		if (servername == "")
			return;
		LoginScript s = this.scriptHolder.scripts.get(servername);
		if (s != null)
		{
			this.mod.logMessage("Running " + Minecraft.getMinecraft().getSession().getUsername()
					+ "'s login script for \u00A72" + servername + "\u00A7a...", true);
			s.execute();
		}
	}

	public void openConfig()
	{
		Minecraft.getMinecraft().displayGuiScreen(this.configScreen);
	}

	/////////////////// GETTERS AND SETTERS ////////////////////

	public String getLastServer() {
		return lastServer;
	}

	public void setLastServer(String lastServer) {
		this.lastServer = lastServer;
	}

	public String getCurrentServer() {
		return currentServer;
	}

	public void setCurrentServer(String currentServer) {
		this.currentServer = currentServer;
	}

	public ScriptHolder getScriptHolder() {
		return this.scriptHolder;
	}
}
