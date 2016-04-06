package io.github.kyzderp.teutils.task;

import io.github.kyzderp.teutils.LiteModTEUtils;
import io.github.kyzderp.teutils.TEUtils;

public class RunScriptTask implements Runnable 
{
	private TEUtils util;
	
	public RunScriptTask(TEUtils util)
	{
		this.util = util;
	}

	@Override
	public void run() 
	{
		this.util.runScript(this.util.getCurrentServer());
	}

}
