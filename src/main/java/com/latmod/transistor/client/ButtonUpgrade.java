package com.latmod.transistor.client;

import com.latmod.transistor.TransistorFunction;
import com.latmod.transistor.net.MessageUnlockSlot;
import com.latmod.transistor.net.TransistorNetHandler;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ButtonUpgrade extends ButtonFunctionBase
{
	private final int attack;
	private final int slot;

	public ButtonUpgrade(GuiTransistor g, int x, int y, int a, int s)
	{
		super(g, x, y, ButtonShape.SMALL);
		attack = a;
		slot = s;
	}

	@Override
	public int getIndex()
	{
		return 4 + attack * 2 + slot;
	}

	@Override
	public void click()
	{
		if (isLocked() && gui.data.points >= 1)
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
			}, I18n.format("transistor.unlock_upgrade_slot_q"), I18n.format("transistor.costs_1_point"), 0));
		}
		else
		{
			super.click();
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

			if (gui.data.points >= 1)
			{
				text.add(TextFormatting.GRAY + I18n.format("transistor.unlock_upgrade_slot"));
			}
		}
		else
		{
			TransistorFunction function = getFunction();

			if (function.isEmpty() && !gui.selectedFunction.isEmpty() && !gui.data.getAttack(attack).isEmpty())
			{
				function = gui.selectedFunction;
			}

			if (!function.isEmpty())
			{
				String s = gui.data.getAttack(attack).getEffect(function);

				if (s.isEmpty())
				{
					text.add(TextFormatting.DARK_GRAY + I18n.format("transistor.effect.none"));
				}
				else
				{
					text.add(I18n.format("transistor.effects") + ":");
					text.add(TextFormatting.BLUE + "+ " + I18n.format(s));
				}
			}
		}
	}

	@Override
	public boolean isLocked()
	{
		return !gui.data.isUpgradeSlotUnlocked(attack, slot);
	}
}