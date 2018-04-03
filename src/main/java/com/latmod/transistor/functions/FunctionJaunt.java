package com.latmod.transistor.functions;

import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorFunction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class FunctionJaunt extends TransistorFunction
{
	public FunctionJaunt(int index, String n, int m, TextFormatting c)
	{
		super(index, n, m, c);
	}

	@Override
	public boolean onAttack(TransistorData data, EntityPlayer player)
	{
		if (player.world.isRemote || ((EntityPlayerMP) player).connection == null)
		{
			return true;
		}

		BlockPos pos = data.getCustomTempData("Jaunt_Pos");

		if (pos != null)
		{
			double x = pos.getX() + 0.5D;
			double y = pos.getY() + 0.5D;
			double z = pos.getZ() + 0.5D;
			player.fallDistance = 0F;
			player.motionX = player.motionY = player.motionZ = 0D;
			((EntityPlayerMP) player).connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
		}

		return true;
	}

	@Override
	public void onUpdate(TransistorData data, EntityPlayer player, boolean isSelected)
	{
		Vec3d vec3d = player.getPositionEyes(1F);
		double myawr = Math.toRadians(-player.rotationYaw);
		double mpitchr = Math.toRadians(-player.rotationPitch);
		double f4 = -Math.cos(mpitchr);
		double dist = 20D;
		Vec3d vec3d1 = vec3d.addVector(Math.sin(myawr - Math.PI) * f4 * dist, Math.sin(mpitchr) * dist, Math.cos(myawr - Math.PI) * f4 * dist);
		RayTraceResult result = player.world.rayTraceBlocks(vec3d, vec3d1, false, true, false);

		if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			BlockPos pos = result.getBlockPos().offset(result.sideHit);
			data.setCustomTempData("Jaunt_Pos", pos);

			if (player.world.isRemote && isSelected)
			{
				for (int i = 0; i < 8; i++)
				{
					player.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + player.world.rand.nextFloat(), pos.getY() + player.world.rand.nextFloat() * 2D, pos.getZ() + player.world.rand.nextFloat(), 0D, 0D, 0D);
				}
			}
		}
		else
		{
			data.setCustomTempData("Jaunt_Pos", null);
		}
	}
}