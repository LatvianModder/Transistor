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
		text.add(gui.data.getXP() + " / " + gui.data.getNextLevelXP());
		int points = gui.data.getPoints();
		text.add(I18n.format("transistor.available_points") + ": " + (points > 0 ? TextFormatting.GREEN : TextFormatting.RED) + points);
	}

	@Override
	public int getValue()
	{
		return gui.data.getXP();
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
