package com.latmod.transistor.functions;

import com.latmod.transistor.TransistorFunction;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TransistorFunctions
{
	public static final TransistorFunction EMPTY = new FunctionEmpty(-1, "empty", 0, TextFormatting.DARK_GRAY);
	public static final TransistorFunction CRASH = new FunctionCrash(0, "crash", 1, TextFormatting.YELLOW);
	public static final TransistorFunction BREACH = new FunctionBreach(1, "breach", 3, TextFormatting.AQUA);
	public static final TransistorFunction SPARK = new FunctionSpark(2, "spark", 2, TextFormatting.GOLD);
	public static final TransistorFunction JAUNT = new FunctionJaunt(3, "jaunt", 3, TextFormatting.AQUA);
	public static final TransistorFunction BOUNCE = new FunctionBounce(4, "bounce", 2, TextFormatting.GREEN);
	public static final TransistorFunction LOAD = new FunctionLoad(5, "load", 3, TextFormatting.GOLD);
	public static final TransistorFunction HELP = new FunctionHelp(6, "help", 4, TextFormatting.BLUE);
	public static final TransistorFunction MASK = new FunctionMask(7, "mask", 1, TextFormatting.DARK_PURPLE);
	public static final TransistorFunction PING = new FunctionPing(8, "ping", 1, TextFormatting.GOLD);
	public static final TransistorFunction SWITCH = new FunctionSwitch(9, "switch", 2, TextFormatting.LIGHT_PURPLE);
	public static final TransistorFunction GET = new FunctionGet(10, "get", 1, TextFormatting.GREEN);
	public static final TransistorFunction PURGE = new FunctionPurge(11, "purge", 2, TextFormatting.YELLOW);
	public static final TransistorFunction FLOOD = new FunctionFlood(12, "flood", 3, TextFormatting.DARK_GREEN);
	public static final TransistorFunction CULL = new FunctionCull(13, "cull", 4, TextFormatting.GOLD);
	public static final TransistorFunction TAP = new FunctionTap(14, "tap", 4, TextFormatting.DARK_GREEN);
	public static final TransistorFunction VOID = new FunctionVoid(15, "void", 4, TextFormatting.DARK_RED);

	private static final List<TransistorFunction> LIST = Collections.unmodifiableList(Arrays.asList(CRASH, BREACH, SPARK, JAUNT, BOUNCE, LOAD, HELP, MASK, PING, SWITCH, GET, PURGE, FLOOD, CULL, TAP, VOID));
	private static final HashMap<String, TransistorFunction> MAP = new HashMap<>();

	public static void init()
	{
		for (TransistorFunction function : LIST)
		{
			MAP.put(function.toString(), function);
		}
	}

	public static List<TransistorFunction> getAll()
	{
		return LIST;
	}

	public static TransistorFunction get(String name)
	{
		TransistorFunction function = name.isEmpty() ? null : MAP.get(name);
		return function == null ? EMPTY : function;
	}

	public static TransistorFunction get(int index)
	{
		return index < 0 || index >= 16 ? EMPTY : LIST.get(index);
	}
}