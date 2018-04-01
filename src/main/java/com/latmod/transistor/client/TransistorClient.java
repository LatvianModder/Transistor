package com.latmod.transistor.client;

import com.latmod.transistor.TransistorCommon;
import com.latmod.transistor.TransistorData;
import net.minecraft.client.Minecraft;

/**
 * @author LatvianModder
 */
public class TransistorClient extends TransistorCommon
{
	@Override
	public void openGui(TransistorData data)
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFunction(data));
	}
}