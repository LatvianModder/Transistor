package com.latmod.transistor.functions;

import com.google.common.collect.Multimap;
import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorFunction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class FunctionCrash extends TransistorFunction
{
	private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	private static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

	public FunctionCrash(int index, String n, int m, TextFormatting c)
	{
		super(index, n, m, c);
	}

	@Override
	public String getEffect(TransistorFunction upgrade)
	{
		if (upgrade == TransistorFunctions.PURGE)
		{
			return "transistor.effect.poison";
		}
		else if (upgrade == TransistorFunctions.VOID)
		{
			return "transistor.effect.wither_weakness";
		}
		else if (upgrade == TransistorFunctions.SPARK)
		{
			return "transistor.effect.damage";
		}
		else if (upgrade == TransistorFunctions.PING)
		{
			return "transistor.effect.speed";
		}

		return "";
	}

	@Override
	public void onPassiveUpdate(TransistorData data, EntityPlayer player, boolean isSelected)
	{
		if (data.getTick(player.world) % 60L == 0L)
		{
			player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 60, 3, true, false));
			player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 60, 1, true, false));
		}
	}

	@Override
	public boolean onAttack(TransistorData data, EntityPlayer player)
	{
		return false;
	}

	@Override
	public boolean hitEntity(TransistorData data, EntityPlayer player, EntityLivingBase target)
	{
		data.useEnergy(player.world, 100);

		if (data.hasUpgrade(TransistorFunctions.PURGE))
		{
			target.addPotionEffect(new PotionEffect(MobEffects.POISON, 100));
		}

		if (data.hasUpgrade(TransistorFunctions.VOID))
		{
			target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100));
		}

		int xp = 7;

		if (data.hasUpgrade(TransistorFunctions.SPARK))
		{
			xp *= 2;
		}

		if (data.hasUpgrade(TransistorFunctions.PING))
		{
			xp *= 0.6D;
		}

		data.addXP(player.world, xp);
		return true;
	}

	@Override
	public void getAttributeModifiers(TransistorData data, Multimap<String, AttributeModifier> map)
	{
		if (!data.canUseEnergy(100))
		{
			return;
		}

		double damage = 7D;
		double speed = 2.4D;

		if (data.hasUpgrade(TransistorFunctions.SPARK))
		{
			damage *= 2D;
			speed *= 0.6D;
		}

		if (data.hasUpgrade(TransistorFunctions.PING))
		{
			damage *= 0.6D;
			speed *= 2D;
		}

		map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage, 0));
		map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", speed, 0));
	}

	private boolean cheapMaterial(IBlockState state)
	{
		Material m = state.getMaterial();
		return m == Material.PLANTS || m == Material.VINE || m == Material.LEAVES;
	}

	@Override
	public boolean canHarvestBlock(TransistorData data, IBlockState state)
	{
		return data.canUseEnergy(cheapMaterial(state) ? 10 : 100);
	}

	@Override
	public float getBlockDestroySpeed(TransistorData data, IBlockState state)
	{
		if (!data.canUseEnergy(cheapMaterial(state) ? 10 : 100))
		{
			return 0F;
		}

		float speed = 6F;

		if (data.hasUpgrade(TransistorFunctions.SPARK))
		{
			speed *= 2F;
		}

		if (data.hasUpgrade(TransistorFunctions.PING))
		{
			speed *= 2F;
		}

		return speed;
	}

	@Override
	public void onBlockDestroyed(TransistorData data, IBlockState state, BlockPos pos, EntityPlayer player)
	{
		boolean cheapMaterial = cheapMaterial(state);

		if (cheapMaterial)
		{
			data.useEnergy(player.world, 10);
		}
		else
		{
			data.addXP(player.world, 1);
			data.useEnergy(player.world, 100);
		}
	}
}