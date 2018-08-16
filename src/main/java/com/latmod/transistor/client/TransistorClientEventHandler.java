package com.latmod.transistor.client;

import com.latmod.transistor.Transistor;
import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = Transistor.MOD_ID, value = Side.CLIENT)
public class TransistorClientEventHandler
{
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		ModelLoader.setCustomModelResourceLocation(TransistorItems.TRANSISTOR, 0, new ModelResourceLocation(TransistorItems.TRANSISTOR.getRegistryName(), "inventory"));
	}

	@SubscribeEvent
	public static void onWorldRender(RenderWorldLastEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

		if (stack.getItem() != TransistorItems.TRANSISTOR)
		{
			stack = player.getHeldItem(EnumHand.OFF_HAND);
		}

		if (stack.getItem() == TransistorItems.TRANSISTOR)
		{
			TransistorData data = TransistorData.get(stack);
			data.getSelectedAttack().onWorldRender(data, event.getPartialTicks());
		}
	}
}