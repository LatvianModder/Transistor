package com.latmod.transistor.client;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * @author LatvianModder
 */
public class BarXP extends Bar
{
	public BarXP(GuiTransistor g, int _x, int _y)
	{
		super(g, _x, _y);
	}

	@Override
	public void addHoverText(List<String> text)
	{
		text.add(TextFormatting.GREEN + I18n.format("transistor.xp"));
		text.add(gui.data.xp + " / " + gui.data.getNextLevelXP());
		text.add(I18n.format("transistor.available_points") + ": " + (gui.data.points > 0 ? TextFormatting.GREEN : TextFormatting.RED) + gui.data.points);
	}

	@Override
	public int getValue()
	{
		return gui.data.xp;
	}

	@Override
	public int getMaxValue()
	{
		return gui.data.getNextLevelXP();
	}

	@Override
	public int getBarColor(int index, int bars, int actualBars)
	{
		return GREEN;
	}
}
