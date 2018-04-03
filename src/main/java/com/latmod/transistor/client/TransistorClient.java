package com.latmod.transistor.client;

import com.latmod.transistor.TransistorCommon;
import com.latmod.transistor.TransistorData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;

/**
 * @author LatvianModder
 */
public class TransistorClient extends TransistorCommon
{
	@Override
	public void openGui(TransistorData data, EnumHand hand)
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFunction(data, hand));
	}
}