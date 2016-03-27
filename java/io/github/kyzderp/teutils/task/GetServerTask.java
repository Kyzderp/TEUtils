package io.github.kyzderp.teutils.task;

import io.github.kyzderp.teutils.LiteModTEUtils;
import io.github.kyzderp.teutils.TEUtils;

public class GetServerTask implements Runnable 
{
	private LiteModTEUtils mod;
	private TEUtils util;
	
	public GetServerTask(LiteModTEUtils mod, TEUtils util)
	{
		this.mod = mod;
		this.util = util;
	}

	@Override
	public void run() 
	{
		String servername = this.util.getServer();
		System.out.println("Current Server: " + servername);
		this.util.setLastServer(this.util.getCurrentServer());
		this.util.setCurrentServer(servername);
		
		// Now that we know which server it is, run the script.
		this.util.runScript(servername);
		this.mod.setSchedulerActive(true);
	}

}
