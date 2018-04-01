package com.latmod.transistor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;

/**
 * @author LatvianModder
 */
public class TransistorFunction
{
	public static final TransistorFunction EMPTY = new TransistorFunction("empty", 0, TextFormatting.DARK_GRAY);

	private static final HashMap<String, TransistorFunction> MAP = new HashMap<>();

	public static void register(TransistorFunction function)
	{
		MAP.put(function.toString(), function);
	}

	public static TransistorFunction get(String name)
	{
		TransistorFunction function = name.isEmpty() ? null : MAP.get(name);
		return function == null ? EMPTY : function;
	}

	private final String name;
	public final ITextComponent textComponent;
	public final byte memory;
	public final TextFormatting color;
	public ResourceLocation texture;

	public TransistorFunction(String n, int m, TextFormatting c)
	{
		name = n;
		memory = (byte) m;
		color = c;
		textComponent = new TextComponentTranslation("transistor.function." + name);
		texture = new ResourceLocation(Transistor.MOD_ID, "textures/functions/" + name + ".png");
	}

	public final boolean isEmpty()
	{
		return this == EMPTY;
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

	public String getDisplayName()
	{
		return color + textComponent.getUnformattedText();
	}

	public boolean onAttack(TransistorData data, EntityLivingBase living)
	{
		return true;
	}

	public void onPassiveUpdate(TransistorData data, EntityPlayer player, boolean isSelected)
	{
	}
}