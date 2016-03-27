package io.github.kyzderp.teutils.loginscript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

public class ScriptHolder 
{
	public Map<String, LoginScript> scripts;
	
	private final File dirs = new File(Minecraft.getMinecraft().mcDataDir, "liteconfig" + File.separator 
			+ "config.1.8" + File.separator + "TEUtils");
	private final File file = new File(dirs.getPath() + File.separator + "scripts.json");
	
	public ScriptHolder()
	{
		this.scripts = new HashMap<String, LoginScript>();
		this.dirs.mkdirs();
	}
	
	/**
	 * Load the scripts from file
	 */
	public void loadScripts()
	{
		if (this.file.exists())
		{
			try
			{
				BufferedReader in = new BufferedReader(new FileReader(this.file));
				LoginScript[] scriptArray = (LoginScript[])new Gson().fromJson(in, LoginScript[].class);
				if (scriptArray == null) throw new Exception("Null scripts");
				for (int i = 0; i < scriptArray.length; i++) 
				{ 
					LoginScript s = scriptArray[i];
					if (s == null) throw new Exception("Null script");
					this.scripts.put(s.getServername(), s);
				}
				in.close();
			}
			catch (Exception e)
			{
				LiteLoaderLogger.warning("Cannot read from TE Utils scripts file!");
			}
		}
		else
		{
			try {
				String[] servers = {"spawn", "build1", "build2", "build3", "build4", "gaia", "nether", "mine"};
				for (String server: servers)
					this.scripts.put(server, new LoginScript(server));
				this.file.createNewFile();
			} catch (IOException e) {
				LiteLoaderLogger.warning("Cannot create new TE Utils scripts file!");
			}
		}
	}
	
	public void saveScripts()
	{
		LoginScript[] scriptArray = (LoginScript[])this.scripts.values().toArray(new LoginScript[0]);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(scriptArray, LoginScript[].class);

		try
		{
			FileWriter out = new FileWriter(this.file);
			out.write(json);
			out.close();
		} catch (IOException e) {
			 LiteLoaderLogger.warning("Failed saving scripts");
		}
	}
	
	
}
