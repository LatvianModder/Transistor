package com.latmod.transistor;

import com.google.common.collect.Multimap;
import com.latmod.transistor.functions.TransistorFunctions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class TransistorFunction
{
	public final byte index;
	private final String name;
	public final String displayName;
	public final byte memory;
	public final TextFormatting color;

	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite sprite;

	public TransistorFunction(int i, String n, int m, TextFormatting c)
	{
		index = (byte) i;
		name = n;
		memory = (byte) m;
		color = c;
		displayName = Character.toUpperCase(name.charAt(0)) + name.substring(1) + "()";
	}

	public final boolean isEmpty()
	{
		return this == TransistorFunctions.EMPTY;
	}

	public final int hashCode()
	{
		return name.hashCode();
	}

	public final boolean equals(Object o)
	{
		return o == this || o != null && o.toString().equals(toString());
	}

	public final String toString()
	{
		return name;
	}

	@SideOnly(Side.CLIENT)
	public String getDisplayName()
	{
		return color + displayName;
	}

	public String getEffect(TransistorFunction upgrade)
	{
		return "";
	}

	public boolean onAttack(TransistorData data, EntityPlayer player)
	{
		return true;
	}

	public boolean hitEntity(TransistorData data, EntityPlayer player, EntityLivingBase target)
	{
		return false;
	}

	public void onUpdate(TransistorData data, EntityPlayer player, boolean isSelected)
	{
	}

	public void onPassiveUpdate(TransistorData data, EntityPlayer player, boolean isSelected)
	{
	}

	@SideOnly(Side.CLIENT)
	public void onWorldRender(TransistorData data, float partialTicks)
	{
	}

	public void getAttributeModifiers(TransistorData data, Multimap<String, AttributeModifier> map)
	{
	}

	public boolean canHarvestBlock(TransistorData data, IBlockState state)
	{
		return false;
	}

	public float getBlockDestroySpeed(TransistorData data, IBlockState state)
	{
		return 0F;
	}

	public void onBlockDestroyed(TransistorData data, IBlockState state, BlockPos pos, EntityPlayer player)
	{
	}
}