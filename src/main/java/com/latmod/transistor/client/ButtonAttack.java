package com.latmod.transistor.client;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ButtonAttack extends ButtonFunctionBase
{
	private final int index;

	public ButtonAttack(GuiTransistor g, int x, int y, int i)
	{
		super(g, x, y, ButtonShape.LARGE);
		index = i;
	}

	@Override
	public void addHoverText(List<String> text)
	{
		if (!getFunction().isEmpty())
		{
			text.add(getFunction().getDisplayName());
		}

		if (!getFunction().isEmpty())
		{
			text.add(TextFormatting.GRAY + I18n.format("transistor.uninstall_function"));
		}
	}

	@Override
	public int getIndex()
	{
		return index;
	}

	@Override
	public boolean isBeingUsed()
	{
		return gui.data.getSelected() == index;
	}
}