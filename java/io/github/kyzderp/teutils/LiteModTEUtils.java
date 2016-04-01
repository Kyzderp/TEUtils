package io.github.kyzderp.teutils;

import io.github.kyzderp.teutils.loginscript.ScriptHolder;
import io.netty.buffer.Unpooled;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
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
import com.mumfrey.liteloader.PluginChannelListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.ClientPluginChannels;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.PluginChannels;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

public class LiteModTEUtils implements OutboundChatFilter, ChatListener, JoinGameListener, Tickable, PluginChannelListener
{
	private static KeyBinding configKeyBinding;
	private static KeyBinding worldbackKeyBinding;
	
	private boolean isTE;

	private CommandHandler cmdHandler;
	private TEUtils util;
	public static ScheduledExecutorService scheduler;

	private boolean schedulerActive;

	@Override
	public String getName() { return "TE Utils"; }

	@Override
	public String getVersion() { return "1.1.1"; }

	@Override
	public void init(File configPath) 
	{
		this.util = new TEUtils(this, new ScriptHolder());
		this.isTE = false;
		this.cmdHandler = new CommandHandler(this, this.util);
		this.scheduler = Executors.newScheduledThreadPool(1);
		this.schedulerActive = false;
		this.configKeyBinding = new KeyBinding("key.teutils.config", Keyboard.CHAR_NONE, "key.categories.litemods");
		this.worldbackKeyBinding = new KeyBinding("key.teutils.worldback", Keyboard.CHAR_NONE, "key.categories.litemods");
		LiteLoader.getInput().registerKeyBinding(this.configKeyBinding);
		LiteLoader.getInput().registerKeyBinding(this.worldbackKeyBinding);
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
		if (this.isTE
				&& (message.equals("[!] Success [!] Welcome back, your login session has been resumed.")
				|| message.equals("                  Welcome Back To TeamExtreme. ")))
		{
			PacketBuffer outPacket = new PacketBuffer(Unpooled.copiedBuffer(new byte[0]));
			ClientPluginChannels.sendMessage("world_info", outPacket, PluginChannels.ChannelPolicy.DISPATCH_ALWAYS);
		} // TODO: /teu reload
	}

	/**
	 * Upon first login, because it needs a longer delay. Client may still be loading
	 */
	@Override
	public void onJoinGame(INetHandler netHandler, S01PacketJoinGame joinGamePacket, 
			ServerData serverData, RealmsServer realmsServer) 
	{
//		PacketBuffer outPacket = new PacketBuffer(Unpooled.copiedBuffer(new byte[0]));
//		ClientPluginChannels.sendMessage("world_info", outPacket, PluginChannels.ChannelPolicy.DISPATCH_ALWAYS);
		this.isTE = false;
		if (serverData.serverMOTD.split("\n")[0].matches("§8\\[§aBitches§8\\]§7=§8\\[§aBe§8\\]§7=§8\\[§aCrazy.*"))
			this.isTE = true;
	}


	/**
	 * Key bindings
	 */
	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) 
	{
		if (inGame && minecraft.currentScreen == null && this.configKeyBinding.isPressed())
			this.util.openConfig();
		else if (inGame && minecraft.currentScreen == null && this.worldbackKeyBinding.isPressed())
			this.cmdHandler.handleCommand("/worldback");
	}

	/**
	 * Channels for which to receive messages
	 */
	@Override
	public List<String> getChannels() 
	{
		return Arrays.asList(new String[] { "world_info", "world_id" });
	}

	/**
	 * On receiving plugin message, check server
	 */
	@Override
	public void onCustomPayload(String channel, PacketBuffer data) 
	{
		if (!this.isTE)
			return;
		if (channel.equals("world_info") || channel.equals("world_id"))
		{
			String world = new String(data.array(), StandardCharsets.UTF_8);
			String server = this.util.convertWorldToServer(world.trim());
			
			if (!this.util.getCurrentServer().equals(server))
			{ // We've got a new world!
				this.util.setLastServer(this.util.getCurrentServer());
				this.util.setCurrentServer(server);
				LiteLoaderLogger.info("Current world is now \"" + server + "\" and last world is \""
						+ this.util.getLastServer() + "\"");
				
				this.util.runScript(server);
			}
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