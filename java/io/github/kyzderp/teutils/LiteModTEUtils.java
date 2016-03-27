package io.github.kyzderp.teutils;

import io.github.kyzderp.teutils.loginscript.ScriptHolder;
import io.github.kyzderp.teutils.task.GetServerTask;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.ChatListener;
import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.OutboundChatFilter;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;

public class LiteModTEUtils implements OutboundChatFilter, ChatListener, JoinGameListener, Tickable
{
	private static KeyBinding configKeyBinding;

	private CommandHandler cmdHandler;
	private TEUtils util;
	public static ScheduledExecutorService scheduler;

	private boolean schedulerActive;

	@Override
	public String getName() { return "TE Utils"; }

	@Override
	public String getVersion() { return "1.0.0"; }

	@Override
	public void init(File configPath) 
	{
		this.util = new TEUtils(this, new ScriptHolder());
		this.cmdHandler = new CommandHandler(this, this.util);
		this.scheduler = Executors.newScheduledThreadPool(1);
		this.schedulerActive = false;
		this.configKeyBinding = new KeyBinding("key.teutils.config", Keyboard.CHAR_NONE, "key.categories.litemods");
		LiteLoader.getInput().registerKeyBinding(this.configKeyBinding);
	}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

	/**
	 * Client-side command handling
	 */
	@Override
	public boolean onSendChatMessage(String message) 
	{
		boolean result = this.cmdHandler.handleCommand(message);
		return result;
	}

	/**
	 * Chat listener
	 */
	@Override
	public void onChat(IChatComponent chat, String message) 
	{
		message = message.replaceAll("\u00A7.", "");
		if (message.equals("[!] Success [!] Welcome back, your login session has been resumed.")
				|| message.equals("                  Welcome Back To TeamExtreme. "))
		{
			// Schedule to run this half a sec later, or it may be inaccurate.
			if (this.schedulerActive)
				this.scheduler.schedule(new GetServerTask(this, this.util), 1000, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Upon first login, because it needs a longer delay.
	 */
	@Override
	public void onJoinGame(INetHandler netHandler,
			S01PacketJoinGame joinGamePacket, ServerData serverData,
			RealmsServer realmsServer) {
		this.schedulerActive = false;
		this.scheduler.schedule(new GetServerTask(this, this.util), 3000, TimeUnit.MILLISECONDS);
		// TODO: if not loaded, run again?
	}


	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame,
			boolean clock) 
	{
		if (inGame && minecraft.currentScreen == null && this.configKeyBinding.isPressed())
		{
			this.util.openConfig();
		}
	}

	/**
	 * Logs the message to the user
	 * @param message The message to log
	 * @param addPrefix Whether to add the mod-specific prefix or not
	 */
	public void logMessage(String message, boolean addPrefix)
	{
		if (addPrefix)
			message = "§8[§2TE Utils§8] §a" + message;
		ChatComponentText displayMessage = new ChatComponentText(message);
		displayMessage.setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.GREEN));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(displayMessage);
	}

	/**
	 * Logs the error message to the user
	 * @param message The error message to log
	 */
	public void logError(String message)
	{
		ChatComponentText displayMessage = new ChatComponentText("§8[§4!§8] §c" + message + " §8[§4!§8]");
		displayMessage.setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.RED));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(displayMessage);
	}

	public void setSchedulerActive(boolean active) 
	{
		this.schedulerActive = active;
	}
}