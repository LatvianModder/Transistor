package com.latmod.transistor.client;

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