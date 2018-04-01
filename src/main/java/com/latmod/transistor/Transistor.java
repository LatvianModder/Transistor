package com.latmod.transistor;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author LatvianModder
 */
@Mod(
		modid = Transistor.MOD_ID,
		name = Transistor.MOD_NAME,
		version = Transistor.VERSION,
		acceptedMinecraftVersions = "[1.12,)"
)
public class Transistor
{
	public static final String MOD_ID = "transistor";
	public static final String MOD_NAME = "Transistor";
	public static final String VERSION = "@VERSION@";

	@SidedProxy(serverSide = "com.latmod.transistor.TransistorCommon", clientSide = "com.latmod.transistor.client.TransistorClient")
	public static TransistorCommon PROXY;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		PROXY.preInit();
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		PROXY.postInit();
	}
}