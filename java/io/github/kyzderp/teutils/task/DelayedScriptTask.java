package io.github.kyzderp.teutils.task;

import io.github.kyzderp.teutils.LiteModTEUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;

public class DelayedScriptTask implements Runnable 
{
	private List<String> chatLines;
	private long delay;

	public DelayedScriptTask(List<String> chatLines, long delay)
	{
		this.chatLines = chatLines;
		this.delay = delay;
	}

	@Override
	public void run() 
	{
		String chat = this.chatLines.remove(0);
		Minecraft.getMinecraft().thePlayer.sendChatMessage(chat);

		if (!this.chatLines.isEmpty())
			LiteModTEUtils.scheduler.schedule(new DelayedScriptTask(this.chatLines, this.delay),
					this.delay, TimeUnit.MILLISECONDS);
	}
}
