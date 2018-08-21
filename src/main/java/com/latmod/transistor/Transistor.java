package com.latmod.transistor;

import com.latmod.transistor.functions.TransistorFunctions;
import com.latmod.transistor.net.TransistorNetHandler;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author LatvianModder
 */
@Mod(
		modid = Transistor.MOD_ID,
		name = Transistor.MOD_NAME,
		version = Transistor.VERSION
)
public class Transistor
{
	public static final String MOD_ID = "transistor";
	public static final String MOD_NAME = "Transistor";
	public static final String VERSION = "@VERSION@";

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		CapabilityManager.INSTANCE.register(TransistorData.class, new Capability.IStorage<TransistorData>()
		{
			@Override
			public NBTBase writeNBT(Capability<TransistorData> capability, TransistorData instance, EnumFacing side)
			{
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<TransistorData> capability, TransistorData instance, EnumFacing side, NBTBase nbt)
			{
				if (nbt instanceof NBTTagCompound)
				{
					instance.deserializeNBT((NBTTagCompound) nbt);
				}
			}
		}, TransistorData::new);

		TransistorNetHandler.init();
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		TransistorFunctions.init();
	}
}