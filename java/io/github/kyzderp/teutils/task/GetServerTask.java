package io.github.kyzderp.teutils.task;

import java.util.concurrent.TimeUnit;

import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

import io.github.kyzderp.teutils.LiteModTEUtils;
import io.github.kyzderp.teutils.TEUtils;

public class GetServerTask implements Runnable 
{
	private LiteModTEUtils mod;
	private TEUtils util;
	private int retries;

	public GetServerTask(LiteModTEUtils mod, TEUtils util, int tries)
	{
		this.mod = mod;
		this.util = util;
		this.retries = tries;
	}

	@Override
	public void run() 
	{
		String servername = this.util.getServer();
		LiteLoaderLogger.info("[TE Utils] Current server: " + servername);
		
		if (servername.equals("login"))
		{
			this.mod.setSchedulerActive(true);
			return;
		}

		if (servername.equals("") || !this.util.getScriptHolder().scripts.containsKey(servername))
		{
			// Didn't find the servername, retry
			if (this.retries > 0)
			{
				LiteLoaderLogger.info("[TE Utils] Could not get server name, retrying "
						+ this.retries + " more times...");
				LiteModTEUtils.scheduler.schedule(new GetServerTask(this.mod, this.util, this.retries - 1)
				, 1000, TimeUnit.MILLISECONDS);
			}
			else
				this.mod.setSchedulerActive(true);
			return;
		}

		this.util.setLastServer(this.util.getCurrentServer());
		this.util.setCurrentServer(servername);

		// Now that we know which server it is, run the script.
		this.util.runScript(servername);
		this.mod.setSchedulerActive(true);
	}

}
