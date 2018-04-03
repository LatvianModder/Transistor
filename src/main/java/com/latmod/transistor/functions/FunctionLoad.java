package com.latmod.transistor.functions;

import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorFunction;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class FunctionLoad extends TransistorFunction
{
	public FunctionLoad(int index, String n, int m, TextFormatting c)
	{
		super(index, n, m, c);
	}

	@Override
	public boolean onAttack(TransistorData data, EntityPlayer player)
	{
		if (!player.world.isRemote && data.useEnergy(player.world, 1000))
		{
			EntityEnderCrystal entity = new EntityEnderCrystal(player.world);
			double myawr = Math.toRadians(-player.rotationYaw);
			double mpitchr = Math.toRadians(-player.rotationPitch);
			double f4 = -Math.cos(mpitchr);
			double a = 2D;
			entity.setPosition(player.posX + Math.sin(myawr - Math.PI) * f4 * a, player.posY + player.getEyeHeight() + Math.sin(mpitchr) * a, player.posZ + Math.cos(myawr - Math.PI) * f4 * a);
			entity.setShowBottom(false);
			player.world.spawnEntity(entity);
		}

		return true;
	}
}