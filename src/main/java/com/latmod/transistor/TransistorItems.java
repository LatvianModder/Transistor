package com.latmod.transistor;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = Transistor.MOD_ID)
@GameRegistry.ObjectHolder(Transistor.MOD_ID)
public class TransistorItems
{
	public static final Item TRANSISTOR = Items.AIR;
}