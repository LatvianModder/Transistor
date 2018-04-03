package com.latmod.transistor;

import com.latmod.transistor.functions.TransistorFunctions;
import com.latmod.transistor.net.TransistorNetHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class TransistorCommon
{
	public void preInit()
	{
		CapabilityManager.INSTANCE.register(TransistorData.class, new Capability.IStorage<TransistorData>()
		{
			@Nullable
			@Override
			public NBTBase writeNBT(Capability<TransistorData> capability, TransistorData instance, EnumFacing side)
			{
				return null;
			}

			@Override
			public void readNBT(Capability<TransistorData> capability, TransistorData instance, EnumFacing side, NBTBase nbt)
			{
			}
		}, () -> new TransistorData(ItemStack.EMPTY));

		TransistorNetHandler.init();
	}

	public void postInit()
	{
		TransistorFunctions.init();
	}

	public void openGui(TransistorData data, EnumHand hand)
	{
	}
}