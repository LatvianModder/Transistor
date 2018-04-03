package com.latmod.transistor.client;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * @author LatvianModder
 */
public class BarMemory extends Bar
{
	public BarMemory(GuiTransistor g, int _x, int _y)
	{
		super(g, _x, _y);
	}

	@Override
	public void addHoverText(List<String> text)
	{
		text.add(TextFormatting.DARK_AQUA + I18n.format("transistor.memory"));
		text.add(gui.data.getUsedMemory() + " / " + gui.data.getMemory());

		if (gui.data.getPoints() > 0 && gui.data.getMemory() < 32)
		{
			text.add(TextFormatting.GRAY + I18n.format("transistor.install_memory"));
		}
	}

	@Override
	public int getValue()
	{
		return gui.data.getUsedMemory();
	}

	@Override
	public int getMaxValue()
	{
		return gui.data.getMemory();
	}

	@Override
	public int getBars(int actualBars)
	{
		return super.getBars(actualBars);
	}

	@Override
	public int getBarColor(int index, int bars, int actualBars)
	{
		return CYAN;
	}
}