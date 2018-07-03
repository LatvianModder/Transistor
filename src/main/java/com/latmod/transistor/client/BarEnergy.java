package com.latmod.transistor.client;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * @author LatvianModder
 */
public class BarEnergy extends Bar
{
	public BarEnergy(GuiTransistor g, int _x, int _y)
	{
		super(g, _x, _y);
	}

	@Override
	public void addHoverText(List<String> text)
	{
		text.add(TextFormatting.GREEN + I18n.format("transistor.energy"));
		text.add(gui.data.energy + " / " + gui.data.getMaxEnergy());

		if (gui.data.energy < gui.data.getMaxEnergy())
		{
			text.add(I18n.format("transistor.cooldown") + ": " + TextFormatting.BLUE + ((gui.data.rechargeAt - gui.mc.world.getTotalWorldTime()) / 20L + 1L) + "s");
		}
	}

	@Override
	public int getValue()
	{
		return gui.data.energy;
	}

	@Override
	public int getMaxValue()
	{
		return gui.data.getMaxEnergy();
	}

	@Override
	public int getBarColor(int index, int bars, int actualBars)
	{
		if (bars == 1)
		{
			return RED;
		}
		else if (bars < 5)
		{
			return ORANGE;
		}
		else if (bars < 9)
		{
			return YELLOW;
		}

		return GREEN;
	}
}