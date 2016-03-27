package io.github.kyzderp.teutils.loginscript;

import io.github.kyzderp.teutils.LiteModTEUtils;
import io.github.kyzderp.teutils.task.DelayedScriptTask;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;

public class LoginScript 
{
	private String servername;
	private String chatLines;
	private int delayMilliseconds;
	
	public LoginScript(String name)
	{
		this.servername = name;
		this.chatLines = "";
		this.delayMilliseconds = 0;
	}
	
	public LoginScript(String name, String chatLines, int delay)
	{
		this.servername = name;
		this.chatLines = chatLines;
		this.delayMilliseconds = delay;
	}
	
	public void execute()
	{
		if (this.chatLines.equals(""))
			return;
		
		String[] lines = this.chatLines.split("\\|");
		
		// No delay, send them all at once.
		if (this.delayMilliseconds == 0)
		{
			for (String line: lines)
				Minecraft.getMinecraft().thePlayer.sendChatMessage(line);
			return;
		}
		
		List<String> linesList = new LinkedList<String>();
		for (int i = 0; i < lines.length; i++)
			linesList.add(lines[i]);
		
		LiteModTEUtils.scheduler.schedule(new DelayedScriptTask(linesList, this.delayMilliseconds), 
				1, TimeUnit.MILLISECONDS);
	}
	
	////////////////////////////// GETTERS AND SETTERS /////////////////////////
	
	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}

	public String getChatLines() {
		return chatLines;
	}

	public void setChatLines(String chatLines) {
		this.chatLines = chatLines;
	}

	public int getDelayMilliseconds() {
		return delayMilliseconds;
	}

	public void setDelayMilliseconds(int delayMilliseconds) {
		this.delayMilliseconds = delayMilliseconds;
	}
}
