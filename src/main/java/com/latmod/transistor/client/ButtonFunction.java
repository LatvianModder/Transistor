package com.latmod.transistor.client;

import com.latmod.transistor.TransistorFunction;
import com.latmod.transistor.functions.TransistorFunctions;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ButtonFunction extends ButtonFunctionBase
{
	public final TransistorFunction function;

	public ButtonFunction(GuiTransistor g, int x, int y, int i)
	{
		super(g, x, y, ButtonShape.LARGE);
		function = TransistorFunctions.get(i);
	}

	@Override
	public TransistorFunction getFunction()
	{
		return function;
	}

	@Override
	public int getIndex()
	{
		return -function.index;
	}

	@Override
	public void addHoverText(List<String> text)
	{
		text.add(getFunction().getDisplayName());

		if (isLocked())
		{
			text.add(TextFormatting.GOLD + I18n.format("transistor.locked"));
		}
		else
		{
			int used = gui.data.isFunctionInUse(function);

			if (used >= 0)
			{
				String s;

				if (used < 4)
				{
					s = I18n.format("transistor.in_use.attack", used + 1);
				}
				else if (used < 12)
				{
					s = I18n.format("transistor.in_use.upgrade", (used - 4) % 2 + 1, (used - 4) / 2 + 1);
				}
				else
				{
					s = I18n.format("transistor.in_use.passive", (used - 12) + 1);
				}

				text.add(I18n.format("transistor.in_use", s));
			}
			else
			{
				text.add(TextFormatting.GRAY + I18n.format("transistor.install_function"));
			}
		}

		text.add(TextFormatting.GRAY + I18n.format("transistor.memory") + ": " + TextFormatting.GOLD + function.memory);
	}

	@Override
	public boolean isLocked()
	{
		return !gui.data.isFunctionUnlocked(function);
	}

	@Override
	public boolean isBeingUsed()
	{
		return gui.data.isFunctionInUse(function) >= 0;
	}

	@Override
	public boolean isSelected()
	{
		return gui.selectedFunction == function && !hasError();
	}

	@Override
	public boolean hasError()
	{
		return gui.selectedFunction == function && (gui.data.getUsedMemory() + function.memory) > gui.data.getMemory() || gui.data.isOverloaded(function);
	}

	@Override
	public void click()
	{
		if (gui.selectedFunction == function)
		{
			gui.selectedFunction = TransistorFunctions.EMPTY;
		}
		else if (!isBeingUsed() && !isLocked())
		{
			gui.selectedFunction = function;
		}
	}
}