package com.latmod.transistor;

import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class TransistorFunctions
{
	public static final TransistorFunction CRASH = new TransistorFunction("crash", 1, TextFormatting.YELLOW);
	public static final TransistorFunction BREACH = new TransistorFunction("breach", 3, TextFormatting.BLUE);
	public static final TransistorFunction SPARK = new TransistorFunction("spark", 2, TextFormatting.GOLD);
	public static final TransistorFunction JAUNT = new TransistorFunction("jaunt", 3, TextFormatting.AQUA);
	public static final TransistorFunction BOUNCE = new TransistorFunction("bounce", 2, TextFormatting.GREEN);
	public static final TransistorFunction LOAD = new TransistorFunction("load", 3, TextFormatting.GOLD);
	public static final TransistorFunction HELP = new TransistorFunction("help", 4, TextFormatting.BLUE);
	public static final TransistorFunction MASK = new TransistorFunction("mask", 1, TextFormatting.DARK_PURPLE);
	public static final TransistorFunction PING = new TransistorFunction("ping", 1, TextFormatting.GOLD);
	public static final TransistorFunction SWITCH = new TransistorFunction("switch", 2, TextFormatting.LIGHT_PURPLE);
	public static final TransistorFunction GET = new TransistorFunction("get", 1, TextFormatting.GREEN);
	public static final TransistorFunction PURGE = new TransistorFunction("purge", 2, TextFormatting.YELLOW);
	public static final TransistorFunction FLOOD = new TransistorFunction("flood", 3, TextFormatting.BLUE);
	public static final TransistorFunction CULL = new TransistorFunction("cull", 4, TextFormatting.GOLD);
	public static final TransistorFunction TAP = new TransistorFunction("tap", 4, TextFormatting.RED);
	public static final TransistorFunction VOID = new TransistorFunction("void", 4, TextFormatting.DARK_RED);
	public static final TransistorFunction CRACK = new TransistorFunction("crack", 1, TextFormatting.DARK_GREEN);

	public static void registerAll()
	{
		TransistorFunction.register(CRASH);
		TransistorFunction.register(BREACH);
		TransistorFunction.register(SPARK);
		TransistorFunction.register(JAUNT);
		TransistorFunction.register(BOUNCE);
		TransistorFunction.register(LOAD);
		TransistorFunction.register(HELP);
		TransistorFunction.register(MASK);
		TransistorFunction.register(PING);
		TransistorFunction.register(SWITCH);
		TransistorFunction.register(GET);
		TransistorFunction.register(PURGE);
		TransistorFunction.register(FLOOD);
		TransistorFunction.register(CULL);
		TransistorFunction.register(TAP);
		TransistorFunction.register(VOID);
		TransistorFunction.register(CRACK);
	}
}