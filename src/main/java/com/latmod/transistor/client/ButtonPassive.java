package com.latmod.transistor.client;

import com.latmod.transistor.net.MessageUnlockSlot;
import com.latmod.transistor.net.TransistorNetHandler;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ButtonPassive extends ButtonFunctionBase
{
	private final int index;

	public ButtonPassive(GuiTransistor g, int x, int y, int i)
	{
		super(g, x, y, ButtonShape.SMALL);
		index = i;
	}

	@Override
	public int getIndex()
	{
		return index + 12;
	}

	@Override
	public void click(boolean left)
	{
		if (left && isLocked() && gui.data.getPoints() >= 2)
		{
			gui.mc.displayGuiScreen(new GuiYesNo((result, id) -> {
				gui.mc.displayGuiScreen(gui);

				if (result)
				{
					if (gui.data.unlockSlot(getIndex(), true))
					{
						TransistorNetHandler.NET.sendToServer(new MessageUnlockSlot(getIndex(), gui.hand));
					}
				}
			}, I18n.format("transistor.unlock_passive_slot_q"), "", 0));
		}
		else
		{
			super.click(left);
		}
	}

	@Override
	public void addHoverText(List<String> text)
	{
		if (!getFunction().isEmpty())
		{
			text.add(getFunction().getDisplayName());
		}

		if (isLocked())
		{
			text.add(TextFormatting.GOLD + I18n.format("transistor.locked"));

			if (gui.data.getPoints() >= 2)
			{
				text.add(TextFormatting.GRAY + I18n.format("transistor.unlock_passive_slot"));
			}
		}
		else if (!getFunction().isEmpty())
		{
			text.add(TextFormatting.GRAY + I18n.format("transistor.uninstall_function"));
		}
	}

	@Override
	public boolean isLocked()
	{
		return !gui.data.isPassiveSlotUnlocked(index);
	}
}