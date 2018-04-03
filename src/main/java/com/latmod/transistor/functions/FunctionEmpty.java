package com.latmod.transistor.functions;

import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorFunction;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class FunctionEmpty extends TransistorFunction
{
	public FunctionEmpty(int i, String n, int m, TextFormatting c)
	{
		super(i, n, m, c);
	}

	@Override
	public boolean onAttack(TransistorData data, EntityPlayer player)
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDisplayName()
	{
		return I18n.format("transistor.function.empty");
	}
}