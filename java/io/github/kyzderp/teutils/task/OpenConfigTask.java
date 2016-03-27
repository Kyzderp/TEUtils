package io.github.kyzderp.teutils.task;

import io.github.kyzderp.teutils.LiteModTEUtils;
import io.github.kyzderp.teutils.TEUtils;

public class OpenConfigTask implements Runnable 
{
	private LiteModTEUtils mod;
	private TEUtils util;
	
	public OpenConfigTask(LiteModTEUtils mod, TEUtils util)
	{
		this.mod = mod;
		this.util = util;
	}

	@Override
	public void run() 
	{
		this.util.openConfig();
	}

}
