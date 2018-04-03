package com.latmod.transistor.functions;

import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorFunction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class FunctionBreach extends TransistorFunction
{
	public FunctionBreach(int index, String n, int m, TextFormatting c)
	{
		super(index, n, m, c);
	}

	@Override
	public boolean onAttack(TransistorData data, EntityPlayer player)
	{
		if (!player.world.isRemote)
		{
			EntityArrow arrow;

			if (data.hasUpgrade(TransistorFunctions.MASK))
			{
				arrow = new EntitySpectralArrow(player.world, player);
			}
			else
			{
				arrow = new EntityTippedArrow(player.world, player);
				((EntityTippedArrow) arrow).setPotionEffect(new ItemStack(Items.ARROW));
			}

			arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0F, 7F, 0F);
			arrow.setIsCritical(true);
			arrow.setDamage(1D);
			arrow.setKnockbackStrength(1);
			NBTTagCompound nbt = new NBTTagCompound();
			arrow.writeEntityToNBT(nbt);
			nbt.setInteger("life", 1199);
			arrow.readEntityFromNBT(nbt);
			arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
			player.world.spawnEntity(arrow);
		}

		return true;
	}
}