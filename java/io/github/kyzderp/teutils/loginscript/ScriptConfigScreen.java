package io.github.kyzderp.teutils.loginscript;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class ScriptConfigScreen extends GuiScreen
{
	private ScriptHolder scriptHolder;

	private int currServer;

	private GuiTextField delayField;
	private GuiTextField scriptField;

	private final String[] servers = {"Spawn", "Build1", "Build2", "Build3", "Build4", "Gaia", "Nether", "Mine"};

	public ScriptConfigScreen(ScriptHolder scriptHolder)
	{
		super();
		this.scriptHolder = scriptHolder;
		this.currServer = 1;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		// Save button
		GuiButton save = new GuiButton(0, this.width/2 - 25, this.height/2 + 50, 50, 20, "Save");
		this.buttonList.add(save);
		save.enabled = false;

		// Server buttons
		int i = 1;
		int serverWidth = this.scriptHolder.scripts.size() * 50;
		for (String name: this.servers)
		{
			GuiButton button = new GuiButton(i, this.width/2 - serverWidth/2 + i * 50 - 50, 
					this.height/2 - 40, 48, 20, name);
			this.buttonList.add(button);
			if (this.currServer == i)
				button.enabled = false;
			i++;
		}

		LoginScript currentScript = this.scriptHolder.scripts.get(this.servers[this.currServer - 1].toLowerCase());

		// Delay text field
		this.delayField = new GuiTextField(0, this.fontRendererObj, 150, this.height/2 - 10, 38, 15);
		this.delayField.setMaxStringLength(5);
		this.delayField.setText(currentScript.getDelayMilliseconds() + "");
		this.delayField.setCanLoseFocus(true);

		// Script text field
		this.scriptField = new GuiTextField(1, this.fontRendererObj, 15, this.height/2 + 30, this.width - 30, 15);
		this.scriptField.setMaxStringLength(500);
		this.scriptField.setText(currentScript.getChatLines());
		this.scriptField.setCanLoseFocus(true);
		this.scriptField.setFocused(true);
	}


	@Override
	public void drawScreen(int mouseX, int mouseY, float f)
	{
		this.drawDefaultBackground();

		if (this.delayField != null)
			this.delayField.drawTextBox();
		if (this.scriptField != null)
			this.scriptField.drawTextBox();
		
		this.drawCenteredString(this.fontRendererObj, "TE Utils - Login Script Configuration", 
				this.width/2, this.height/2 - 60, 0x99FF99);
		this.fontRendererObj.drawStringWithShadow("Delay between messages:", 15, this.height/2 - 7, 0xFFFFFF);
		this.fontRendererObj.drawStringWithShadow("milliseconds", 195, this.height/2 - 7, 0xFFFFFF);
		
		this.fontRendererObj.drawStringWithShadow("Commands or chat lines separated by | (vertical bar):", 
				15, this.height/2 + 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, f);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		// Save
		if (button.id == 0)
		{
			LoginScript s = this.scriptHolder.scripts.get(this.servers[this.currServer - 1].toLowerCase());
			int delay = Integer.parseInt(this.delayField.getText());
			s.setDelayMilliseconds(delay);
			s.setChatLines(this.scriptField.getText());
			this.scriptHolder.saveScripts();
			button.enabled = false;
			// TODO: error for not int
		}

		// Switch a server
		else
		{
			this.currServer = button.id;
			for (int i = 0; i < this.servers.length; i++)
				((GuiButton)this.buttonList.get(i + 1)).enabled = true;
			button.enabled = false;
			LoginScript s = this.scriptHolder.scripts.get(this.servers[this.currServer - 1].toLowerCase());
			this.delayField.setText(s.getDelayMilliseconds() + "");
			this.scriptField.setText(s.getChatLines());
			this.scriptField.setFocused(true);
		}

		this.updateScreen();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.delayField.mouseClicked(mouseX, mouseY, mouseButton);
		this.scriptField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);
		if (this.delayField.textboxKeyTyped(typedChar, keyCode)
				|| this.scriptField.textboxKeyTyped(typedChar, keyCode))
				((GuiButton)this.buttonList.get(0)).enabled = true;
	}


	@Override
	public void updateScreen()
	{
		super.updateScreen();
	}
}
